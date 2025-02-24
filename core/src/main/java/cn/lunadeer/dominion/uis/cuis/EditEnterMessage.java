package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.events.dominion.modify.DominionSetMessageEvent;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.scui.CuiTextInput;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.PermissionButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.commands.DominionOperateCommand.setMessage;
import static cn.lunadeer.dominion.misc.Converts.toDominionDTO;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;
import static cn.lunadeer.dominion.utils.Misc.formatString;

/**
 * Represents the EditEnterMessage functionality for editing the enter message of a dominion.
 */
public class EditEnterMessage {

    public static class EditEnterMessageCuiText extends ConfigurationPart {
        public String title = "Edit {0} Enter Message";
        public String button = "ENTER MSG";
        public String description = "Message shown when entering dominion.";
    }

    /**
     * Creates a FunctionalButton for editing the enter message of a dominion.
     *
     * @param sender       The command sender who initiates the action.
     * @param dominionName The name of the dominion to edit the enter message for.
     * @return A FunctionalButton that opens the edit enter message interface.
     */
    public static PermissionButton button(CommandSender sender, String dominionName) {
        return new FunctionalButton(Language.editEnterMessageCuiText.button) {
            @Override
            public void function() {
                open(sender, dominionName);
            }
        }.needPermission(defaultPermission);
    }

    /**
     * Callback for handling the input of the new enter message.
     */
    private record editJoinMessageCB(Player sender, String dominionName) implements CuiTextInput.InputCallback {
        @Override
        public void handleData(String input) {
            try {
                setMessage(sender, dominionName, DominionSetMessageEvent.TYPE.ENTER.name(), input);
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
            CuiTextInput.InputCallback editEnterMessageCB = new editJoinMessageCB(player, dominionName);
            CuiTextInput view = CuiTextInput.create(editEnterMessageCB).title(
                    formatString(Language.editEnterMessageCuiText.title, dominionName)
            ).setText(dominion.getJoinMessage());
            view.setSuggestCommand(setMessage.getUsage());
            view.open(player);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }
}
