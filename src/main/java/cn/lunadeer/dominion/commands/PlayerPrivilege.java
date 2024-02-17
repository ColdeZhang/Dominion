package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.tuis.DominionPrivilegeList;
import cn.lunadeer.dominion.tuis.PrivilegeInfo;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.controllers.PrivilegeController.*;

public class PlayerPrivilege {

    /**
     * 创建玩家特权
     * /dominion create_privilege <玩家名称> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void createPlayerPrivilege(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2 && args.length != 3 && args.length != 4) {
            Notification.error(sender, "用法: /dominion create_privilege <玩家名称> [领地名称]");
            return;
        }
        if (args.length == 2) {
            if (!createPrivilege(player, args[1])) {
                Notification.error(sender, "创建玩家特权失败");
                return;
            }
        } else {
            if (!createPrivilege(player, args[1], args[2])) {
                Notification.error(sender, "创建玩家特权失败");
                return;
            }
        }
        Notification.info(sender, "成功创建玩家特权 " + args[1]);
        if (args.length == 4) {
            String[] newArgs = new String[2];
            newArgs[0] = "privilege_list";
            newArgs[1] = args[2];
            DominionPrivilegeList.show(sender, newArgs);
        }
    }

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

        if (args.length == 4) {
            if (!setPrivilege(player, args[1], args[2], Boolean.parseBoolean(args[3]))) {
                Notification.error(sender, "设置玩家权限失败");
                return;
            }
        } else if (args.length == 5) {
            if (!setPrivilege(player, args[1], args[2], Boolean.parseBoolean(args[3]), args[4])) {
                Notification.error(sender, "设置玩家权限失败");
                return;
            }
        } else if (args.length == 6) {
            if (!setPrivilege(player, args[1], args[2], Boolean.parseBoolean(args[3]), args[4])) {
                Notification.error(sender, "设置玩家权限失败");
                return;
            }
            String[] newArgs = new String[4];
            newArgs[0] = "privilege_info";
            newArgs[1] = args[1];
            newArgs[2] = args[4];
            newArgs[3] = args[5];
            PrivilegeInfo.show(sender, newArgs);
            return;
        } else {
            Notification.error(sender, "用法: /dominion set_privilege <玩家名称> <权限名称> <true/false> [领地名称]");
            return;
        }
        Notification.info(sender, "设置玩家权限 " + args[1] + " 为 " + args[2]);
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
        if (args.length != 2 && args.length != 3 && args.length != 4) {
            Notification.error(sender, "用法: /dominion clear_privilege <玩家名称> [领地名称]");
            return;
        }
        if (args.length == 2) {
            if (!clearPrivilege(player, args[1])) {
                Notification.error(sender, "重置玩家权限失败");
                return;
            }
        } else {
            if (!clearPrivilege(player, args[1], args[2])) {
                Notification.error(sender, "重置玩家权限失败");
                return;
            }
        }
        Notification.info(sender, "成功清除玩家权限 " + args[1]);
        if (args.length == 4) {
            String[] newArgs = new String[3];
            newArgs[0] = "privilege_list";
            newArgs[1] = args[2];
            DominionPrivilegeList.show(sender, newArgs);
        }
    }

}
