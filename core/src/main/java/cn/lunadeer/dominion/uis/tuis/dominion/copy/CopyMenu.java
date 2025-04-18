package cn.lunadeer.dominion.uis.tuis.dominion.copy;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionList;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import org.bukkit.command.CommandSender;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Asserts.assertDominionAdmin;
import static cn.lunadeer.dominion.misc.Converts.toDominionDTO;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class CopyMenu {

    public static class CopyMenuTuiText extends ConfigurationPart {
        public String button = "COPY";
        public String description = "Copy Privilege Settings From Other Dominion.";
        public String title = "Select Copy Type";
    }

    public static ListViewButton button(CommandSender sender, String toDominionName) {
        return (ListViewButton) new ListViewButton(Language.copyMenuTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, toDominionName, pageStr);
            }
        }.needPermission(defaultPermission);
    }

    public static void show(CommandSender sender, String toDominionName, String pageStr) {
        try {
            DominionDTO dominion = toDominionDTO(toDominionName);
            assertDominionAdmin(sender, dominion);
            int page = toIntegrity(pageStr);

            ListView view = ListView.create(10, button(sender, toDominionName));
            view.title(formatString(Language.copyMenuTuiText.title));
            view.navigator(
                    Line.create()
                            .append(MainMenu.button(sender).build())
                            .append(DominionList.button(sender).build())
                            .append(DominionManage.button(sender, toDominionName).build())
                            .append(Language.copyMenuTuiText.button)
            );
            view.add(Line.create()
                    .append(EnvCopy.button(sender, toDominionName).build())
                    .append(Language.envCopyTuiText.description));
            view.add(Line.create()
                    .append(GuestCopy.button(sender, toDominionName).build())
                    .append(Language.guestCopyTuiText.description));
            view.add(Line.create()
                    .append(MemberCopy.button(sender, toDominionName).build())
                    .append(Language.memberCopyTuiText.description));
            view.add(Line.create()
                    .append(GroupCopy.button(sender, toDominionName).build())
                    .append(Language.groupCopyTuiText.description));
            view.showOn(sender, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }
}
