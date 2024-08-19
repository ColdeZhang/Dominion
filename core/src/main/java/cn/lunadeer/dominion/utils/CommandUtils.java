package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandUtils {
    public static Player playerOnly(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Notification.error(sender, "该命令只能由玩家执行");
            return null;
        }
        return (Player) sender;
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            Notification.error(sender, "你没有 %s 权限执行此命令", permission);
            return false;
        }
        return true;
    }

    public static Map<Integer, Location> autoPoints(Player player) {
        Integer size = Dominion.config.getAutoCreateRadius();
        Location location = player.getLocation();
        Location location1 = new Location(location.getWorld(), location.getX() - size, location.getY() - size, location.getZ() - size);
        Location location2 = new Location(location.getWorld(), location.getX() + size, location.getY() + size, location.getZ() + size);
        if (Dominion.config.getLimitVert()) {
            location1.setY(Dominion.config.getLimitMinY());
            location2.setY(Dominion.config.getLimitMaxY() - 1);
        }
        Map<Integer, Location> points = new HashMap<>();
        points.put(0, location1);
        points.put(1, location2);
        Dominion.pointsSelect.put(player.getUniqueId(), points);
        return points;
    }

    public static String CommandParser(String command, Object... args) {
        return String.format(command, args);
    }

}
