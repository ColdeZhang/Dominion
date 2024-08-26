package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.DominionNode;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.tuis.dominion.DominionList.BuildTreeLines;
import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getPage;
import static cn.lunadeer.dominion.utils.TuiUtils.notOp;

public class AllDominion {

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (notOp(player)) return;
        int page = getPage(args, 1);

        List<DominionNode> allDominions = DominionNode.BuildNodeTree(-1, DominionDTO.selectAll());

        ListView view = ListView.create(10, "/dominion all_dominion");

        view.title("所有领地");
        view.navigator(Line.create().append(Button.create("主菜单").setExecuteCommand("/dominion menu").build()).append("所有领地"));
        view.addLines(BuildTreeLines(allDominions, 0));
        view.showOn(player, page);
    }
}
