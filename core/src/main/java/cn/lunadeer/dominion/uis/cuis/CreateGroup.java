package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.commands.GroupCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.events.group.GroupCreateEvent;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.GroupList;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.scui.CuiTextInput;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toDominionDTO;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;

public class CreateGroup {

    public static class CreateGroupCuiText extends ConfigurationPart {
        public String title = "Create New Group";
        public String input = "New Group Name";
        public String description = "Create a new permission group.";
        public String button = "CREATE";
    }

    public static FunctionalButton button(CommandSender sender, String dominionName) {
        return (FunctionalButton) new FunctionalButton(Language.createGroupCuiText.button) {
            @Override
            public void function() {
                open(sender, dominionName);
            }
        }.needPermission(defaultPermission).setHoverText(Language.createGroupCuiText.description);
    }

    private record createGroupCB(Player sender, String dominionName) implements CuiTextInput.InputCallback {
        @Override
        public void handleData(String input) {
            try {
                DominionDTO dominion = toDominionDTO(dominionName);
                new GroupCreateEvent(sender, dominion, input).call();
                GroupList.show(sender, dominionName, "1");
            } catch (Exception e) {
                Notification.error(sender, e.getMessage());
            }
        }
    }

    public static void open(CommandSender sender, String dominionName) {
        try {
            Player player = toPlayer(sender);
            DominionDTO dominion = toDominionDTO(dominionName);
            CuiTextInput.InputCallback createGroupCB = new createGroupCB(player, dominion.getName());
            CuiTextInput view = CuiTextInput.create(createGroupCB)
                    .setText(Language.createGroupCuiText.input)
                    .title(Language.createGroupCuiText.title);
            view.setSuggestCommand(GroupCommand.createGroup.getUsage());
            view.open(player);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
