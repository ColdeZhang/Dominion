package cn.lunadeer.dominion.uis.tuis.dominion;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.*;

public class DominionManage {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominionNameArg_1(player, args);
        if (dominion == null) {
            Notification.error(sender, Translation.TUI_DominionManage_NotInDominion);
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        int page = getPage(args, 2);
        Line size_info = Line.create()
                .append(Button.create(Translation.TUI_DominionManage_InfoButton).setExecuteCommand("/dominion info " + dominion.getName()).build())
                .append(Translation.TUI_DominionManage_InfoDescription);
        Line env_info = Line.create()
                .append(Button.create(Translation.TUI_DominionManage_EnvSettingButton).setExecuteCommand("/dominion env_setting " + dominion.getName()).build())
                .append(Translation.TUI_DominionManage_EnvSettingDescription);
        Line flag_info = Line.create()
                .append(Button.create(Translation.TUI_DominionManage_GuestSettingButton).setExecuteCommand("/dominion guest_setting " + dominion.getName()).build())
                .append(Translation.TUI_DominionManage_GuestSettingDescription);
        Line privilege_list = Line.create()
                .append(Button.create(Translation.TUI_DominionManage_MemberListButton).setExecuteCommand("/dominion member list " + dominion.getName()).build())
                .append(Translation.TUI_DominionManage_MemberListDescription);
        Line group_list = Line.create()
                .append(Button.create(Translation.TUI_DominionManage_GroupListButton).setExecuteCommand("/dominion group list " + dominion.getName()).build())
                .append(Translation.TUI_DominionManage_GroupListDescription);
        Line set_tp = Line.create()
                .append(Button.create(Translation.TUI_DominionManage_SetTpLocationButton).setExecuteCommand("/dominion set_tp_location " + dominion.getName()).build())
                .append(Translation.TUI_DominionManage_SetTpLocationDescription);
        Line rename = Line.create()
                .append(Button.create(Translation.TUI_DominionManage_RenameButton).setExecuteCommand("/dominion cui_rename " + dominion.getName()).build())
                .append(Translation.TUI_DominionManage_RenameDescription);
        Line join_msg = Line.create()
                .append(Button.create(Translation.TUI_DominionManage_EditJoinMessageButton).setExecuteCommand("/dominion cui_edit_join_message " + dominion.getName()).build())
                .append(Translation.TUI_DominionManage_EditJoinMessageDescription);
        Line leave_msg = Line.create()
                .append(Button.create(Translation.TUI_DominionManage_EditLeaveMessageButton).setExecuteCommand("/dominion cui_edit_leave_message " + dominion.getName()).build())
                .append(Translation.TUI_DominionManage_EditLeaveMessageDescription);
        Line map_color = Line.create()
                .append(Button.create(Translation.TUI_DominionManage_SetMapColorButton).setExecuteCommand("/dominion cui_set_map_color " + dominion.getName()).build())
                .append(Component.text(Translation.TUI_DominionManage_SetMapColorDescription.trans())
                        .append(Component.text(dominion.getColor(),
                                TextColor.color(dominion.getColorR(), dominion.getColorG(), dominion.getColorB()))));
        ListView view = ListView.create(10, "/dominion manage " + dominion.getName());
        view.title(String.format(Translation.TUI_DominionManage_Title.trans(), dominion.getName()))
                .navigator(Line.create()
                        .append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build())
                        .append(Button.create(Translation.TUI_Navigation_DominionList).setExecuteCommand("/dominion list").build())
                        .append(dominion.getName()))
                .add(size_info)
                .add(env_info)
                .add(flag_info)
                .add(privilege_list)
                .add(group_list)
                .add(set_tp)
                .add(rename)
                .add(join_msg)
                .add(leave_msg);
        if (Dominion.config.getBlueMap() || Dominion.config.getDynmap()) {
            view.add(map_color);
        }
        view.showOn(player, page);
    }
}
