package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.PlayerDTO;

import java.util.Comparator;
import java.util.List;

public class PlayerController {



    public static List<PlayerDTO> allPlayers() {
        List<PlayerDTO> players = PlayerDTO.all();
        // 按照名字排序
        players.sort(Comparator.comparing(PlayerDTO::getLastKnownName));
        return players;
    }
}
