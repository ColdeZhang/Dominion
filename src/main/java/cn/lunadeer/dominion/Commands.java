package cn.lunadeer.dominion;

import cn.lunadeer.dominion.commands.*;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.controllers.GroupController;
import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.STUI.Line;
import cn.lunadeer.dominion.utils.STUI.ListView;
import cn.lunadeer.dominion.utils.STUI.Pagination;
import cn.lunadeer.dominion.utils.STUI.View;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.commands.Helper.*;

public class Commands implements TabExecutor {
    /*
    创建领地： /dominion create <领地名称>
    自动创建领地： /dominion auto_create <领地名称>
    创建子领地： /dominion create_sub <子领地名称> [父领地名称]
    自动创建子领地： /dominion auto_create_sub <子领地名称> [父领地名称]
    扩张领地： /dominion expand [大小] [领地名称]
    缩小领地： /dominion contract [大小] [领地名称]
    删除领地： /dominion delete <领地名称> [force]
    设置领地权限： /dominion set <权限名称> <true/false> [领地名称]
    设置玩家权限： /dominion set_privilege <玩家名称> <权限名称> <true/false> [领地名称]
    重置玩家权限： /dominion clear_privilege <玩家名称> [领地名称]
    创建权限组： /dominion create_group <权限组名称>
    删除权限组： /dominion delete_group <权限组名称>
    设置权限组权限： /dominion set_group <权限组名称> <权限名称> <true/false>
    设置玩家在某个领地归属的权限组： /dominion add_player <玩家名称> <权限组名称> [领地名称]
    删除玩家在某个领地归属的权限组： /dominion remove_player <玩家名称> <权限组名称> [领地名称]
     */


    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }
        switch (args[0]) {
            case "menu":
                TUIs.menu(sender, args);
                break;
            case "help":
                TUIs.printHelp(sender, args);
                break;
            case "info":
                TUIs.sizeInfo(sender, args);
                break;
            case "manage":
                TUIs.manage(sender, args);
                break;
            case "flag_info":
                TUIs.flagInfo(sender, args);
                break;
            case "group_list":
                TUIs.groupList(sender, args);
                break;
            case "privilege_list":
                TUIs.privilegeList(sender, args);
                break;
            case "group":
                TUIs.group(sender, args);
                break;
            case "group_detail":
                TUIs.groupDetail(sender, args);
                break;
            case "create":
                DominionOperate.createDominion(sender, args);
                break;
            case "auto_create":
                DominionOperate.autoCreateDominion(sender, args);
                break;
            case "create_sub":
                DominionOperate.createSubDominion(sender, args);
                break;
            case "auto_create_sub":
                DominionOperate.autoCreateSubDominion(sender, args);
                break;
            case "expand":
                DominionOperate.expandDominion(sender, args);
                break;
            case "contract":
                DominionOperate.contractDominion(sender, args);
                break;
            case "delete":
                DominionOperate.deleteDominion(sender, args);
                break;
            case "set":
                DominionFlag.setDominionFlag(sender, args);
                break;
            case "set_privilege":
                PlayerPrivilege.setPlayerPrivilege(sender, args);
                break;
            case "clear_privilege":
                PlayerPrivilege.clearPlayerPrivilege(sender, args);
                break;
            case "create_group":
                PrivilegeGroup.createGroup(sender, args);
                break;
            case "delete_group":
                PrivilegeGroup.deleteGroup(sender, args);
                break;
            case "set_group":
                PrivilegeGroup.setDominionFlag(sender, args);
                break;
            case "add_player":
                PrivilegeGroup.addPlayer(sender, args);
                break;
            case "remove_player":
                PrivilegeGroup.removePlayer(sender, args);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("menu", "help", "info", "manage", "flag_info", "group_list", "privilege_list", "group",
                    "create", "auto_create", "create_sub", "auto_create_sub", "expand", "contract", "delete", "set",
                    "set_privilege", "clear_privilege", "create_group", "delete_group", "set_group", "add_player",
                    "remove_player"
            );
        }
        if (args.length == 2) {
            switch (args[0]) {
                case "help":
                case "group":
                    return Collections.singletonList("页码(可选)");
                case "create":
                case "auto_create":
                    return Collections.singletonList("输入领地名称");
                case "delete":
                case "create_sub":
                case "auto_create_sub":
                case "expand":
                case "contract":
                case "info":
                case "manage":
                case "flag_info":
                case "group_list":
                case "privilege_list":
                    return playerDominions(sender);
                case "set":
                    return dominionFlags();
                case "set_privilege":
                case "clear_privilege":
                case "add_player":
                case "remove_player":
                    return playerNames();
                case "create_group":
                case "delete_group":
                case "set_group":
                    return playerGroups(sender);
            }
        }
        if (args.length == 3) {
            switch (args[0]) {
                case "set":
                    return boolOptions();
                case "set_privilege":
                    return playerPrivileges();
                case "set_group":
                    return groupPrivileges();
                case "add_player":
                case "remove_player":
                    return playerGroups(sender);
            }
        }
        if (args.length == 4) {
            switch (args[0]) {
                case "set":
                case "set_privilege":
                case "clear_privilege":
                case "add_player":
                case "remove_player":
                    return playerDominions(sender);
                case "set_group":
                    return boolOptions();
            }
        }
        return null;
    }

    private static List<String> boolOptions() {
        return Arrays.asList("true", "false");
    }

    private static List<String> playerNames() {
        List<PlayerDTO> players = PlayerController.allPlayers();
        List<String> names = new ArrayList<>();
        for (PlayerDTO player : players) {
            names.add(player.getLastKnownName());
        }
        return names;
    }
}
