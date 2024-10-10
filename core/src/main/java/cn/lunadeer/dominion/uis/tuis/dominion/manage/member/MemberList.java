package cn.lunadeer.dominion.uis.tuis.dominion.manage.member;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.*;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.utils.CommandUtils.CommandParser;
import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getPage;
import static cn.lunadeer.dominion.utils.TuiUtils.noAuthToManage;

public class MemberList {

    public static void show(CommandSender sender, String dominionName, Integer page) {
        show(sender, new String[]{"", "", dominionName, page.toString()});
    }

    public static void show(CommandSender sender, String dominionName) {
        show(sender, new String[]{"", "", dominionName});
    }

    public static void show(CommandSender sender, String[] args) {
        if (args.length < 3) {
            Notification.error(sender, Translation.TUI_MemberList_Usage);
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[2]);
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[2]);
            return;
        }
        int page = getPage(args, 3);
        ListView view = ListView.create(10, "/dominion member list " + dominion.getName());
        if (noAuthToManage(player, dominion)) return;
        List<MemberDTO> privileges = MemberDTO.select(dominion.getId());
        view.title(String.format(Translation.TUI_MemberList_Title.trans(), dominion.getName()));
        view.navigator(
                Line.create()
                        .append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build())
                        .append(Button.create(Translation.TUI_Navigation_DominionList).setExecuteCommand("/dominion list").build())
                        .append(Button.create(Translation.TUI_Navigation_Manage).setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append(Translation.TUI_Navigation_MemberList)
        );
        view.add(Line.create().append(Button.create(Translation.TUI_MemberList_AddButton)
                .setExecuteCommand(CommandParser("/dominion member select_player %s", dominion.getName())).build()));
        for (MemberDTO privilege : privileges) {
            PlayerDTO p_player = PlayerDTO.select(privilege.getPlayerUUID());
            if (p_player == null) continue;
            GroupDTO group = Cache.instance.getGroup(privilege.getGroupId());
            Line line = Line.create();

            if (group != null) {
                line.append(groupTag);
            } else if (privilege.getAdmin()) {
                line.append(adminTag);
            } else {
                if (!privilege.getFlagValue(Flag.MOVE)) {
                    line.append(banTag);
                } else {
                    line.append(normalTag);
                }
            }

            Button prev = Button.createGreen(Translation.TUI_MemberList_FlagButton)
                    .setHoverText(Translation.TUI_MemberList_FlagDescription)
                    .setExecuteCommand(CommandParser("/dominion member setting %s %s", dominion.getName(), p_player.getLastKnownName()));
            Button remove = Button.createRed(Translation.TUI_MemberList_RemoveButton)
                    .setHoverText(Translation.TUI_MemberList_RemoveDescription)
                    .setExecuteCommand(CommandParser("/dominion member remove %s %s", dominion.getName(), p_player.getLastKnownName()));

            if (!player.getUniqueId().equals(dominion.getOwner())) {
                boolean disable = false;
                if (group == null) {
                    if (privilege.getAdmin()) {
                        disable = true;
                    }
                } else {
                    if (group.getAdmin()) {
                        disable = true;
                    }
                }
                if (disable) {
                    prev.setDisabled(Translation.TUI_MemberList_NoPermissionSet);
                    remove.setDisabled(Translation.TUI_MemberList_NoPermissionRemove);
                }
            }
            if (group != null) {
                prev.setDisabled(String.format(Translation.TUI_MemberList_BelongToGroup.trans(), group.getNamePlain()));
            }
            line.append(remove.build());
            line.append(prev.build());
            line.append(p_player.getLastKnownName());
            view.add(line);
        }
        view.showOn(player, page);
    }

    private static final TextComponent adminTag = Component.text("[A]", Style.style(TextColor.color(97, 97, 210)))
            .hoverEvent(Component.text(Translation.TUI_MemberList_AdminTag.trans()));
    private static final TextComponent normalTag = Component.text("[N]", Style.style(TextColor.color(255, 255, 255)))
            .hoverEvent(Component.text(Translation.TUI_MemberList_NormalTag.trans()));
    private static final TextComponent banTag = Component.text("[B]", Style.style(TextColor.color(255, 67, 0)))
            .hoverEvent(Component.text(Translation.TUI_MemberList_BlacklistTag.trans()));
    private static final TextComponent groupTag = Component.text("[G]", Style.style(TextColor.color(0, 185, 153)))
            .hoverEvent(Component.text(Translation.TUI_MemberList_GroupTag.trans()));
}
