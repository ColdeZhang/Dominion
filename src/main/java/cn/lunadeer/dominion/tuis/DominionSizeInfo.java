package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.STUI.Button;
import cn.lunadeer.dominion.utils.STUI.Line;
import cn.lunadeer.dominion.utils.STUI.View;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getDominion;

public class DominionSizeInfo {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominion(player, args);
        if (dominion == null) {
            Notification.error(sender, "你不在任何领地内，请指定领地名称 /dominion info <领地名称>");
            return;
        }
        PlayerDTO owner = PlayerController.getPlayerDTO(dominion.getOwner());
        Integer x1 = dominion.getX1();
        Integer y1 = dominion.getY1();
        Integer z1 = dominion.getZ1();
        Integer x2 = dominion.getX2();
        Integer y2 = dominion.getY2();
        Integer z2 = dominion.getZ2();
        View view = View.create();
        view.title("领地 " + dominion.getName() + " 的尺寸信息")
                .subtitle("领地归属：" + owner.getLastKnownName())
                .addLine(Line.create().append("领地大小：").append((x2 - x1) + " x" + (y2 - y1) + " x" + (z2 - z1)))
                .addLine(Line.create().append("中心坐标：").append((x1 + (x2 - x1) / 2) + " " + (y1 + (y2 - y1) / 2) + " " + (z1 + (z2 - z1) / 2)))
                .addLine(Line.create().append("垂直高度：").append(String.valueOf(y2 - y1)))
                .addLine(Line.create().append("水平面积：").append(String.valueOf((x2 - x1) * (z2 - z1))))
                .addLine(Line.create().append("领地体积：").append(String.valueOf((x2 - x1) * (y2 - y1) * (z2 - z1))))
                .actionBar(Line.create()
                        .append(Button.create("管理界面", "/dominion manage " + dominion.getName()))
                        .append(Button.create("权限列表", "/dominion flag_info " + dominion.getName())))
                .showOn(player);
    }
}
