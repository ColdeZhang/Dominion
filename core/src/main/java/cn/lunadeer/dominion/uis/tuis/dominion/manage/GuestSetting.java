package cn.lunadeer.dominion.uis.tuis.dominion.manage;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.commands.DominionFlagCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionList;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Asserts.assertDominionAdmin;
import static cn.lunadeer.dominion.misc.Converts.toDominionDTO;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class GuestSetting {
    public static class GuestSettingTuiText extends ConfigurationPart {
        public String title = "{0} Guest Setting";
        public String button = "GUEST SET";
        public String description = "Set guest behavior of dominion.";
    }

    public static ListViewButton button(CommandSender sender, String dominionName) {
        return (ListViewButton) new ListViewButton(Language.guestSettingTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, dominionName, pageStr);
            }
        }.needPermission(defaultPermission);
    }

    public static void show(CommandSender sender, String dominionName, String pageStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            assertDominionAdmin(sender, dominion);
            int page = toIntegrity(pageStr);

            ListView view = ListView.create(10, button(sender, dominionName));
            view.title(formatString(Language.guestSettingTuiText.title, dominion.getName()))
                    .navigator(Line.create()
                            .append(MainMenu.button(sender).build())
                            .append(DominionList.button(sender).build())
                            .append(DominionManage.button(sender, dominionName).build())
                            .append(Language.guestSettingTuiText.button));
            for (PriFlag flag : Flags.getAllPriFlagsEnable()) {
                if (flag.equals(Flags.ADMIN)) continue; // Skip admin flag this only for group or member
                if (dominion.getGuestFlagValue(flag)) {
                    view.add(Line.create()
                            .append(new FunctionalButton("☑") {
                                @Override
                                public void function() {
                                    DominionFlagCommand.setGuest(sender, dominionName, flag.getFlagName(), "false", String.valueOf(page));
                                }
                            }.green().build())
                            .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())))
                    );
                } else {
                    view.add(Line.create()
                            .append(new FunctionalButton("☐") {
                                @Override
                                public void function() {
                                    DominionFlagCommand.setGuest(sender, dominionName, flag.getFlagName(), "true", String.valueOf(page));
                                }
                            }.red().build())
                            .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())))
                    );
                }
            }
            view.showOn(sender, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }
}
