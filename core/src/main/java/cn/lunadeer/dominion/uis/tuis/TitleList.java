package cn.lunadeer.dominion.uis.tuis;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getPage;

public class TitleList {

    public static void show(CommandSender sender, int page) {
        show(sender, new String[]{String.valueOf(page)});
    }

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args, 1);
        ListView view = ListView.create(10, "/dominion title_list");

        view.title(Translation.TUI_TitleList_Title);
        view.navigator(Line.create().append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build()).append(Translation.TUI_Navigation_TitleList));

        List<GroupDTO> groups = Cache.instance.getBelongGroupsOf(player.getUniqueId());
        GroupDTO using = Cache.instance.getPlayerUsingGroupTitle(player.getUniqueId());

        // 将其拥有的所有领地的权限组称号都加入列表 - 领地所有者可以使用其领地的任意权限组称号
        List<DominionDTO> dominions = Cache.instance.getPlayerDominions(player.getUniqueId());
        for (DominionDTO dominion : dominions) {
            List<GroupDTO> groupsOfDom = dominion.getGroups();
            groups.addAll(groupsOfDom);
        }

        for (GroupDTO group : groups) {
            DominionDTO dominion = Cache.instance.getDominion(group.getDomID());
            Line line = Line.create();
            if (using != null && using.getId().equals(group.getId())) {
                line.append(Button.createRed(Translation.TUI_TitleList_RemoveButton).setExecuteCommand("/dominion use_title -1").build());
            } else {
                line.append(Button.createGreen(Translation.TUI_TitleList_ApplyButton).setExecuteCommand("/dominion use_title " + group.getId()).build());
            }
            line.append(group.getNameColoredComponent()).append(Translation.TUI_TitleList_FromDominion.trans() + dominion.getName());
            view.add(line);
        }

        view.showOn(player, page);
    }

}
