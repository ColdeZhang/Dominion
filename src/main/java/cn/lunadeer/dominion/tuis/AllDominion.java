package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.DominionNode;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;
import static cn.lunadeer.dominion.tuis.Apis.notOp;
import static cn.lunadeer.dominion.tuis.dominion.DominionList.BuildTreeLines;

public class AllDominion {

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (notOp(player)) return;
        int page = getPage(args, 1);

        List<DominionNode> allDominions = Cache.instance.getAllDominionTree();

        ListView view = ListView.create(10, "/dominion all_dominion");

        view.title("所有领地");
        view.navigator(Line.create().append(Button.create("主菜单").setExecuteCommand("/dominion menu").build()).append("所有领地"));
        view.addLines(BuildTreeLines(allDominions, 0));
        view.showOn(player, page);
    }
}
