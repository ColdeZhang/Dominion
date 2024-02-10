package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.FlagsController;
import cn.lunadeer.dominion.tuis.DominionFlagInfo;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class DominionFlag {

    /**
     * 设置领地权限
     * /dominion set <权限名称> <true/false> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setDominionFlag(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length == 3) {
            if (FlagsController.setFlag(player, args[1], Boolean.parseBoolean(args[2])) == null) {
                Notification.error(sender, "设置领地权限失败");
            }
        } else if (args.length == 4) {
            if (FlagsController.setFlag(player, args[1], Boolean.parseBoolean(args[2]), args[3]) == null) {
                Notification.error(sender, "设置领地权限失败");
            }
        } else if (args.length == 5) {
            if (FlagsController.setFlag(player, args[1], Boolean.parseBoolean(args[2]), args[3]) == null) {
                Notification.error(sender, "设置领地权限失败");
            }
            String[] newArgs = new String[3];
            newArgs[0] = "flag_info";
            newArgs[1] = args[3];
            newArgs[2] = args[4];
            DominionFlagInfo.show(sender, newArgs);
            return;
        } else {
            Notification.error(sender, "用法: /dominion set <权限名称> <true/false> [领地名称]");
            return;
        }
        Notification.info(sender, "设置领地权限 " + args[1] + " 为 " + args[2]);
    }

}
