package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.GroupController;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.GroupList;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.GroupSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.SelectMember;
import cn.lunadeer.minecraftpluginutils.ColorParser;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static cn.lunadeer.dominion.Commands.boolOptions;
import static cn.lunadeer.dominion.commands.Helper.*;
import static cn.lunadeer.dominion.utils.CommandUtils.hasPermission;
import static cn.lunadeer.dominion.utils.TuiUtils.getPage;

public class Group {

    /**
     * /dominion group create <领地名称> <权限组名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void createGroup(CommandSender sender, String[] args) {
        try {
            if (!hasPermission(sender, "dominion.default")) {
                return;
            }
            if (args.length < 4) {
                Notification.error(sender, Translation.Commands_Group_CreateGroupUsage);
                return;
            }
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            String dominionName = args[2];
            String groupName = args[3];
            GroupController.createGroup(operator, dominionName, ColorParser.getPlainText(groupName), groupName);
            GroupList.show(sender, dominionName);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * /dominion group delete <领地名称> <权限组名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void deleteGroup(CommandSender sender, String[] args) {
        try {
            if (!hasPermission(sender, "dominion.default")) {
                return;
            }
            if (args.length < 4) {
                Notification.error(sender, Translation.Commands_Group_DeleteGroupUsage);
                return;
            }
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            String dominionName = args[2];
            String groupName = args[3];
            GroupController.deleteGroup(operator, dominionName, groupName);
            GroupList.show(sender, dominionName);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * /dominion group rename <领地名称> <权限组旧名称> <新名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void renameGroup(CommandSender sender, String[] args) {
        try {
            if (!hasPermission(sender, "dominion.default")) {
                return;
            }
            if (args.length < 5) {
                Notification.error(sender, Translation.Commands_Group_RenameGroupUsage);
                return;
            }
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            String dominionName = args[2];
            String oldGroupName = args[3];
            String newGroupName = args[4];
            GroupController.renameGroup(operator, dominionName, oldGroupName, ColorParser.getPlainText(newGroupName), newGroupName);
            GroupSetting.show(sender, dominionName, newGroupName);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * /dominion group set_flag <领地名称> <权限组名称> <权限名称> <true|false>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setGroupFlag(CommandSender sender, String[] args) {
        try {
            if (!hasPermission(sender, "dominion.default")) {
                return;
            }
            if (args.length < 6) {
                Notification.error(sender, Translation.Commands_Group_SetGroupFlagUsage);
                return;
            }
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            String dominionName = args[2];
            String groupName = args[3];
            String flag = args[4];
            boolean value = Boolean.parseBoolean(args[5]);
            int page = getPage(args, 6);
            GroupController.setGroupFlag(operator, dominionName, groupName, flag, value);
            GroupSetting.show(sender, dominionName, groupName, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * /dominion group add_member <领地名称> <权限组名称> <玩家名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void addMember(CommandSender sender, String[] args) {
        try {
            if (!hasPermission(sender, "dominion.default")) {
                return;
            }
            if (args.length < 5) {
                Notification.error(sender, Translation.Commands_Group_AddGroupMemberUsage);
                return;
            }
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            String dominionName = args[2];
            String groupName = args[3];
            String playerName = args[4];
            int page = getPage(args, 5);
            GroupController.addMember(operator, dominionName, groupName, playerName);
            GroupList.show(sender, dominionName, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * /dominion group remove_member <领地名称> <权限组名称> <玩家名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void removeMember(CommandSender sender, String[] args) {
        try {
            if (!hasPermission(sender, "dominion.default")) {
                return;
            }
            if (args.length < 5) {
                Notification.error(sender, Translation.Commands_Group_RemoveGroupMemberUsage);
                return;
            }
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            String dominionName = args[2];
            String groupName = args[3];
            String playerName = args[4];
            int page = getPage(args, 5);
            GroupController.removeMember(operator, dominionName, groupName, playerName);
            GroupList.show(sender, dominionName, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    public static void handle(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            Notification.error(sender, Translation.Commands_Group_GroupUsage);
            return;
        }
        switch (args[1]) {
            case "create":
                createGroup(sender, args);
                break;
            case "delete":
                deleteGroup(sender, args);
                break;
            case "rename":
                renameGroup(sender, args);
                break;
            case "set_flag":
                setGroupFlag(sender, args);
                break;
            case "add_member":
                addMember(sender, args);
                break;
            case "remove_member":
                removeMember(sender, args);
                break;
            case "select_member":
                SelectMember.show(sender, args);
                break;
            case "setting":
                GroupSetting.show(sender, args);
                break;
            case "list":
                GroupList.show(sender, args);
                break;
        }
    }

    public static @Nullable List<String> handleTab(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 2) {
            return Arrays.asList("create", "delete", "rename", "set_flag", "add_member", "remove_member",
                    "select_member", "list");
        }
        if (args.length == 3) {
            switch (args[1]) {
                case "create":
                case "delete":
                case "rename":
                case "set_flag":
                case "add_member":
                case "remove_member":
                case "select_member":
                case "setting":
                case "list":
                    return playerDominions(sender);
            }
        }
        if (args.length == 4) {
            switch (args[1]) {
                case "create":
                    return Collections.singletonList(Translation.Commands_Group_NewGroupName.trans());
                case "delete":
                case "rename":
                case "set_flag":
                case "add_member":
                case "remove_member":
                case "select_member":
                case "setting":
                    return dominionGroups(args[2]);
            }
        }
        if (args.length == 5) {
            switch (args[1]) {
                case "rename":
                    return Collections.singletonList(Translation.Commands_Group_NewGroupName.trans());
                case "set_flag":
                    return playerPrivileges();
                case "remove_member":
                    return groupPlayers(args[2], args[3]);
            }
        }
        if (args.length == 6) {
            switch (args[1]) {
                case "set_flag":
                    return boolOptions();
            }
        }
        return null;
    }

}
