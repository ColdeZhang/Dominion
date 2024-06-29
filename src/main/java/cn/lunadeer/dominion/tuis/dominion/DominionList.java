package cn.lunadeer.dominion.tuis.dominion;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.DominionNode;
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

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.commands.Helper.playerAdminDominions;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class DominionList {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args);
        ListView view = ListView.create(10, "/dominion list");

        view.title("我的领地列表");
        view.navigator(Line.create().append(Button.create("主菜单").setExecuteCommand("/dominion menu").build()).append("我的领地"));
        view.addLines(BuildTreeLines(Cache.instance.getDominionTreeByPlayer(player.getName()), 0));
        List<String> admin_dominions = playerAdminDominions(sender);
        if (admin_dominions.size() != 0) {
            view.add(Line.create().append(""));
            view.add(Line.create().append(Component.text("--- 以下为你拥有管理员权限的领地 ---", ViewStyles.main_color)));
        }
        for (String dominion : admin_dominions) {
            TextComponent manage = Button.createGreen("管理").setExecuteCommand("/dominion manage " + dominion).build();
            view.add(Line.create().append(manage).append(dominion));
        }
        view.showOn(player, page);
    }

    public static List<Line> BuildTreeLines(List<DominionNode> dominionTree, Integer depth) {
        List<Line> lines = new ArrayList<>();
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            prefix.append(" | ");
        }
        for (DominionNode node : dominionTree) {
            TextComponent manage = Button.createGreen("管理").setExecuteCommand("/dominion manage " + node.getDominion().getName()).build();
            TextComponent delete = Button.createRed("删除").setExecuteCommand("/dominion delete " + node.getDominion().getName()).build();
            Line line = Line.create().append(delete).append(manage).append(prefix + node.getDominion().getName());
            lines.add(line);
            lines.addAll(BuildTreeLines(node.getChildren(), depth + 1));
        }
        return lines;
    }
}
