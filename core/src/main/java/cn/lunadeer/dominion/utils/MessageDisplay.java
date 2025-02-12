package cn.lunadeer.dominion.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageDisplay {
    public enum Place {
        BOSS_BAR,
        ACTION_BAR,
        TITLE,
        SUBTITLE,
        CHAT
    }

    public static void show(Player player, Place place, String message) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = ColorParser.getBukkitType(message);
        // BOSS_BAR, ACTION_BAR, TITLE, SUBTITLE, CHAT
        if (place == Place.BOSS_BAR) {
            Notification.bossBar(player, message);
        } else if (place == Place.CHAT) {
            player.sendMessage(message);
        } else if (place == Place.TITLE) {
            Notification.title(player, message);
        } else if (place == Place.SUBTITLE) {
            Notification.subTitle(player, message);
        } else {
            Notification.actionBar(player, message);
        }
    }

}
