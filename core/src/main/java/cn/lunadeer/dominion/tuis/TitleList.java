package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class TitleList {

    public static void show(CommandSender sender, int page) {
        show(sender, new String[]{String.valueOf(page)});
    }

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args, 1);
        ListView view = ListView.create(10, "/dominion title_list");

        view.title("我可使用的权限组称号");
        view.navigator(Line.create().append(Button.create("主菜单").setExecuteCommand("/dominion menu").build()).append("权限组称号列表"));

        List<GroupDTO> groups = Cache.instance.getBelongGroupsOf(player.getUniqueId());
        GroupDTO using = Cache.instance.getPlayerUsingGroupTitle(player.getUniqueId());

        for (GroupDTO group : groups) {
            DominionDTO dominion = Cache.instance.getDominion(group.getDomID());
            Line line = Line.create();
            if (using != null && using.getId().equals(group.getId())) {
                line.append(Button.createRed("卸下").setExecuteCommand("/dominion use_title -1").build());
            } else {
                line.append(Button.createGreen("使用").setExecuteCommand("/dominion use_title " + group.getId()).build());
            }
            line.append(group.getNameColoredComponent()).append("来自领地：" + dominion.getName());
            view.add(line);
        }

        view.showOn(player, page);
    }

}
