package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.STUI.Button;
import cn.lunadeer.dominion.utils.STUI.Line;
import cn.lunadeer.dominion.utils.STUI.ListView;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getDominion;
import static cn.lunadeer.dominion.tuis.Apis.noAuthToManage;

public class DominionPrivilegeList {

    public static void show(CommandSender sender, String[] args) {
        int page = 1;
        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (Exception ignored) {
            }
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominion(player, args);
        if (dominion == null) {
            Notification.error(sender, "你不在任何领地内，请指定领地名称 /dominion privilege_list <领地名称>");
            return;
        }
        ListView view = ListView.create(10, "/dominion privilege_list " + dominion.getName());
        if (noAuthToManage(player, dominion)) return;
        List<PlayerPrivilegeDTO> privileges = PlayerPrivilegeDTO.select(dominion.getId());
        if (privileges.isEmpty()) {
            Notification.warn(sender, "领地 " + dominion.getName() + " 没有任何玩家拥有特权");
            return;
        }
        view.title("领地 " + dominion.getName() + " 玩家特权列表");
        view.navigator(
                Line.create()
                        .append(Button.create("主菜单", "/dominion menu"))
                        .append(Button.create("我的领地", "/dominion list"))
                        .append(Button.create("管理界面", "/dominion manage " + dominion.getName()))
                        .append("特权列表")
        );
        for (PlayerPrivilegeDTO privilege : privileges) {
            PlayerDTO p_player = PlayerDTO.select(privilege.getPlayerUUID());
            if (p_player == null) continue;
            view.add(Line.create()
                    .append(p_player.getLastKnownName())
                    .append(Button.createGreen("管理", "/dominion privilege_info " + p_player.getLastKnownName() + " " + dominion.getName()))
                    .append(Button.createRed("清空", "/dominion clear_privilege " + p_player.getLastKnownName() + " " + dominion.getName()))
            );
        }
        view.showOn(player, page);
    }
}
