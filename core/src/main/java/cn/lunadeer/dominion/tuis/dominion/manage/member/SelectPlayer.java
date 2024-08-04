package cn.lunadeer.dominion.tuis.dominion.manage.member;

import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.CommandParser;
import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class SelectPlayer {
    public static void show(CommandSender sender, String dominionName, Integer page) {
        show(sender, new String[]{"", "", dominionName, page.toString()});
    }

    public static void show(CommandSender sender, String[] args) {
        if (args.length < 3) {
            Notification.error(sender, "用法: /dominion member select_player <领地名称> [页码]");
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args, 3);
        String dominion_name = args[2];
        ListView view = ListView.create(10, "/dominion member select_player " + dominion_name);
        Line sub = Line.create()
                .append("只能选择已经登录过的玩家")
                .append(Button.create("搜索").setExecuteCommand("/dominion cui_member_add " + dominion_name).build())
                .append(Button.create("返回").setExecuteCommand("/dominion member list " + dominion_name).build());
        view.title("选择玩家添加为成员").subtitle(sub);
        List<PlayerDTO> players = PlayerController.allPlayers();
        for (PlayerDTO p : players) {
            if (p.getUuid() == player.getUniqueId()) {
                continue;
            }
            view.add(Line.create().
                    append(Button.create(p.getLastKnownName())
                            .setExecuteCommand(CommandParser("/dominion member add %s %s", dominion_name, p.getLastKnownName()))
                            .build()));
        }
        view.showOn(player, page);
    }
}
