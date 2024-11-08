package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * 指令工具类
 */
public class CommandUtils {
    /**
     * 验证指令发送者是否为Player
     * @param sender 指令发送者
     * @return Player对象或者null
     */
    public static Player playerOnly(CommandSender sender) {
        if (!(sender instanceof Player)) {
            //如果发送者不是Player返回null
            Notification.error(sender, Translation.Messages_CommandPlayerOnly);
            return null;
        }
        //将sender对象强转为Player对象返回
        return (Player) sender;
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            Notification.error(sender, Translation.Messages_NoPermission, permission);
            return false;
        }
        return true;
    }

    public static Map<Integer, Location> autoPoints(Player player) {
        Integer size = Dominion.config.getAutoCreateRadius();
        Location location = player.getLocation();
        Location location1 = new Location(location.getWorld(), location.getX() - size, location.getY() - size, location.getZ() - size);
        Location location2 = new Location(location.getWorld(), location.getX() + size, location.getY() + size, location.getZ() + size);
        if (Dominion.config.getLimitVert(player)) {
            location1.setY(Dominion.config.getLimitMinY(player));
            location2.setY(Dominion.config.getLimitMaxY(player) - 1);
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
