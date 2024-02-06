package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        Notification.info(sender, "领地 " + dominionDTO.getName() + " 的尺寸信息：");
        Notification.info(sender, "  大小为" + (x2 - x1) + " x" + (y2 - y1) + " x" + (z2 - z1));
        Notification.info(sender, "  中心坐标为 " + (x1 + (x2 - x1) / 2) + " " + (y1 + (y2 - y1) / 2) + " " + (z1 + (z2 - z1) / 2));
        Notification.info(sender, "  高度为 " + (y2 - y1));
        Notification.info(sender, "  体积为 " + (x2 - x1) * (y2 - y1) * (z2 - z1));
        Notification.info(sender, "  领地的世界为 " + dominionDTO.getWorld());
        Notification.info(sender, "  领地的对角点坐标为 x1=" + x1 + " y1=" + y1 + " z1=" + z1 + " x2=" + x2 + " y2=" + y2 + " z2=" + z2);
    }

}
