package cn.lunadeer.dominion.uis.tuis;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.DominionNode;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.uis.tuis.dominion.DominionList.BuildTreeLines;
import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getPage;
import static cn.lunadeer.dominion.utils.TuiUtils.notOp;

public class AllDominion {

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (notOp(player)) return;
        int page = getPage(args, 1);

        List<DominionNode> allDominions = DominionNode.BuildNodeTree(-1, Cache.instance.getAllDominions());

        ListView view = ListView.create(10, "/dominion all_dominion");

        view.title(Translation.TUI_Navigation_AllDominion);
        view.navigator(Line.create().append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build()).append((Translation.TUI_Navigation_AllDominion)));
        view.addLines(BuildTreeLines(allDominions, 0));
        view.showOn(player, page);
    }
}
