package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.PlayerDTO;

import java.util.List;
import java.util.UUID;

public class PlayerController {

    public static PlayerDTO getPlayerDTO(String playerName) {
        return PlayerDTO.select(playerName);
    }

    public static List<PlayerDTO> searchPlayer(String playerName) {
        return PlayerDTO.search(playerName);
    }

    public static List<PlayerDTO> allPlayers() {
        return PlayerDTO.all();
    }
}
