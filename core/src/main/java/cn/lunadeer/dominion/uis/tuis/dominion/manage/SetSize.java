package cn.lunadeer.dominion.uis.tuis.dominion.manage;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.events.dominion.modify.DominionReSizeEvent;
import cn.lunadeer.dominion.uis.cuis.ResizeDominion;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionList;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import org.bukkit.command.CommandSender;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Asserts.assertDominionOwner;
import static cn.lunadeer.dominion.misc.Converts.toDominionDTO;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class SetSize {

    public static class SetSizeTuiText extends ConfigurationPart {
        public String title = "Resize {0}";
        public String button = "RESIZE";
        public String north = "North(z-)";
        public String south = "South(z+)";
        public String west = "West(x-)";
        public String east = "East(x+)";
        public String up = "Up(y+)";
        public String down = "Down(y-)";
        public String expand = "EXPAND";
        public String contract = "CONTRACT";
    }

    public static ListViewButton button(CommandSender sender, String dominionName) {
        return (ListViewButton) new ListViewButton(Language.setSizeTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, dominionName);
            }
        }.needPermission(defaultPermission);
    }

    public static void show(CommandSender sender, String dominionName) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            assertDominionOwner(sender, dominion);

            ListView view = ListView.create(10, button(sender, dominionName));
            view.title(formatString(Language.setSizeTuiText.title, dominion.getName()));
            view.navigator(Line.create()
                    .append(MainMenu.button(sender).build())
                    .append(DominionList.button(sender).build())
                    .append(DominionManage.button(sender, dominionName).build())
                    .append(Info.button(sender, dominionName).build())
                    .append(Language.setSizeTuiText.button));

            view.add(
                    Line.create()
                            .append(Language.setSizeTuiText.north)
                            .append(ResizeDominion.buttonExpand(sender, dominion.getName(), DominionReSizeEvent.TYPE.EXPAND.name(), DominionReSizeEvent.DIRECTION.NORTH.name()).build())
                            .append(ResizeDominion.buttonContract(sender, dominion.getName(), DominionReSizeEvent.TYPE.CONTRACT.name(), DominionReSizeEvent.DIRECTION.NORTH.name()).build())
            );
            view.add(
                    Line.create()
                            .append(Language.setSizeTuiText.south)
                            .append(ResizeDominion.buttonExpand(sender, dominion.getName(), DominionReSizeEvent.TYPE.EXPAND.name(), DominionReSizeEvent.DIRECTION.SOUTH.name()).build())
                            .append(ResizeDominion.buttonContract(sender, dominion.getName(), DominionReSizeEvent.TYPE.CONTRACT.name(), DominionReSizeEvent.DIRECTION.SOUTH.name()).build())
            );
            view.add(
                    Line.create()
                            .append(Language.setSizeTuiText.west)
                            .append(ResizeDominion.buttonExpand(sender, dominion.getName(), DominionReSizeEvent.TYPE.EXPAND.name(), DominionReSizeEvent.DIRECTION.WEST.name()).build())
                            .append(ResizeDominion.buttonContract(sender, dominion.getName(), DominionReSizeEvent.TYPE.CONTRACT.name(), DominionReSizeEvent.DIRECTION.WEST.name()).build())
            );
            view.add(
                    Line.create()
                            .append(Language.setSizeTuiText.east)
                            .append(ResizeDominion.buttonExpand(sender, dominion.getName(), DominionReSizeEvent.TYPE.EXPAND.name(), DominionReSizeEvent.DIRECTION.EAST.name()).build())
                            .append(ResizeDominion.buttonContract(sender, dominion.getName(), DominionReSizeEvent.TYPE.CONTRACT.name(), DominionReSizeEvent.DIRECTION.EAST.name()).build())
            );
            view.add(
                    Line.create()
                            .append(Language.setSizeTuiText.up)
                            .append(ResizeDominion.buttonExpand(sender, dominion.getName(), DominionReSizeEvent.TYPE.EXPAND.name(), DominionReSizeEvent.DIRECTION.UP.name()).build())
                            .append(ResizeDominion.buttonContract(sender, dominion.getName(), DominionReSizeEvent.TYPE.CONTRACT.name(), DominionReSizeEvent.DIRECTION.UP.name()).build())
            );
            view.add(
                    Line.create()
                            .append(Language.setSizeTuiText.down)
                            .append(ResizeDominion.buttonExpand(sender, dominion.getName(), DominionReSizeEvent.TYPE.EXPAND.name(), DominionReSizeEvent.DIRECTION.DOWN.name()).build())
                            .append(ResizeDominion.buttonContract(sender, dominion.getName(), DominionReSizeEvent.TYPE.CONTRACT.name(), DominionReSizeEvent.DIRECTION.DOWN.name()).build())
            );

            view.showOn(sender, 1);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
