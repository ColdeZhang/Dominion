package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.api.dtos.PlayerDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerCache extends Cache {

    private final Map<UUID, PlayerDTO> playerCache = new HashMap<>();
    private final Map<UUID, Integer> playerUsingTitleId = new HashMap<>();

    public PlayerCache() {
        List<PlayerDTO> players = cn.lunadeer.dominion.dtos.PlayerDTO.all();
        for (PlayerDTO player : players) {
            playerCache.put(player.getUuid(), player);
        }
    }

    @Override
    void loadExecution() throws Exception {

    }

    @Override
    void loadExecution(Integer idToLoad) throws Exception {

    }

    @Override
    void deleteExecution(Integer idToDelete) throws Exception {

    }

    public String getPlayerName(UUID uuid) {
        if (playerCache.containsKey(uuid)) {
            return playerCache.get(uuid).getLastKnownName();
        } else {
            return "Unknown Player: %s".formatted(uuid);
        }
    }
}
