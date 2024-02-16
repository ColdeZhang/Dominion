package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.utils.STUI.Button;
import cn.lunadeer.dominion.utils.STUI.Line;
import cn.lunadeer.dominion.utils.STUI.ListView;
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
        view.title("选择玩家以创建特权").subtitle("只能选择已经登录过的玩家");
        List<PlayerDTO> players = PlayerController.allPlayers();
        for (PlayerDTO p : players) {
            if (p.getUuid() == player.getUniqueId()) {
                continue;
            }
            view.add(Line.create().
                    append(Button.create(p.getLastKnownName(),
                            "/dominion create_privilege " + p.getLastKnownName() + " " + dominion_name + " b")));
        }
        view.showOn(player, page);
    }
}
