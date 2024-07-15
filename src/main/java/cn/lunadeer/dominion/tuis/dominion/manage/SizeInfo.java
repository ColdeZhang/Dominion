package cn.lunadeer.dominion.tuis.dominion.manage;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.ParticleRender;
import cn.lunadeer.minecraftpluginutils.stui.View;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getDominionNameArg_1;

public class SizeInfo {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominionNameArg_1(player, args);
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
                .subtitle("领地所有者：" + owner.getLastKnownName())
                .addLine(Line.create().append("领地大小：").append(dominion.getWidthX() + " x " + dominion.getHeight() + " x " + dominion.getWidthZ()))
                .addLine(Line.create().append("中心坐标：").append((x1 + (x2 - x1) / 2) + " " + (y1 + (y2 - y1) / 2) + " " + (z1 + (z2 - z1) / 2)))
                .addLine(Line.create().append("垂直高度：").append(String.valueOf(dominion.getHeight())))
                .addLine(Line.create().append("Y轴坐标：").append(y1 + " ~ " + y2))
                .addLine(Line.create().append("水平面积：").append(String.valueOf(dominion.getSquare())))
                .addLine(Line.create().append("领地体积：").append(String.valueOf(dominion.getVolume())))
                .addLine(Line.create().append("传送点坐标：").append(
                        dominion.getTpLocation() == null ?
                                "无" :
                                dominion.getTpLocation().getX() + " " + dominion.getTpLocation().getY() + " " + dominion.getTpLocation().getZ()
                ))
                .actionBar(Line.create()
                        .append(Button.create("管理界面").setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append(Button.create("访客权限").setExecuteCommand("/dominion guest_setting " + dominion.getName()).build()))
                .showOn(player);
        ParticleRender.showBoxFace(player,
                dominion.getLocation1(),
                dominion.getLocation2());
    }
}
