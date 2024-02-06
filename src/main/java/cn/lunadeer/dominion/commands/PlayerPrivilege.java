package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.controllers.PrivilegeController.clearPrivilege;
import static cn.lunadeer.dominion.controllers.PrivilegeController.setPrivilege;

public class PlayerPrivilege {

    /**
     * 设置玩家权限
     * /dominion set_privilege <玩家名称> <权限名称> <true/false> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setPlayerPrivilege(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 4 && args.length != 5) {
            Notification.error(sender, "用法: /dominion set_privilege <玩家名称> <权限名称> <true/false> [领地名称]");
            return;
        }
        if (args.length == 4) {
            if (setPrivilege(player, args[1], args[2], Boolean.parseBoolean(args[3]))) {
                return;
            }
        } else {
            if (setPrivilege(player, args[1], args[2], Boolean.parseBoolean(args[3]), args[4])) {
                return;
            }
        }
        Notification.error(sender, "设置玩家权限失败");
    }

    /**
     * 重置玩家权限
     * /dominion clear_privilege <玩家名称> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void clearPlayerPrivilege(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2 && args.length != 3) {
            Notification.error(sender, "用法: /dominion clear_privilege <玩家名称> [领地名称]");
            return;
        }
        if (args.length == 2) {
            if (clearPrivilege(player, args[1])) {
                return;
            }
        } else {
            if (clearPrivilege(player, args[1], args[2])) {
                return;
            }
        }
        Notification.error(sender, "重置玩家权限失败");
    }

}
