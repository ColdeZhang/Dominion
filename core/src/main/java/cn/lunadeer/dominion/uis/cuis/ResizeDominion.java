package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.commands.DominionOperateCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.SetSize;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.scui.CuiTextInput;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.PermissionButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class ResizeDominion {

    public static class ResizeDominionCuiText extends ConfigurationPart {
        public String title = "{0} {1} with dir {2}";
        public String placeholder = "input size";
    }

    public static PermissionButton buttonExpand(CommandSender sender, String dominionName, String typeStr, String directionStr) {
        return new FunctionalButton(Language.setSizeTuiText.expand) {
            @Override
            public void function() {
                open(sender, dominionName, typeStr, directionStr);
            }
        }.needPermission(defaultPermission);
    }

    public static PermissionButton buttonContract(CommandSender sender, String dominionName, String typeStr, String directionStr) {
        return new FunctionalButton(Language.setSizeTuiText.contract) {
            @Override
            public void function() {
                open(sender, dominionName, typeStr, directionStr);
            }
        }.needPermission(defaultPermission);
    }

    private record resizeDominionCB(Player sender,
                                    String dominionName,
                                    String typeStr,
                                    String directionStr)
            implements CuiTextInput.InputCallback {

        @Override
        public void handleData(String input) {
            try {
                DominionOperateCommand.resize(sender, dominionName, typeStr, input, directionStr);
                SetSize.show(sender, dominionName);
            } catch (Exception e) {
                Notification.error(sender, e.getMessage());
            }
        }
    }

    public static void open(CommandSender sender, String dominionName, String typeStr, String directionStr) {
        try {
            Player player = toPlayer(sender);
            CuiTextInput.InputCallback resizeDominionCB = new resizeDominionCB(player, dominionName, typeStr, directionStr);
            CuiTextInput view = CuiTextInput.create(resizeDominionCB).title(
                    formatString(Language.resizeDominionCuiText.title, typeStr, dominionName, directionStr)
            ).setText(Language.resizeDominionCuiText.placeholder);
            view.setSuggestCommand(DominionOperateCommand.resize.getUsage());
            view.open(player);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
