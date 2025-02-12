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

public class EditLeaveMessage {

    public static class EditLeaveMessageCuiText extends ConfigurationPart {
        public String title = "Edit {0} Leave Message";
        public String button = "LEAVE MSG";
        public String description = "Message shown when player leaves dominion.";
    }

    public static PermissionButton button(CommandSender sender, String dominionName) {
        return new FunctionalButton(Language.editLeaveMessageCuiText.button) {
            @Override
            public void function() {
                open(sender, dominionName);
            }
        }.needPermission(defaultPermission);
    }

    private record editLeaveMessageCB(Player sender, String dominionName) implements CuiTextInput.InputCallback {
        @Override
        public void handleData(String input) {
            try {
                setMessage(sender, dominionName, DominionSetMessageEvent.TYPE.LEAVE.name(), input);
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
            CuiTextInput.InputCallback editLeaveMessageCB = new editLeaveMessageCB(player, dominionName);
            CuiTextInput view = CuiTextInput.create(editLeaveMessageCB).title(
                    formatString(Language.editLeaveMessageCuiText.title, dominionName)
            ).setText(dominion.getLeaveMessage());
            view.setSuggestCommand(setMessage.getUsage());
            view.open(player);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }
}
