package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.dtos.PlayerDTO;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player bukkitPlayer = event.getPlayer();
        PlayerDTO player = PlayerDTO.get(bukkitPlayer);
        player.onJoin(); // update name
    }
}
