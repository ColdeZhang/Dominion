package cn.lunadeer.dominion.uis.tuis.dominion.manage;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.utils.Particle;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.View;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getDominionNameArg_1;

public class SizeInfo {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominionNameArg_1(player, args);
        if (dominion == null) {
            Notification.error(sender, Translation.TUI_SizeInfo_Usage);
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
        view.title(String.format(Translation.TUI_SizeInfo_Title.trans(), dominion.getName()))
                .subtitle(Translation.TUI_SizeInfo_Owner.trans() + owner.getLastKnownName())
                .addLine(Line.create().append(Translation.TUI_SizeInfo_Size).append(dominion.getWidthX() + " x " + dominion.getHeight() + " x " + dominion.getWidthZ()))
                .addLine(Line.create().append(Translation.TUI_SizeInfo_Center).append((x1 + (x2 - x1) / 2) + " " + (y1 + (y2 - y1) / 2) + " " + (z1 + (z2 - z1) / 2)))
                .addLine(Line.create().append(Translation.TUI_SizeInfo_Vertical).append(String.valueOf(dominion.getHeight())))
                .addLine(Line.create().append(Translation.TUI_SizeInfo_VertY).append(y1 + " ~ " + y2))
                .addLine(Line.create().append(Translation.TUI_SizeInfo_Square).append(String.valueOf(dominion.getSquare())))
                .addLine(Line.create().append(Translation.TUI_SizeInfo_Volume).append(String.valueOf(dominion.getVolume())))
                .addLine(Line.create().append(Translation.TUI_SizeInfo_TpLocation).append(
                        dominion.getTpLocation() == null ?
                                Translation.TUI_SizeInfo_NoneTp.trans() :
                                dominion.getTpLocation().getX() + " " + dominion.getTpLocation().getY() + " " + dominion.getTpLocation().getZ()
                ))
                .actionBar(Line.create()
                        .append(Button.create(Translation.TUI_Navigation_Manage).setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append(Button.create(Translation.TUI_Navigation_GuestSetting).setExecuteCommand("/dominion guest_setting " + dominion.getName()).build()))
                .showOn(player);
        Particle.showBorder(player, dominion);
    }
}
