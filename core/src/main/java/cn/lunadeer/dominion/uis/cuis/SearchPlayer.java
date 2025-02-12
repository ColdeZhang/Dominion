package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.commands.MemberCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberList;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.scui.CuiTextInput;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.PermissionButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;

public class SearchPlayer {

    public static class SearchPlayerCuiText extends ConfigurationPart {
        public String title = "Search Player";
        public String input = "Exact player name";
        public String button = "SEARCH";
    }

    public static PermissionButton button(CommandSender sender, String dominionName) {
        return new FunctionalButton(Language.searchPlayerCuiText.button) {
            @Override
            public void function() {
                open(sender, dominionName);
            }
        }.needPermission(defaultPermission);
    }

    private record memberAddCB(Player sender, String dominionName) implements CuiTextInput.InputCallback {
        @Override
        public void handleData(String input) {
            try {
                MemberCommand.addMember(sender, dominionName, input);
                MemberList.show(sender, dominionName, "1");
            } catch (Exception e) {
                Notification.error(sender, e.getMessage());
            }
        }
    }

    public static void open(CommandSender sender, String dominionName) {
        try {
            Player player = toPlayer(sender);
            CuiTextInput.InputCallback createPrivilegeCB = new memberAddCB(player, dominionName);
            CuiTextInput view = CuiTextInput.create(createPrivilegeCB).setText(Language.searchPlayerCuiText.input)
                    .title(Language.searchPlayerCuiText.title);
            view.setSuggestCommand(MemberCommand.addMember.getUsage());
            view.open(player);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
