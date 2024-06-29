package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.GroupController;
import cn.lunadeer.dominion.tuis.dominion.manage.group.GroupList;
import cn.lunadeer.dominion.tuis.dominion.manage.group.GroupSetting;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class Group {

    /**
     * /dominion create_group <领地名称> <权限组名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void createGroup(CommandSender sender, String[] args) {
        if (args.length < 3) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        String dominionName = args[1];
        String groupName = args[2];
        GroupController.createGroup(operator, dominionName, groupName);
        String[] newArgs = new String[2];
        newArgs[0] = "group_list";
        newArgs[1] = dominionName;
        GroupList.show(sender, newArgs);
    }

    /**
     * /dominion delete_group <领地名称> <权限组名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void deleteGroup(CommandSender sender, String[] args) {
        if (args.length < 3) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        String dominionName = args[1];
        String groupName = args[2];
        GroupController.deleteGroup(operator, dominionName, groupName);
        String[] newArgs = new String[2];
        newArgs[0] = "group_list";
        newArgs[1] = dominionName;
        GroupList.show(sender, newArgs);
    }

    /**
     * /dominion rename_group <领地名称> <权限组旧名称> <新名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void renameGroup(CommandSender sender, String[] args) {
        if (args.length < 4) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        String dominionName = args[1];
        String oldGroupName = args[2];
        String newGroupName = args[3];
        GroupController.renameGroup(operator, dominionName, oldGroupName, newGroupName);
        String[] newArgs = new String[3];
        newArgs[0] = "group_manage";
        newArgs[1] = dominionName;
        newArgs[2] = newGroupName;
        GroupSetting.show(sender, newArgs);
    }

    /**
     * /dominion set_group_flag <领地名称> <权限组名称> <权限名称> <true|false>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setGroupFlag(CommandSender sender, String[] args) {
        if (args.length < 5) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        String dominionName = args[1];
        String groupName = args[2];
        String flag = args[3];
        boolean value = Boolean.parseBoolean(args[4]);
        GroupController.setGroupFlag(operator, dominionName, groupName, flag, value);
        String[] newArgs = new String[4];
        newArgs[0] = "group_manage";
        newArgs[1] = dominionName;
        newArgs[2] = groupName;
        newArgs[3] = String.valueOf(getPage(args, 5));
        GroupSetting.show(sender, newArgs);
    }

    /**
     * /dominion group_add_member <领地名称> <权限组名称> <玩家名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void addMember(CommandSender sender, String[] args) {
        if (args.length < 4) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        String dominionName = args[1];
        String groupName = args[2];
        String playerName = args[3];
        GroupController.addMember(operator, dominionName, groupName, playerName);
        String[] newArgs = new String[3];
        newArgs[0] = "group_list";
        newArgs[1] = dominionName;
        newArgs[2] = String.valueOf(getPage(args, 4));
        GroupList.show(sender, newArgs);
    }

    /**
     * /dominion group_remove_member <领地名称> <权限组名称> <玩家名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void removeMember(CommandSender sender, String[] args) {
        if (args.length < 4) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        String dominionName = args[1];
        String groupName = args[2];
        String playerName = args[3];
        GroupController.removeMember(operator, dominionName, groupName, playerName);
        String[] newArgs = new String[3];
        newArgs[0] = "group_list";
        newArgs[1] = dominionName;
        newArgs[2] = String.valueOf(getPage(args, 4));
        GroupList.show(sender, newArgs);
    }

}
