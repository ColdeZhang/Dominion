package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PreFlag;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.MemberController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.events.member.MemberAddedEvent;
import cn.lunadeer.dominion.events.member.MemberRemovedEvent;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberList;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.SelectPlayer;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.SelectTemplate;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static cn.lunadeer.dominion.Commands.boolOptions;
import static cn.lunadeer.dominion.Commands.playerNames;
import static cn.lunadeer.dominion.commands.Helper.*;
import static cn.lunadeer.dominion.utils.CommandUtils.hasPermission;

public class Member {

    /**
     * 创建玩家特权
     * /dominion member add <领地名称> <玩家名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void member_add(CommandSender sender, String[] args) {
        try {
            if (!hasPermission(sender, "dominion.default")) {
                return;
            }
            if (args.length < 4) {
                Notification.error(sender, Translation.Commands_Member_DominionAddMemberUsage);
                return;
            }
            PlayerDTO playerDTO = PlayerDTO.select(args[3]);
            if (playerDTO == null) {
                Notification.error(sender, Translation.Messages_PlayerNotExist, args[3]);
                return;
            }
            DominionDTO dominion = DominionDTO.select(args[2]);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, args[2]);
                return;
            }
            MemberDTO member = MemberDTO.select(playerDTO.getUuid(), dominion.getId());
            if (member != null) {
                Notification.error(sender, Translation.Messages_PlayerAlreadyMember, args[3], args[2]);
                return;
            }
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            new MemberAddedEvent(operator, dominion, playerDTO).call();
            MemberList.show(sender, args[2]);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * 设置玩家权限
     * /dominion member set_flag <领地名称> <玩家名称> <权限名称> <true/false>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void member_set_flag(CommandSender sender, String[] args) {
        try {
            if (!hasPermission(sender, "dominion.default")) {
                return;
            }
            if (args.length < 6) {
                Notification.error(sender, Translation.Commands_Member_DominionSetFlagUsage);
                return;
            }
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            String dominionName = args[2];
            String playerName = args[3];
            String flagName = args[4];
            boolean flagValue = Boolean.parseBoolean(args[5]);
            Integer page = args.length == 7 ? Integer.parseInt(args[6]) : 1;
            PreFlag flag = Flags.getPreFlag(flagName);
            if (flag == null) {
                Notification.error(sender, Translation.Messages_UnknownFlag, flagName);
                return;
            }
            MemberController.setMemberFlag(operator, dominionName, playerName, flag, flagValue);
            MemberSetting.show(sender, dominionName, playerName, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * 重置玩家权限
     * /dominion member remove <领地名称> <玩家名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void member_remove(CommandSender sender, String[] args) {
        try {
            if (!hasPermission(sender, "dominion.default")) {
                return;
            }
            if (args.length < 4) {
                Notification.error(sender, Translation.Commands_Member_DominionRemoveMemberUsage);
                return;
            }
            DominionDTO dominion = DominionDTO.select(args[2]);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, args[2]);
                return;
            }
            PlayerDTO playerDTO = PlayerDTO.select(args[3]);
            if (playerDTO == null) {
                Notification.error(sender, Translation.Messages_PlayerNotExist, args[3]);
                return;
            }
            MemberDTO member = MemberDTO.select(playerDTO.getUuid(), dominion.getId());
            if (member == null) {
                Notification.error(sender, Translation.Messages_PlayerNotMember, args[3], args[2]);
                return;
            }
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            new MemberRemovedEvent(operator, dominion, member).call();
            MemberList.show(sender, args[2]);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * 应用权限模板
     * /dominion member apply_template <领地名称> <玩家名称> <模板名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void member_apply_template(CommandSender sender, String[] args) {
        try {
            if (!hasPermission(sender, "dominion.default")) {
                return;
            }
            if (args.length < 5) {
                Notification.error(sender, Translation.Commands_Member_DominionApplyTemplateUsage);
                return;
            }
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            String dominionName = args[2];
            String playerName = args[3];
            String templateName = args[4];
            MemberController.applyTemplate(operator, dominionName, playerName, templateName);
            MemberSetting.show(sender, dominionName, playerName);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    public static void handle(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            Notification.error(sender, Translation.Commands_Member_MemberUsage);
            return;
        }
        switch (args[1]) {
            case "add":
                Member.member_add(sender, args);
                break;
            case "set_flag":
                Member.member_set_flag(sender, args);
                break;
            case "remove":
                Member.member_remove(sender, args);
                break;
            case "apply_template":
                Member.member_apply_template(sender, args);
                break;
            case "list":
                MemberList.show(sender, args);
                break;
            case "setting":
                MemberSetting.show(sender, args);
                break;
            case "select_player":
                SelectPlayer.show(sender, args);
                break;
            case "select_template":
                SelectTemplate.show(sender, args);
                break;
        }
    }

    public static @Nullable List<String> handleTab(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 2) {
            return Arrays.asList("add", "set_flag", "remove", "apply_template", "list", "setting", "select_player", "select_template");
        }
        if (args.length == 3) {
            switch (args[1]) {
                case "add":
                case "remove":
                case "list":
                case "setting":
                case "set_flag":
                case "apply_template":
                case "select_player":
                case "select_template":
                    return playerDominions(sender);
            }
        }
        if (args.length == 4) {
            switch (args[1]) {
                case "add":
                case "remove":
                case "set_flag":
                case "apply_template":
                case "setting":
                case "select_template":
                case "select_player":
                    return playerNames();
                case "list":
                    return Collections.singletonList(Translation.Commands_PageOptional.trans());
            }
        }
        if (args.length == 5) {
            switch (args[1]) {
                case "set_flag":
                    return playerPrivileges();
                case "apply_template":
                    return allTemplates(sender);
                case "setting":
                case "select_template":
                    return Collections.singletonList(Translation.Commands_PageOptional.trans());
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
