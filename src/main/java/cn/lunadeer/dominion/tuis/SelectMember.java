package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.*;

public class SelectMember {
    public static void show(CommandSender sender, String[] args) {
        if (args.length < 3) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominionNameArg_1(player, args);
        if (dominion == null) {
            Notification.error(sender, "领地不存在");
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        GroupDTO group = GroupDTO.select(dominion.getId(), args[2]);
        if (group == null) {
            Notification.error(sender, "权限组不存在");
            return;
        }
        int backPage = getPage(args, 3);
        ListView view = ListView.create(10, "/dominion select_member_add_group " + dominion.getName() + " " + group.getName() + " " + backPage);
        view.title("选择成员");
        view.navigator(
                Line.create()
                        .append("添加到权限组 " + group.getName())
                        .append(Button.create("返回").setExecuteCommand("/dominion group_list " + dominion.getName() + " " + backPage).build())
        );
        List<PlayerPrivilegeDTO> members = PlayerPrivilegeDTO.selectByGroupId(-1);
        for (PlayerPrivilegeDTO member : members) {
            PlayerDTO p = PlayerDTO.select(member.getPlayerUUID());
            if (p == null) continue;
            view.add(Line.create()
                    .append(Button.create(p.getLastKnownName())
                            .setExecuteCommand("/dominion group_add_member " + dominion.getName() + " " + group.getName() + " " + p.getLastKnownName() + " " + backPage)
                            .build()));
        }
        view.showOn(player, backPage);
    }
}
