package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class SelectPlayer {
    // /dominion select_player_create_privilege <领地名称> [页码]
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = 1;
        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (Exception ignored) {
            }
        }
        String dominion_name = args[1];
        ListView view = ListView.create(10, "/dominion select_player_create_privilege " + dominion_name);
        Line sub = Line.create()
                .append("只能选择已经登录过的玩家")
                .append(Button.create("搜索").setExecuteCommand("/dominion cui_create_privilege " + dominion_name).build())
                .append(Button.create("返回").setExecuteCommand("/dominion privilege_list " + dominion_name).build());
        view.title("选择玩家添加为成员").subtitle(sub);
        List<PlayerDTO> players = PlayerController.allPlayers();
        for (PlayerDTO p : players) {
            if (p.getUuid() == player.getUniqueId()) {
                continue;
            }
            view.add(Line.create().
                    append(Button.create(p.getLastKnownName())
                            .setExecuteCommand("/dominion create_privilege " + p.getLastKnownName() + " " + dominion_name + " b")
                            .build()));
        }
        view.showOn(player, page);
    }
}
