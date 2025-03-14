package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.commands.DominionOperateCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.scui.CuiTextInput;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.PermissionButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toDominionDTO;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class SetMapColor {

    public static class SetMapColorCuiText extends ConfigurationPart {
        public String title = "Set {0} Map Color";
        public String button = "COLOR";
        public String description = "Color of the dominion on the web map.";
    }

    public static PermissionButton button(CommandSender sender, String dominionName) {
        return new FunctionalButton(Language.setMapColorCuiText.button) {
            @Override
            public void function() {
                open(sender, dominionName);
            }
        }.needPermission(defaultPermission);
    }

    private record setMapColorCB(Player sender, String dominionName) implements CuiTextInput.InputCallback {
        @Override
        public void handleData(String input) {
            try {
                DominionOperateCommand.setMapColor(sender, dominionName, input);
                DominionManage.show(sender, dominionName, "1");
            } catch (Exception e) {
                Notification.error(sender, e.getMessage());
            }
        }
    }

    public static void open(CommandSender sender, String dominionName) {
        try {
            Player player = toPlayer(sender);
            DominionDTO dominion = toDominionDTO(dominionName);
            CuiTextInput.InputCallback setMapColorCB = new SetMapColor.setMapColorCB(player, dominionName);
            CuiTextInput view = CuiTextInput.create(setMapColorCB).setText(dominion.getColor())
                    .title(formatString(Language.setMapColorCuiText.title, dominionName));
            view.setSuggestCommand(DominionOperateCommand.setMapColor.getUsage());
            view.open(player);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
