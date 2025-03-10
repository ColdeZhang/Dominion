package cn.lunadeer.dominion.uis.tuis.dominion.manage;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionList;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.ParticleUtil;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toDominionDTO;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class Info {
    public static class SizeInfoTuiText extends ConfigurationPart {
        public String title = "{0} Size Info";
        public String button = "SIZE INFO";
        public String description = "Show dominion size information";
        public String ownerName = "This dominion is owned by {0}.";

        public String infoLWH = "Size: {0} x {1} x {2}";
        public String infoHeight = "Height: {0} ~ {1}";
        public String square = "Square: {0}";
        public String volume = "Volume: {0}";
    }

    public static ListViewButton button(CommandSender sender, String dominionName) {
        return (ListViewButton) new ListViewButton(Language.sizeInfoTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, dominionName);
            }
        }.needPermission(defaultPermission);
    }

    public static void show(CommandSender sender, String dominionName) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            String ownerName = CacheManager.instance.getPlayerName(dominion.getOwner());
            ListView view = ListView.create(10, button(sender, dominionName));
            view.title(formatString(Language.sizeInfoTuiText.title, dominion.getName()));

            boolean isOwner = false;
            if (sender instanceof Player player) {
                if (player.getUniqueId().equals(dominion.getOwner())) {
                    isOwner = true;
                    ParticleUtil.showBorder(player, dominion);
                }
            }
            if (!isOwner) {
                view.subtitle(Line.create().append(formatString(Language.sizeInfoTuiText.ownerName, ownerName)));
            } else {
                view.subtitle(Line.create()
                        .append(MainMenu.button(sender).build())
                        .append(DominionList.button(sender).build())
                        .append(DominionManage.button(sender, dominionName).build())
                        .append(Language.sizeInfoTuiText.button).build()
                );
            }
            CuboidDTO cuboid = dominion.getCuboid();
            view.add(Line.create().append(SetSize.button(sender, dominionName).build()))
                    .add(Line.create().append(formatString(Language.sizeInfoTuiText.infoLWH, cuboid.xLength(), cuboid.yLength(), cuboid.zLength())))
                    .add(Line.create().append(formatString(Language.sizeInfoTuiText.infoHeight, cuboid.y1(), cuboid.y2())))
                    .add(Line.create().append(formatString(Language.sizeInfoTuiText.square, cuboid.getSquare())))
                    .add(Line.create().append(formatString(Language.sizeInfoTuiText.volume, cuboid.getVolume())))
                    .showOn(sender, 1);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }
}
