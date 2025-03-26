package cn.lunadeer.dominion.uis.tuis.dominion.manage.member;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.commands.MemberCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.doos.PlayerDOO;
import cn.lunadeer.dominion.uis.cuis.SearchPlayer;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import org.bukkit.command.CommandSender;

import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Asserts.assertDominionAdmin;
import static cn.lunadeer.dominion.misc.Converts.toDominionDTO;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;

public class SelectPlayer {

    public static class SelectPlayerTuiText extends ConfigurationPart {
        public String title = "Select Player";
        public String button = "ADD PLAYER";
        public String description = "Add a player as a member of this dominion.";
        public String back = "BACK";
    }

    public static ListViewButton button(CommandSender sender, String dominionName) {
        return (ListViewButton) new ListViewButton(Language.selectPlayerTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, dominionName, pageStr);
            }
        }.needPermission(defaultPermission).setHoverText(Language.selectPlayerTuiText.description);
    }

    public static void show(CommandSender sender, String dominionName, String pageStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            assertDominionAdmin(sender, dominion);
            int page = toIntegrity(pageStr);

            ListView view = ListView.create(10, button(sender, dominionName));
            Line sub = Line.create()
                    .append(SearchPlayer.button(sender, dominionName).build())
                    .append(MemberList.button(sender, dominionName).setText(Language.selectPlayerTuiText.back).build());
            view.title(Language.selectPlayerTuiText.title).subtitle(sub);
            List<PlayerDTO> players = PlayerDOO.all();
            for (PlayerDTO p : players) {
                view.add(Line.create().
                        append(new FunctionalButton(p.getLastKnownName()) {
                            @Override
                            public void function() {
                                MemberCommand.addMember(sender, dominionName, p.getLastKnownName());
                            }
                        }.needPermission(defaultPermission).build()));
            }
            view.showOn(sender, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }
}
