package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.STUI.Button;
import cn.lunadeer.dominion.utils.STUI.Line;
import cn.lunadeer.dominion.utils.STUI.ListView;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.commands.Helper.playerDominions;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class ListDominion {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args);
        ListView view = ListView.create(5, "/dominion list");
        List<String> dominions = playerDominions(sender);
        if (dominions.isEmpty()) {
            Notification.warn(sender, "你没有任何领地");
            return;
        }
        view.title("我的领地列表");
        view.navigator(Line.create().append(Button.create("主菜单", "/dominion menu")).append("我的领地"));
        for (String dominion : dominions) {
            TextComponent manage = Button.createGreen("管理", "/dominion manage " + dominion);
            TextComponent delete = Button.createRed("删除", "/dominion delete " + dominion);
            view.add(Line.create().append(dominion).append(manage).append(delete));
        }
        view.showOn(player, page);
    }
}
