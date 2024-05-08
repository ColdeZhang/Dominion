package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.utils.STUI.Button;
import cn.lunadeer.dominion.utils.STUI.Line;
import cn.lunadeer.dominion.utils.STUI.ListView;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class DominionConfig {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args);
        ListView view = ListView.create(10, "/dominion config");
        view.title("系统配置");
        view.navigator(Line.create().append(Button.create("主菜单", "/dominion menu")).append("系统配置"));
        // todo: add config items
        view.showOn(player, page);
    }
}
