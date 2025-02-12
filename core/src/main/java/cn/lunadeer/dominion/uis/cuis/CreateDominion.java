package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.commands.DominionCreateCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionList;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.scui.CuiTextInput;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.PermissionButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;

public class CreateDominion {

    public static class CreateDominionCuiText extends ConfigurationPart {
        public String title = "Create Dominion";
        public String input = "New Dominion Name";
        public String description = "Auto Create Dominion around you.";
        public String button = "CREATE";
    }

    public static PermissionButton button(CommandSender sender) {
        return new FunctionalButton(Language.createDominionCuiText.button) {
            @Override
            public void function() {
                open(sender);
            }
        }.needPermission(defaultPermission);
    }

    private record createDominionCB(Player sender) implements CuiTextInput.InputCallback {
        @Override
        public void handleData(String input) {
            try {
                DominionCreateCommand.autoCreate(sender, input);
                DominionList.show(sender, "1");
            } catch (Exception e) {
                Notification.error(sender, e.getMessage());
            }
        }
    }

    public static void open(CommandSender sender) {
        try {
            Player player = toPlayer(sender);
            CuiTextInput.InputCallback createDominionCB = new createDominionCB(player);
            CuiTextInput view = CuiTextInput.create(createDominionCB).setText(Language.createDominionCuiText.input)
                    .title(Language.createDominionCuiText.title);
            view.setSuggestCommand(DominionCreateCommand.autoCreate.getUsage());
            view.open(player);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }
}
