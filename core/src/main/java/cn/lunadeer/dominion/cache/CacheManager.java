package cn.lunadeer.dominion.cache;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.server.DominionCache;
import cn.lunadeer.dominion.cache.server.PlayerCache;
import cn.lunadeer.dominion.cache.server.ServerCache;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.events.PlayerCrossDominionBorderEvent;
import cn.lunadeer.dominion.events.PlayerMoveInDominionEvent;
import cn.lunadeer.dominion.events.PlayerMoveOutDominionEvent;
import cn.lunadeer.dominion.handler.CacheEventHandler;
import cn.lunadeer.dominion.utils.AutoTimer;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static cn.lunadeer.dominion.misc.Others.*;

/**
 * Manages the cache for the server and other servers.
 */
public class CacheManager {
    private final ServerCache thisServerCache;
    private final Map<Integer, ServerCache> otherServerCaches;
    private final PlayerCache playerCache;

    private boolean recheckPlayerStatus = false;
    private final Map<UUID, Integer> playerCurrentDominionId = new HashMap<>();

    public static CacheManager instance;

    /**
     * Constructs a CacheManager and initializes the server cache.
     */
    public CacheManager() {
        this.thisServerCache = new ServerCache(Configuration.multiServer.serverId);
        this.otherServerCaches = new HashMap<>();
        this.playerCache = new PlayerCache();
        Bukkit.getPluginManager().registerEvents(new CacheEventHandler(), Dominion.instance);
        instance = this;
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
     *
     * @param serverId the ID of the server whose cache should be reloaded
     */
    public void reloadServerCache(@NotNull Integer serverId) {
        if (!otherServerCaches.containsKey(serverId)) {
            XLogger.debug("Server cache not found for serverId: {0}", serverId);
            return;
        }
        otherServerCaches.get(serverId).getDominionCache().load();
        otherServerCaches.get(serverId).getMemberCache().load();
        otherServerCaches.get(serverId).getGroupCache().load();
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
                    && cache.getChildrenId(last_in_dom_id).isEmpty()) {
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

    public String getPlayerName(@NotNull UUID uuid) {
        return playerCache.getPlayerName(uuid);
    }
}