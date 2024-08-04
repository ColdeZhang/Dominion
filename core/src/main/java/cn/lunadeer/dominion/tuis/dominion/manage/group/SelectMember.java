package cn.lunadeer.dominion.tuis.dominion.manage.group;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;
import static cn.lunadeer.dominion.tuis.Apis.noAuthToManage;

public class SelectMember {
    public static void show(CommandSender sender, String[] args) {
        if (args.length < 4) {
            Notification.error(sender, "用法: /dominion group select_member <领地名称> <权限组名称> [回显页码] [页码]");
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[2]);
        if (dominion == null) {
            Notification.error(sender, "领地 %s 不存在", args[2]);
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        GroupDTO group = GroupDTO.select(dominion.getId(), args[3]);
        if (group == null) {
            Notification.error(sender, "权限组不存在");
            return;
        }
        int backPage = getPage(args, 4);
        int page = getPage(args, 5);
        ListView view = ListView.create(10, "/dominion group select_member " + dominion.getName() + " " + group.getName() + " " + backPage);
        view.title("选择成员");
        Line sub = Line.create().append("选择成员添加到权限组 " + group.getName())
                .append(Button.create("返回").setExecuteCommand("/dominion group list " + dominion.getName() + " " + backPage).build());
        view.subtitle(sub);
        List<MemberDTO> members = MemberDTO.selectByDomGroupId(dominion.getId(), -1);
        for (MemberDTO member : members) {
            PlayerDTO p = PlayerDTO.select(member.getPlayerUUID());
            if (p == null) continue;
            view.add(Line.create()
                    .append(Button.create(p.getLastKnownName())
                            .setExecuteCommand("/dominion group add_member " + dominion.getName() + " " + group.getName() + " " + p.getLastKnownName() + " " + backPage)
                            .build()));
        }
        view.showOn(player, page);
    }
}
