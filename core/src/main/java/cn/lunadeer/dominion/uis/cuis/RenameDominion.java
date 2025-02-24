package cn.lunadeer.dominion.uis.cuis;

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
import static cn.lunadeer.dominion.misc.Converts.toPlayer;

public class RenameDominion {
    public static class RenameDominionCuiText extends ConfigurationPart {
        public String title = "Rename {0}";
        public String description = "Rename dominion.";
        public String button = "RENAME";
    }

    public static PermissionButton button(CommandSender sender, String dominionName) {
        return new FunctionalButton(Language.renameDominionCuiText.button) {
            @Override
            public void function() {
                open(sender, dominionName);
            }
        }.needPermission(defaultPermission);
    }

    private record renameDominionCB(Player sender, String oldName) implements CuiTextInput.InputCallback {
        @Override
        public void handleData(String input) {
            try {
                DominionOperateCommand.rename(sender, oldName, input);
                DominionManage.show(sender, input, "1");
            } catch (Exception e) {
                Notification.error(sender, e.getMessage());
            }
        }
    }

    public static void open(CommandSender sender, String dominionName) {
        try {
            Player player = toPlayer(sender);
            CuiTextInput.InputCallback renameDominionCB = new renameDominionCB(player, dominionName);
            CuiTextInput view = CuiTextInput.create(renameDominionCB).setText(dominionName).title(
                    String.format(Language.renameDominionCuiText.title, dominionName)
            );
            view.setSuggestCommand(DominionOperateCommand.rename.getUsage());
            view.open(player);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }
}
