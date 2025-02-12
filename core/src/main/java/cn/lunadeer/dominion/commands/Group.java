package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.events.group.*;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.GroupList;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.GroupSetting;
import org.bukkit.command.CommandSender;

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
            DominionDTO dominion = DominionDTO.select(args[2]);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, args[2]);
                return;
            }
            String groupName = args[3];
            new GroupCreateEvent(operator, dominion, groupName).call();
            GroupList.show(sender, dominion.getName());
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
            DominionDTO dominion = DominionDTO.select(args[2]);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, args[2]);
                return;
            }
            GroupDTO group = GroupDTO.select(dominion.getId(), args[3]);
            if (group == null) {
                Notification.error(sender, Translation.Messages_GroupNotExist, dominion.getName(), args[3]);
                return;
            }
            new GroupDeleteEvent(operator, dominion, group).call();
            GroupList.show(sender, dominion.getName());
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
            DominionDTO dominion = DominionDTO.select(dominionName);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, dominionName);
                return;
            }
            GroupDTO group = GroupDTO.select(dominion.getId(), oldGroupName);
            if (group == null) {
                Notification.error(sender, Translation.Messages_GroupNotExist, dominionName, oldGroupName);
                return;
            }
            new GroupRenamedEvent(operator, group, newGroupName).call();
            GroupSetting.show(sender, dominion.getName(), newGroupName);
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
            PriFlag f = Flags.getPreFlag(flag);
            if (f == null) {
                Notification.error(sender, Translation.Messages_UnknownFlag, flag);
                return;
            }
            GroupController.setGroupFlag(operator, dominionName, groupName, f, value);
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
            DominionDTO dominion = DominionDTO.select(args[2]);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, args[2]);
                return;
            }
            PlayerDTO player = PlayerDTO.select(args[4]);
            if (player == null) {
                Notification.error(sender, Translation.Messages_PlayerNotExist, args[4]);
                return;
            }
            MemberDTO member = MemberDTO.select(player.getUuid(), dominion.getId());
            if (member == null) {
                Notification.error(sender, Translation.Messages_PlayerNotDominionMember, args[4], dominion.getName());
                return;
            }
            GroupDTO group = GroupDTO.select(dominion.getId(), args[3]);
            if (group == null) {
                Notification.error(sender, Translation.Messages_GroupNotExist, dominion.getName(), args[3]);
                return;
            }
            int page = getPage(args, 5);
            new GroupAddMemberEvent(operator, group, member).call();
            GroupList.show(sender, dominion.getName(), page);
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
            DominionDTO dominion = DominionDTO.select(args[2]);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, args[2]);
                return;
            }
            GroupDTO group = GroupDTO.select(dominion.getId(), args[3]);
            if (group == null) {
                Notification.error(sender, Translation.Messages_GroupNotExist, dominion.getName(), args[3]);
                return;
            }
            PlayerDTO player = PlayerDTO.select(args[4]);
            if (player == null) {
                Notification.error(sender, Translation.Messages_PlayerNotExist, args[4]);
                return;
            }
            MemberDTO member = MemberDTO.select(player.getUuid(), dominion.getId());
            if (member == null) {
                Notification.error(sender, Translation.Messages_PlayerNotDominionMember, args[4], dominion.getName());
                return;
            }
            int page = getPage(args, 5);
            new GroupRemoveMemberEvent(operator, group, member).call();
            GroupList.show(sender, dominion.getName(), page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
