package cn.lunadeer.dominion.uis.tuis;

import cn.lunadeer.dominion.DominionNode;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionList;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.*;

public class AllDominion {

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (notOp(player)) return;

        if (isBedRockPlayer(player)) {
            cn.lunadeer.dominion.uis.beuis.AllDominion.sendAllDominionMenu(player);
            return;
        }

        int page = getPage(args, 1);

        List<DominionNode> allDominions = DominionNode.BuildNodeTree(-1, DominionDTO.selectAll());

        ListView view = ListView.create(10, "/dominion all_dominion");

        view.title(Translation.TUI_Navigation_AllDominion);
        view.navigator(Line.create().append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build()).append((Translation.TUI_Navigation_AllDominion)));
        view.addLines(DominionList.BuildTreeLines(allDominions, 0));
        view.showOn(player, page);
    }
}
