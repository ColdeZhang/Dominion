package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.FlagsController;
import cn.lunadeer.dominion.controllers.GroupController;
import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class PrivilegeGroup {

    /**
     * 创建权限组
     * /dominion create_group <权限组名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void createGroup(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2) {
            Notification.error(sender, "用法: /dominion create_group <权限组名称>");
            return;
        }
        GroupController.create(player, args[1]);
    }

    /**
     * 删除权限组
     * /dominion delete_group <权限组名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void deleteGroup(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2) {
            Notification.error(sender, "用法: /dominion delete_group <权限组名称>");
            return;
        }
        GroupController.delete(player, args[1]);
    }

    /**
     * 设置权限组权限
     * /dominion set_group <权限组名称> <权限名称> <true/false>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setDominionFlag(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 4) {
            Notification.error(sender, "用法: /dominion set_group <权限组名称> <权限名称> <true/false>");
            return;
        }
        GroupController.setFlag(player, args[1], args[2], Boolean.parseBoolean(args[3]));
    }

    /**
     * 设置玩家在某个领地归属的权限组
     * /dominion add_player <玩家名称> <权限组名称> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void addPlayer(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 3 && args.length != 4) {
            Notification.error(sender, "用法: /dominion add_player <玩家名称> <权限组名称> [领地名称]");
            return;
        }
        if (args.length == 3) {
            GroupController.addPlayer(player, args[1], args[2]);
        } else {
            GroupController.addPlayer(player, args[1], args[2], args[3]);
        }
    }

    /**
     * 删除玩家在某个领地归属的权限组
     * /dominion remove_player <玩家名称> <权限组名称> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void removePlayer(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 3 && args.length != 4) {
            Notification.error(sender, "用法: /dominion remove_player <玩家名称> <权限组名称> [领地名称]");
            return;
        }
        if (args.length == 3) {
            GroupController.removePlayer(player, args[1], args[2]);
        } else {
            GroupController.removePlayer(player, args[1], args[2], args[3]);
        }
    }
}
