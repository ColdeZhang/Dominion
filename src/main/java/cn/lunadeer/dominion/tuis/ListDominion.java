package cn.lunadeer.dominion.tuis;

import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.commands.Helper.playerAdminDominions;
import static cn.lunadeer.dominion.commands.Helper.playerOwnDominions;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class ListDominion {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args);
        ListView view = ListView.create(10, "/dominion list");
        List<String> own_dominions = playerOwnDominions(sender);
        List<String> admin_dominions = playerAdminDominions(sender);

        view.title("我的领地列表");
        view.navigator(Line.create().append(Button.create("主菜单").setExecuteCommand("/dominion menu").build()).append("我的领地"));
        for (String dominion : own_dominions) {
            TextComponent manage = Button.createGreen("管理").setExecuteCommand("/dominion manage " + dominion).build();
            TextComponent delete = Button.createRed("删除").setExecuteCommand("/dominion delete " + dominion).build();
            view.add(Line.create().append(dominion).append(manage).append(delete));
        }
        for (String dominion : admin_dominions) {
            TextComponent manage = Button.createGreen("管理").setExecuteCommand("/dominion manage " + dominion).build();
            view.add(Line.create().append(dominion).append(manage));
        }
        view.showOn(player, page);
    }
}
