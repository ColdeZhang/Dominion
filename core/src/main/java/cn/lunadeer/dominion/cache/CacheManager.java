package cn.lunadeer.dominion.cache;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.cache.server.DominionCache;
import cn.lunadeer.dominion.cache.server.PlayerCache;
import cn.lunadeer.dominion.cache.server.ResidenceDataCache;
import cn.lunadeer.dominion.cache.server.ServerCache;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.events.PlayerCrossDominionBorderEvent;
import cn.lunadeer.dominion.events.PlayerMoveInDominionEvent;
import cn.lunadeer.dominion.events.PlayerMoveOutDominionEvent;
import cn.lunadeer.dominion.handler.CacheEventHandler;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.utils.AutoTimer;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static cn.lunadeer.dominion.misc.Others.*;

/**
 * Manages the cache for the server and other servers.
 */
public class CacheManager {
    private final ServerCache thisServerCache;
    private final Map<Integer, ServerCache> otherServerCaches;
    private final PlayerCache playerCache;
    private final ResidenceDataCache residenceDataCache = new ResidenceDataCache();

    private boolean recheckPlayerStatus = false;
    private final Map<UUID, Integer> playerCurrentDominionId = new HashMap<>();

    public static CacheManager instance;

    public static final Long UPDATE_INTERVAL = 1000 * 4L;

    /**
     * Constructs a CacheManager and initializes the server cache.
     */
    public CacheManager() {
        instance = this;
        this.thisServerCache = new ServerCache(Configuration.multiServer.serverId);
        this.thisServerCache.getDominionCache().load();
        this.thisServerCache.getMemberCache().load();
        this.thisServerCache.getGroupCache().load();

        this.otherServerCaches = new HashMap<>();

        this.playerCache = new PlayerCache();
        this.playerCache.load();

        Bukkit.getPluginManager().registerEvents(new CacheEventHandler(), Dominion.instance);
    }

    /**
     * Gets the cache for this server.
     *
     * @return the server cache
     */
    public ServerCache getCache() {
        return thisServerCache;
    }

    /**
     * Gets the caches for other servers.
     *
     * @return a map of server IDs to server caches
     */
    public Map<Integer, ServerCache> getOtherServerCaches() {
        return otherServerCaches;
    }

    public @Nullable ServerCache getCache(Integer id) {
        if (thisServerCache.getServerId().equals(id)) {
            return thisServerCache;
        }
        return otherServerCaches.get(id);
    }

    public void addServerCache(@NotNull Integer serverId) {
        ServerCache serverCache = new ServerCache(serverId);
        otherServerCaches.put(serverId, serverCache);
    }

    /**
     * Reloads the cache for this server.
     */
    public void reloadCache() {
        thisServerCache.getDominionCache().load();
        thisServerCache.getMemberCache().load();
        thisServerCache.getGroupCache().load();
    }

    /**
     * Reloads the cache for a specific server.
     * <p>
     * This is an asynchronous operation.
     *
     * @param serverId the ID of the server whose cache should be reloaded
     */
    public void reloadServerCache(@NotNull Integer serverId) {
        CompletableFuture.runAsync(() -> {
            if (!otherServerCaches.containsKey(serverId)) {
                XLogger.debug("Server cache not found for serverId: {0}", serverId);
                return;
            }
            otherServerCaches.get(serverId).getDominionCache().load();
            otherServerCaches.get(serverId).getMemberCache().load();
            otherServerCaches.get(serverId).getGroupCache().load();
        });
    }

    /**
     * Checks if a recheck of player status is needed.
     *
     * @return true if a recheck is needed, false otherwise
     */
    public boolean needRecheckPlayerStatus() {
        if (recheckPlayerStatus) {
            recheckPlayerStatus = false;
            return true;
        }
        return false;
    }

    /**
     * Sets the flag to recheck player status.
     */
    public void recheckPlayerStatus() {
        this.recheckPlayerStatus = true;
    }

    public @Nullable DominionDTO getPlayerCurrentDominion(@NotNull Player player) {
        try (AutoTimer ignored = new AutoTimer(Configuration.timer)) {
            Integer last_in_dom_id = playerCurrentDominionId.get(player.getUniqueId());
            DominionDTO last_dominion = null;
            DominionCache cache = thisServerCache.getDominionCache();
            if (last_in_dom_id != null) {
                last_dominion = cache.getDominion(last_in_dom_id);
            }

            // if player still in the same dominion, and the dominion has no children
            // we don't need to check again and return the last dominion directly
            if (isInDominion(last_dominion, player.getLocation())
                    && cache.getChildrenOf(last_in_dom_id).isEmpty()) {
                checkPlayerStates(player, last_dominion);   // check player states
                return last_dominion;
            }

            // or get the current dominion
            DominionDTO current_dominion = cache.getDominion(player.getLocation());
            int last_dom_id = last_dominion == null ? -1 : last_dominion.getId();
            int current_dom_id = current_dominion == null ? -1 : current_dominion.getId();

            // if last and current dominion are the same, return last dominion
            if (last_dom_id == current_dom_id) {
                checkPlayerStates(player, last_dominion);   // check player states
                return last_dominion;
            }

            // if last and current dominion are different, trigger player cross dominion border event
            new PlayerCrossDominionBorderEvent(player, last_dominion, current_dominion).call();
            checkPlayerStates(player, current_dominion);   // check player states

            // if last dominion is not null, trigger player move out dominion event
            if (last_dom_id != -1) {
                new PlayerMoveOutDominionEvent(player, last_dominion).call();
            }
            // if current dominion is not null, trigger player move in dominion event
            if (current_dom_id != -1) {
                new PlayerMoveInDominionEvent(player, current_dominion).call();
            }
            // update player current dominion id
            if (current_dominion == null) {
                playerCurrentDominionId.put(player.getUniqueId(), null);
                return null;
            } else {
                playerCurrentDominionId.put(player.getUniqueId(), current_dominion.getId());
                return current_dominion;
            }
        }
    }

    private void checkPlayerStates(@NotNull Player player, @Nullable DominionDTO dominion) {
        if (!needRecheckPlayerStatus()) return;
        flyOrNot(player, dominion);
        lightOrNot(player, dominion);
    }

    public void updatePlayerName(@NotNull Player bukkitPlayer) {
        PlayerDTO player = playerCache.getPlayer(bukkitPlayer.getUniqueId());
        if (player != null) {
            player.updateLastKnownName(bukkitPlayer.getName());
        } else {
            cn.lunadeer.dominion.dtos.PlayerDTO.create(bukkitPlayer);
        }
    }

    public @Nullable PlayerDTO getPlayer(String name) {
        return playerCache.getPlayer(name);
    }

    public @Nullable PlayerDTO getPlayer(@NotNull UUID player) {
        return playerCache.getPlayer(player);
    }

    public @NotNull String getPlayerName(@NotNull UUID uuid) {
        return playerCache.getPlayerName(uuid);
    }

    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    /**
     * Retrieves the names of the dominions managed by a player.
     * <p>
     * This method retrieves the names of dominions managed by the player, including dominions where the player is an
     * admin member or admin group.
     * <p>
     * If multi-server-mode is enabled, this method will also retrieve the names of dominions managed by the player on
     * other servers.
     *
     * @param player the UUID of the player
     * @return a list of dominion names managed by the player
     */
    public List<String> getPlayerManageDominionNames(@NotNull UUID player) {
        List<DominionDTO> dominions = new ArrayList<>(thisServerCache.getDominionCache().getPlayerOwnDominionDTOs(player));
        dominions.addAll(thisServerCache.getDominionCache().getPlayerAdminDominionDTOs(player));
        // add names from other servers
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominions.addAll(serverCache.getDominionCache().getPlayerOwnDominionDTOs(player));
                dominions.addAll(serverCache.getDominionCache().getPlayerAdminDominionDTOs(player));
            }
        }
        List<String> names = new ArrayList<>(dominions.size());
        for (DominionDTO dominion : dominions) {
            names.add(dominion.getName());
        }
        return names;
    }

    /**
     * Retrieves the names of all dominions.
     * <p>
     * This method retrieves the names of all dominions, including dominions on other servers if multi-server-mode is
     * enabled.
     *
     * @return a list of all dominion names
     */
    public List<String> getAllDominionNames() {
        List<String> names = new ArrayList<>(thisServerCache.getDominionCache().getAllDominionNames());
        // add names from other servers
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                names.addAll(serverCache.getDominionCache().getAllDominionNames());
            }
        }
        return names;
    }

    public List<DominionDTO> getAllDominions() {
        List<DominionDTO> dominions = new ArrayList<>(thisServerCache.getDominionCache().getAllDominions());
        // add dominions from other servers
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominions.addAll(serverCache.getDominionCache().getAllDominions());
            }
        }
        return dominions;
    }

    public List<DominionDTO> getChildrenDominionOf(DominionDTO parent) {
        List<DominionDTO> dominions = new ArrayList<>(thisServerCache.getDominionCache().getChildrenOf(parent.getId()));
        // add dominions from other servers
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominions.addAll(serverCache.getDominionCache().getChildrenOf(parent.getId()));
            }
        }
        return dominions;
    }

    /**
     * Retrieves a DominionDTO by its ID.
     * <p>
     * This method will first attempt to retrieve the DominionDTO from the cache of this server. If the DominionDTO
     * is not found, it will then attempt to retrieve the DominionDTO from the caches of other servers.
     *
     * @param id the ID of the dominion to retrieve
     * @return the DominionDTO associated with the given ID
     * @throws DominionException if the dominion ID is not found
     */
    public @Nullable DominionDTO getDominion(Integer id) {
        DominionDTO dominion = thisServerCache.getDominionCache().getDominion(id);
        if (dominion != null) {
            return dominion;
        } else if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominion = serverCache.getDominionCache().getDominion(id);
                if (dominion != null) {
                    return dominion;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a DominionDTO by its name.
     * <p>
     * This method will first attempt to retrieve the DominionDTO from the cache of this server. If the DominionDTO
     * is not found, it will then attempt to retrieve the DominionDTO from the caches of other servers.
     *
     * @param name the name of the dominion to retrieve
     * @return the DominionDTO associated with the given name
     * @throws DominionException if the dominion name is not found
     */
    public @NotNull DominionDTO getDominion(String name) {
        DominionDTO dominion = thisServerCache.getDominionCache().getDominion(name);
        if (dominion != null) {
            return dominion;
        } else if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominion = serverCache.getDominionCache().getDominion(name);
                if (dominion != null) {
                    return dominion;
                }
            }
        }
        throw new DominionException(Language.convertsText.unknownDominion, name);
    }

    public @Nullable DominionDTO getDominion(Location location) {
        return thisServerCache.getDominionCache().getDominion(location);
    }

    public void resetPlayerCurrentDominionId(@NotNull Player player) {
        playerCurrentDominionId.remove(player.getUniqueId());
    }

    public @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull Player player) {
        return getMember(dominion, player.getUniqueId());
    }

    public @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull UUID player) {
        MemberDTO member = thisServerCache.getMemberCache().getMember(dominion, player);
        if (member != null) {
            return member;
        } else if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                member = serverCache.getMemberCache().getMember(dominion, player);
                if (member != null) {
                    return member;
                }
            }
        }
        return null;
    }

    public @Nullable GroupDTO getGroup(MemberDTO member) {
        return getGroup(member.getGroupId());
    }

    public @Nullable GroupDTO getGroup(Integer id) {
        if (id == null) return null;
        if (id == -1) return null;
        GroupDTO group = thisServerCache.getGroupCache().getGroup(id);
        if (group != null) {
            return group;
        } else if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                group = serverCache.getGroupCache().getGroup(id);
                if (group != null) {
                    return group;
                }
            }
        }
        return null;
    }

    public ResidenceDataCache getResidenceCache() {
        return residenceDataCache;
    }

    public List<DominionDTO> getPlayerOwnDominionDTOs(UUID player) {
        List<DominionDTO> dominions = new ArrayList<>(thisServerCache.getDominionCache().getPlayerOwnDominionDTOs(player));
        // add dominions from other servers
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominions.addAll(serverCache.getDominionCache().getPlayerOwnDominionDTOs(player));
            }
        }
        return dominions;
    }

    public List<DominionDTO> getPlayerAdminDominionDTOs(UUID player) {
        List<DominionDTO> dominions = new ArrayList<>(thisServerCache.getDominionCache().getPlayerAdminDominionDTOs(player));
        // add dominions from other servers
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                dominions.addAll(serverCache.getDominionCache().getPlayerAdminDominionDTOs(player));
            }
        }
        return dominions;
    }

    public Integer dominionCount() {
        int count = thisServerCache.getDominionCache().count();
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                count += serverCache.getDominionCache().count();
            }
        }
        return count;
    }

    public Integer groupCount() {
        int count = thisServerCache.getGroupCache().count();
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                count += serverCache.getGroupCache().count();
            }
        }
        return count;
    }

    public Integer memberCount() {
        int count = thisServerCache.getMemberCache().count();
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : otherServerCaches.values()) {
                count += serverCache.getMemberCache().count();
            }
        }
        return count;
    }


}