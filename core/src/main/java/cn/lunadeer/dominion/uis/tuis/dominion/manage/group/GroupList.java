package cn.lunadeer.dominion.uis.tuis.dominion.manage.group;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.utils.TuiUtils;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.noAuthToManage;

public class GroupList {

    public static void show(CommandSender sender, String dominionName) {
        show(sender, new String[]{"", "", dominionName});
    }

    public static void show(CommandSender sender, String dominionName, int page) {
        show(sender, new String[]{"", "", dominionName, String.valueOf(page)});
    }

    public static void show(CommandSender sender, String[] args) {
        if (args.length < 3) {
            Notification.error(sender, Translation.TUI_GroupList_Usage);
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[2]);
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[2]);
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        int page = TuiUtils.getPage(args, 3);
        List<GroupDTO> groups = GroupDTO.selectByDominionId(dominion.getId());
        ListView view = ListView.create(10, "/dominion group list " + dominion.getName());
        view.title(String.format(Translation.TUI_GroupList_Title.trans(), dominion.getName()));
        view.navigator(
                Line.create()
                        .append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build())
                        .append(Button.create(Translation.TUI_Navigation_DominionList).setExecuteCommand("/dominion list").build())
                        .append(Button.create(Translation.TUI_Navigation_Manage).setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append(Translation.TUI_Navigation_GroupList)
        );

        Button create_btn = Button.createGreen(Translation.TUI_GroupList_CreateButton)
                .setHoverText(Translation.TUI_GroupList_CreateDescription)
                .setExecuteCommand("/dominion cui_create_group " + dominion.getName());
        view.add(new Line().append(create_btn.build()));

        for (GroupDTO group : groups) {
            Line line = new Line();
            Button del = Button.createRed(Translation.TUI_DeleteButton)
                    .setHoverText(String.format(Translation.TUI_GroupList_DeleteDescription.trans(), group.getNamePlain()))
                    .setExecuteCommand("/dominion group delete " + dominion.getName() + " " + group.getNamePlain());
            Button edit = Button.create(Translation.TUI_EditButton)
                    .setHoverText(String.format(Translation.TUI_GroupList_EditDescription.trans(), group.getNamePlain()))
                    .setExecuteCommand("/dominion group setting " + dominion.getName() + " " + group.getNamePlain());
            Button add = Button.createGreen("+")
                    .setHoverText(String.format(Translation.TUI_GroupList_AddMemberDescription.trans(), group.getNamePlain()))
                    .setExecuteCommand("/dominion group select_member " + dominion.getName() + " " + group.getNamePlain() + " " + page);
            line.append(del.build()).append(edit.build()).append(group.getNameColoredComponent()).append(add.build());
            view.add(line);
            List<MemberDTO> players = MemberDTO.selectByGroupId(group.getId());
            XLogger.debug("players: " + players.size());
            for (MemberDTO playerPrivilege : players) {
                PlayerDTO p = PlayerDTO.select(playerPrivilege.getPlayerUUID());
                if (p == null) continue;
                Button remove = Button.createRed("-")
                        .setHoverText(
                                String.format(Translation.TUI_GroupList_RemoveMemberDescription.trans(), p.getLastKnownName(), group.getNamePlain())
                        )
                        .setExecuteCommand("/dominion group remove_member " + dominion.getName() + " " + group.getNamePlain() + " " + p.getLastKnownName() + " " + page);
                Line playerLine = new Line().setDivider("");
                playerLine.append(Component.text("        "));
                playerLine.append(remove.build()).append(" |  " + p.getLastKnownName());
                view.add(playerLine);
            }
            view.add(new Line().append(""));
        }
        view.showOn(player, page);
    }

}
