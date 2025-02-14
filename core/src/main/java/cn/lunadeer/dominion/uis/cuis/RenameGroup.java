package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.commands.GroupCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.events.group.GroupRenamedEvent;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.GroupSetting;
import cn.lunadeer.dominion.utils.ColorParser;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.scui.CuiTextInput;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.*;


public class RenameGroup {

    public static class RenameGroupCuiText extends ConfigurationPart {
        public String title = "Rename Group";
        public String button = "RENAME";
        public String description = "Rename this group.";
    }

    public static FunctionalButton button(CommandSender sender, String dominionName, String groupName) {
        return (FunctionalButton) new FunctionalButton(Language.renameGroupCuiText.button) {
            @Override
            public void function() {
                open(sender, dominionName, groupName);
            }
        }.needPermission(defaultPermission).setHoverText(Language.renameGroupCuiText.description);
    }

    private record renameGroupCB(Player sender, String dominionName,
                                 String oldGroupName) implements CuiTextInput.InputCallback {
        @Override
        public void handleData(String input) {
            try {
                DominionDTO dominion = toDominionDTO(dominionName);
                GroupDTO group = toGroupDTO(dominion, oldGroupName);
                new GroupRenamedEvent(sender, dominion, group, input).call();
                GroupSetting.show(sender, dominionName, ColorParser.getPlainText(input), "1");
            } catch (Exception e) {
                Notification.error(sender, e.getMessage());
            }
        }
    }

    public static void open(CommandSender sender, String dominionName, String groupName) {
        try {
            Player player = toPlayer(sender);
            CuiTextInput.InputCallback renameGroupCB = new renameGroupCB(player, dominionName, groupName);
            CuiTextInput view = CuiTextInput.create(renameGroupCB)
                    .setText(groupName)
                    .title(Language.renameGroupCuiText.title);
            view.setSuggestCommand(GroupCommand.renameGroup.getUsage());
            view.open(player);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
