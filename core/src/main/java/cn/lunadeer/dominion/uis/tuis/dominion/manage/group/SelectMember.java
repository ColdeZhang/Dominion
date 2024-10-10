package cn.lunadeer.dominion.uis.tuis.dominion.manage.group;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getPage;
import static cn.lunadeer.dominion.utils.TuiUtils.noAuthToManage;

public class SelectMember {
    public static void show(CommandSender sender, String[] args) {
        if (args.length < 4) {
            Notification.error(sender, Translation.TUI_SelectMember_Usage);
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
        GroupDTO group = GroupDTO.select(dominion.getId(), args[3]);
        if (group == null) {
            Notification.error(sender, Translation.Messages_GroupNotExist, args[2], args[3]);
            return;
        }
        int backPage = getPage(args, 4);
        int page = getPage(args, 5);
        ListView view = ListView.create(10, "/dominion group select_member " + dominion.getName() + " " + group.getNamePlain() + " " + backPage);
        view.title(Translation.TUI_SelectMember_Title);
        Line sub = Line.create().append(String.format(Translation.TUI_SelectMember_Description.trans(), group.getNamePlain()))
                .append(Button.create(Translation.TUI_BackButton).setExecuteCommand("/dominion group list " + dominion.getName() + " " + backPage).build());
        view.subtitle(sub);
        List<MemberDTO> members = MemberDTO.selectByDomGroupId(dominion.getId(), -1);
        for (MemberDTO member : members) {
            PlayerDTO p = PlayerDTO.select(member.getPlayerUUID());
            if (p == null) continue;
            view.add(Line.create()
                    .append(Button.create(p.getLastKnownName())
                            .setExecuteCommand("/dominion group add_member " + dominion.getName() + " " + group.getNamePlain() + " " + p.getLastKnownName() + " " + backPage)
                            .build()));
        }
        view.showOn(player, page);
    }
}
