package cn.lunadeer.dominion.uis.tuis.dominion;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.DominionNode;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.ViewStyles;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.commands.Helper.playerAdminDominionNames;
import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getPage;

public class DominionList {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args, 1);
        ListView view = ListView.create(10, "/dominion list");

        view.title(Translation.TUI_DominionList_Title);
        view.navigator(Line.create()
                .append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build())
                .append(Translation.TUI_Navigation_DominionList));
        view.addLines(BuildTreeLines(DominionNode.BuildNodeTree(-1, Cache.instance.getPlayerDominions(player.getUniqueId())), 0));
        List<String> admin_dominions = playerAdminDominionNames(sender);
        if (!admin_dominions.isEmpty()) {
            view.add(Line.create().append(""));
            view.add(Line.create().append(Component.text(Translation.TUI_DominionList_AdminSection.trans(), ViewStyles.main_color)));
        }
        for (String dominion : admin_dominions) {
            TextComponent manage = Button.createGreen(Translation.TUI_ManageButton).setExecuteCommand("/dominion manage " + dominion).build();
            view.add(Line.create().append(manage).append(dominion));
        }
        view.showOn(player, page);
    }

    public static List<Line> BuildTreeLines(List<DominionNode> dominionTree, Integer depth) {
        List<Line> lines = new ArrayList<>();
        StringBuilder prefix = new StringBuilder();
        prefix.append(" | ".repeat(Math.max(0, depth)));
        for (DominionNode node : dominionTree) {
            TextComponent manage = Button.createGreen(Translation.TUI_ManageButton).setExecuteCommand("/dominion manage " + node.getDominion().getName()).build();
            TextComponent delete = Button.createRed(Translation.TUI_DeleteButton).setExecuteCommand("/dominion delete " + node.getDominion().getName()).build();
            Line line = Line.create().append(delete).append(manage).append(prefix + node.getDominion().getName());
            lines.add(line);
            lines.addAll(BuildTreeLines(node.getChildren(), depth + 1));
        }
        return lines;
    }
}
