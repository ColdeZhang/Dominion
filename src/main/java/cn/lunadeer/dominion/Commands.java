package cn.lunadeer.dominion;

import cn.lunadeer.dominion.commands.DominionFlag;
import cn.lunadeer.dominion.commands.DominionOperate;
import cn.lunadeer.dominion.commands.Operator;
import cn.lunadeer.dominion.commands.PlayerPrivilege;
import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.tuis.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            Menu.show(sender, args);
            return true;
        }
        switch (args[0]) {
            case "menu":
                Menu.show(sender, args);
                break;
            case "list":
                ListDominion.show(sender, args);
                break;
            case "help":
                cn.lunadeer.dominion.tuis.Apis.printHelp(sender, args);
                break;
            case "info":
                DominionSizeInfo.show(sender, args);
                break;
            case "manage":
                DominionManage.show(sender, args);
                break;
            case "flag_info":
                DominionFlagInfo.show(sender, args);
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
            case "create_privilege":
                PlayerPrivilege.createPlayerPrivilege(sender, args);
                break;
            case "set_privilege":
                PlayerPrivilege.setPlayerPrivilege(sender, args);
                break;
            case "clear_privilege":
                PlayerPrivilege.clearPlayerPrivilege(sender, args);
                break;
            case "privilege_list":
                DominionPrivilegeList.show(sender, args);
                break;
            case "privilege_info":
                PrivilegeInfo.show(sender, args);
                break;
            case "select_player_create_privilege":
                SelectPlayer.show(sender, args);
                break;
            case "set_enter_msg":
                DominionOperate.setEnterMessage(sender, args);
                break;
            case "set_leave_msg":
                DominionOperate.setLeaveMessage(sender, args);
                break;
            case "rename":
                DominionOperate.renameDominion(sender, args);
                break;
            case "give":
                DominionOperate.giveDominion(sender, args);
                break;
            case "reload_cache":
                Operator.reloadCache(sender, args);
                break;
            case "export_mca":
                Operator.exportMca(sender, args);
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
                    "create_privilege", "set_privilege", "clear_privilege", "list", "privilege_info",
                    "set_enter_msg",
                    "set_leave_msg",
                    "rename",
                    "give",
                    "reload_cache",
                    "export_mca"
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
                case "info":
                case "manage":
                case "flag_info":
                case "privilege_list":
                case "rename":
                case "give":
                    return playerDominions(sender);
                case "set":
                    return dominionFlags();
                case "create_privilege":
                case "set_privilege":
                case "clear_privilege":
                case "privilege_info":
                    return playerNames();
                case "expand":
                case "contract":
                    return Collections.singletonList("大小(整数)");
                case "create_sub":
                case "auto_create_sub":
                    return Collections.singletonList("子领地名称");
                case "set_enter_msg":
                    return Collections.singletonList("进入提示语内容");
                case "set_leave_msg":
                    return Collections.singletonList("离开提示语内容");
            }
        }
        if (args.length == 3) {
            switch (args[0]) {
                case "set":
                    return boolOptions();
                case "set_privilege":
                    return playerPrivileges();
                case "expand":
                case "contract":
                case "clear_privilege":
                case "create_privilege":
                case "privilege_info":
                case "auto_create_sub":
                case "create_sub":
                case "set_enter_msg":
                case "set_leave_msg":
                    return playerDominions(sender);
                case "rename":
                    return Collections.singletonList("输入新领地名称");
                case "give":
                    return playerNames();
            }
        }
        if (args.length == 4) {
            switch (args[0]) {
                case "set":
                    return playerDominions(sender);
                case "set_privilege":
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
