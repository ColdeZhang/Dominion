package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.PlayerDTO;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PlayerController {

    public static PlayerDTO getPlayerDTO(String playerName) {
        return PlayerDTO.select(playerName);
    }

    public static PlayerDTO getPlayerDTO(UUID uuid) {
        return PlayerDTO.select(uuid);
    }

    public static List<PlayerDTO> searchPlayer(String playerName) {
        return PlayerDTO.search(playerName);
    }

    public static List<PlayerDTO> allPlayers() {
        List<PlayerDTO> players = PlayerDTO.all();
        // 按照名字排序
        players.sort(Comparator.comparing(PlayerDTO::getLastKnownName));
        return players;
    }
}
