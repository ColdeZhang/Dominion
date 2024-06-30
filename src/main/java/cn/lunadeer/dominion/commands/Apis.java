package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Apis {
    public static Player playerOnly(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Notification.error(sender, "该命令只能由玩家执行");
            return null;
        }
        return (Player) sender;
    }

    public static void sizeInfo(CommandSender sender, DominionDTO dominionDTO) {
        Integer x1 = dominionDTO.getX1();
        Integer y1 = dominionDTO.getY1();
        Integer z1 = dominionDTO.getZ1();
        Integer x2 = dominionDTO.getX2();
        Integer y2 = dominionDTO.getY2();
        Integer z2 = dominionDTO.getZ2();
        Notification.info(sender, "领地 %s 的尺寸信息：", dominionDTO.getName());
        Notification.info(sender, "  大小： %d x %d x %d", x2 - x1, y2 - y1, z2 - z1);
        Notification.info(sender, "  中心坐标： %d %d %d", x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2, z1 + (z2 - z1) / 2);
        Notification.info(sender, "  高度： %d", y2 - y1);
        Notification.info(sender, "  Y坐标范围： %d ~ %d", y1, y2);
        Notification.info(sender, "  体积： %d", (x2 - x1) * (y2 - y1) * (z2 - z1));
        Notification.info(sender, "  领地所在世界： %s", dominionDTO.getWorld());
        Notification.info(sender, "  领地的对角点坐标： x1=%d y1=%d z1=%d, x2=%d y2=%d z2=%d", x1, y1, z1, x2, y2, z2);
    }

    public static void autoPoints(Player player) {
        Integer size = Dominion.config.getAutoCreateRadius();
        Location location = player.getLocation();
        Location location1 = new Location(location.getWorld(), location.getX() - size, location.getY() - size, location.getZ() - size);
        Location location2 = new Location(location.getWorld(), location.getX() + size, location.getY() + size, location.getZ() + size);
        if (Dominion.config.getLimitVert()) {
            location1.setY(Dominion.config.getLimitMinY());
            location2.setY(Dominion.config.getLimitMaxY());
        }
        Map<Integer, Location> points = new HashMap<>();
        points.put(0, location1);
        points.put(1, location2);
        Dominion.pointsSelect.put(player.getUniqueId(), points);
    }

    public static boolean notOpOrConsole(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOp()) {
                Notification.warn(player, "你没有权限使用此命令");
                return true;
            }
        }
        return false;
    }

    public static String CommandParser(String command, Object... args) {
        return String.format(command, args);
    }

}
