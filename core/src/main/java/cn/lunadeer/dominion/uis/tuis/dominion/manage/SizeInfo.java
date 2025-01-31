package cn.lunadeer.dominion.uis.tuis.dominion.manage;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.utils.Particle;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.ControllerUtils.notOwner;
import static cn.lunadeer.dominion.utils.TuiUtils.getDominionNameArg_1;

public class SizeInfo {
    public static void show(CommandSender sender, String dominionName) {
        show(sender, new String[]{"info", dominionName});
    }

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
        ListView view = ListView.create(10, "/dominion info " + dominion.getName());
        view.title(String.format(Translation.TUI_SizeInfo_Title.trans(), dominion.getName()));

        if (notOwner(BukkitPlayerOperator.create(player), dominion)) {
            view.subtitle(Translation.TUI_SizeInfo_Owner.trans() + owner.getLastKnownName());
        } else {
            view.subtitle(Line.create()
                    .append(Button.create(Translation.TUI_Navigation_Manage).setExecuteCommand("/dominion manage " + dominion.getName()).build())
                    .append(Button.create(Translation.TUI_ResizeButton).setExecuteCommand("/dominion resize " + dominion.getName()).build())
            );
        }
        view.add(Line.create().append(Translation.TUI_SizeInfo_Size).append(dominion.getWidthX() + " x " + dominion.getHeight() + " x " + dominion.getWidthZ()))
                .add(Line.create().append(Translation.TUI_SizeInfo_Center).append((x1 + (x2 - x1) / 2) + " " + (y1 + (y2 - y1) / 2) + " " + (z1 + (z2 - z1) / 2)))
                .add(Line.create().append(Translation.TUI_SizeInfo_Vertical).append(String.valueOf(dominion.getHeight())))
                .add(Line.create().append(Translation.TUI_SizeInfo_VertY).append(y1 + " ~ " + y2))
                .add(Line.create().append(Translation.TUI_SizeInfo_Square).append(String.valueOf(dominion.getSquare())))
                .add(Line.create().append(Translation.TUI_SizeInfo_Volume).append(String.valueOf(dominion.getVolume())))
                .add(Line.create().append(Translation.TUI_SizeInfo_TpLocation).append(
                        dominion.getTpLocation() == null ?
                                Translation.TUI_SizeInfo_NoneTp.trans() :
                                dominion.getTpLocation().getX() + " " + dominion.getTpLocation().getY() + " " + dominion.getTpLocation().getZ()
                ))
                .showOn(player, 1);
        Particle.showBorder(player, dominion);
    }
}
