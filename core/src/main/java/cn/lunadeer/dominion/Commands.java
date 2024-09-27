package cn.lunadeer.dominion;

import cn.lunadeer.dominion.commands.*;
import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.cuis.*;
import cn.lunadeer.dominion.uis.tuis.AllDominion;
import cn.lunadeer.dominion.uis.tuis.Menu;
import cn.lunadeer.dominion.uis.tuis.MigrateList;
import cn.lunadeer.dominion.uis.tuis.TitleList;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionList;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.EnvSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.GuestSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.SizeInfo;
import cn.lunadeer.dominion.utils.TuiUtils;
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
                DominionList.show(sender, args);
                break;
            case "help":
                TuiUtils.printHelp(sender, args);
                break;
            case "info":
                SizeInfo.show(sender, args);
                break;
            case "manage":
                DominionManage.show(sender, args);
                break;
            case "guest_setting":
                GuestSetting.show(sender, args);
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
            case "set_enter_msg":
                DominionOperate.setEnterMessage(sender, args);
                break;
            case "set_leave_msg":
                DominionOperate.setLeaveMessage(sender, args);
                break;
            case "set_tp_location":
                DominionOperate.setTpLocation(sender, args);
                break;
            case "tp":
                DominionOperate.teleportToDominion(sender, args);
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
            case "reload_config":
                Operator.reloadConfig(sender, args);
                break;
            case "export_mca":
                Operator.exportMca(sender, args);
                break;
            case "export_db":
                Operator.exportDatabase(sender, args);
                break;
            case "import_db":
                Operator.importDatabase(sender, args);
                break;
//            case "set_config":
//                SetConfig.handler(sender, args);
//                break;
            case "all_dominion":
                AllDominion.show(sender, args);
                break;
            case "migrate_list":
                MigrateList.show(sender, args);
                break;
            case "migrate":
                Migration.migrate(sender, args);
                break;
            case "set_map_color":
                DominionOperate.setMapColor(sender, args);
                break;
            case "env_setting":
                EnvSetting.show(sender, args);
                break;
            case "use_title":
                Title.use_title(sender, args);
                break;
            case "title_list":
                TitleList.show(sender, args);
                break;
            case "version":
                Operator.version(sender, args);
                break;
            // ---===  Sub Command  ===---
            case "member":
                Member.handle(sender, args);
                break;
            case "group":
                Group.handle(sender, args);
                break;
            case "template":
                Template.handle(sender, args);
                break;
            // ---===  CUI  ===---
            case "cui_rename":
                RenameDominion.open(sender, args);
                break;
            case "cui_edit_join_message":
                EditJoinMessage.open(sender, args);
                break;
            case "cui_edit_leave_message":
                EditLeaveMessage.open(sender, args);
                break;
            case "cui_create":
                CreateDominion.open(sender, args);
                break;
            case "cui_member_add":
                MemberAdd.open(sender, args);
                break;
            case "cui_template_create":
                CreateTemplate.open(sender, args);
                break;
            case "cui_set_map_color":
                SetMapColor.open(sender, args);
                break;
            case "cui_create_group":
                CreateGroup.open(sender, args);
                break;
            case "cui_rename_group":
                RenameGroup.open(sender, args);
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
            return Arrays.asList("menu", "help", "info", "manage", "guest_setting",
                    "create", "auto_create", "create_sub", "auto_create_sub", "expand", "contract", "delete", "set",
                    "set_enter_msg",
                    "set_leave_msg",
                    "set_tp_location",
                    "tp",
                    "rename",
                    "give",
                    "reload_cache",
                    "reload_config",
                    "export_mca",
                    "export_db",
                    "import_db",
                    "sys_config",
                    "all_dominion",
                    "set_map_color",
                    "member",
                    "group",
                    "template"
            );
        }
        if (args.length > 1 && args[0].equals("member")) {
            return Member.handleTab(sender, args);
        }
        if (args.length > 1 && args[0].equals("group")) {
            return Group.handleTab(sender, args);
        }
        if (args.length > 1 && args[0].equals("template")) {
            return Template.handleTab(sender, args);
        }
        if (args.length == 2) {
            switch (args[0]) {
                case "help":
                case "list":
                case "sys_config":
                    return Collections.singletonList(Translation.Commands_PageOptional.trans());
                case "create":
                case "auto_create":
                    return Collections.singletonList(Translation.Commands_DominionName.trans());
                case "delete":
                case "info":
                case "manage":
                case "guest_setting":
                case "rename":
                case "give":
                case "set_tp_location":
                    return playerDominions(sender);
                case "tp":
                    return allDominions();
                case "set":
                    return dominionFlags();
                case "expand":
                case "contract":
                    return Collections.singletonList(Translation.Commands_SizeInteger.trans());
                case "create_sub":
                case "auto_create_sub":
                    return Collections.singletonList(Translation.Commands_SubDominionName.trans());
                case "set_enter_msg":
                    return Collections.singletonList(Translation.Commands_EnterMessageContent.trans());
                case "set_leave_msg":
                    return Collections.singletonList(Translation.Commands_LeaveMessageContent.trans());
                case "set_map_color":
                    return Collections.singletonList(Translation.Commands_InputColor.trans());
            }
        }
        if (args.length == 3) {
            switch (args[0]) {
                case "set":
                    return boolOptions();
                case "expand":
                case "contract":
                case "auto_create_sub":
                case "create_sub":
                case "set_enter_msg":
                case "set_leave_msg":
                case "set_map_color":
                    return playerDominions(sender);
                case "rename":
                    return Collections.singletonList(Translation.Commands_NewDominionName.trans());
                case "give":
                    return playerNames();
            }
        }
        if (args.length == 4) {
            switch (args[0]) {
                case "set":
                    return playerDominions(sender);
            }
        }
        return null;
    }

    public static List<String> boolOptions() {
        return Arrays.asList("true", "false");
    }

    public static List<String> playerNames() {
        List<PlayerDTO> players = PlayerController.allPlayers();
        List<String> names = new ArrayList<>();
        for (PlayerDTO player : players) {
            names.add(player.getLastKnownName());
        }
        return names;
    }
}
