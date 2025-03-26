package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.doos.PlayerDOO;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerCache extends Cache {

    private ConcurrentHashMap<Integer, PlayerDTO> playerCache;
    private ConcurrentHashMap<UUID, Integer> playerIdCache;
    private ConcurrentHashMap<UUID, String> playerNameCache;
    private ConcurrentHashMap<String, Integer> playerNameToId;
    private ConcurrentHashMap<UUID, Integer> playerUsingTitleId;

    public PlayerCache() {
    }

    @Override
    void loadExecution() throws Exception {
        playerNameCache = new ConcurrentHashMap<>();
        playerNameToId = new ConcurrentHashMap<>();
        playerIdCache = new ConcurrentHashMap<>();
        playerUsingTitleId = new ConcurrentHashMap<>();
        playerCache = new ConcurrentHashMap<>();

        List<PlayerDTO> players = PlayerDOO.all();
        for (PlayerDTO player : players) {
            playerIdCache.put(player.getUuid(), player.getId());
            playerNameToId.put(player.getLastKnownName(), player.getId());
            playerCache.put(player.getId(), player);
            playerNameCache.put(player.getUuid(), player.getLastKnownName());
            playerUsingTitleId.put(player.getUuid(), player.getUsingGroupTitleID());
        }
    }

    @Override
    void loadExecution(Integer idToLoad) throws Exception {
        PlayerDTO player = playerCache.remove(idToLoad);
        if (player != null) {
            playerIdCache.remove(player.getUuid());
            playerNameCache.remove(player.getUuid());
            playerUsingTitleId.remove(player.getUuid());
            playerNameToId.remove(player.getLastKnownName());
        }
        player = PlayerDOO.selectById(idToLoad);
        if (player == null) {
            return;
        }
        playerCache.put(player.getId(), player);
        playerIdCache.put(player.getUuid(), player.getId());
        playerNameCache.put(player.getUuid(), player.getLastKnownName());
        playerUsingTitleId.put(player.getUuid(), player.getUsingGroupTitleID());
        playerNameToId.put(player.getLastKnownName(), player.getId());
    }

    @Override
    void deleteExecution(Integer idToDelete) throws Exception {
        PlayerDTO player = playerCache.remove(idToDelete);
        if (player != null) {
            playerIdCache.remove(player.getUuid());
            playerNameCache.remove(player.getUuid());
            playerUsingTitleId.remove(player.getUuid());
            playerNameToId.remove(player.getLastKnownName());
        }
    }

    public @Nullable PlayerDTO getPlayer(UUID uuid) {
        if (playerIdCache.containsKey(uuid)) {
            return playerCache.get(playerIdCache.get(uuid));
        } else {
            return null;
        }
    }

    public @Nullable PlayerDTO getPlayer(String name) {
        if (playerNameToId.containsKey(name)) {
            return playerCache.get(playerNameToId.get(name));
        } else {
            return null;
        }
    }

    public String getPlayerName(UUID uuid) {
        if (playerNameCache.containsKey(uuid)) {
            return playerNameCache.get(uuid);
        } else {
            return "Unknown Player: %s".formatted(uuid);
        }
    }

    public List<String> getPlayerNames() {
        return new ArrayList<>(playerNameCache.values());
    }

    public Integer getPlayerUsingTitleId(UUID uuid) {
        return playerUsingTitleId.getOrDefault(uuid, -1);
    }

    public List<GroupDTO> getPlayerGroupTitleList(UUID uuid) {
        List<GroupDTO> groupTitleList = new ArrayList<>();
        List<MemberDTO> playerBelongedDominionMembers = Objects.requireNonNull(CacheManager.instance.getCache()).getMemberCache().getMemberBelongedDominions(uuid);
        if (Configuration.multiServer.enable) {
            for (ServerCache serverCache : CacheManager.instance.getOtherServerCaches().values()) {
                playerBelongedDominionMembers.addAll(serverCache.getMemberCache().getMemberBelongedDominions(uuid));
            }
        }
        for (MemberDTO member : playerBelongedDominionMembers) {
            if (member.getGroupId() == -1) {
                continue;
            }
            GroupDTO group = CacheManager.instance.getGroup(member.getGroupId());
            if (group == null) {
                continue;
            }
            groupTitleList.add(group);
        }
        return groupTitleList;
    }
}
