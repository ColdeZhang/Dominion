package cn.lunadeer.dominion.uis.tuis.dominion.manage.group;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.commands.GroupCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.uis.cuis.RenameGroup;
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
import static cn.lunadeer.dominion.misc.Converts.*;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class GroupSetting {

    public static class GroupSettingTuiText extends ConfigurationPart {
        public String title = "Group {0} Settings";
        public String description = "Manage the settings of group {0}.";
        public String button = "SETTING";
    }

    public static ListViewButton button(CommandSender sender, String dominionName, String groupName) {
        return (ListViewButton) new ListViewButton(Language.groupSettingTuiText.button) {
            @Override
            public void function(String page) {
                show(sender, dominionName, groupName, page);
            }
        }.needPermission(defaultPermission).setHoverText(Language.groupSettingTuiText.description);
    }

    public static void show(CommandSender sender, String dominionName, String groupName, String pageStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            assertDominionAdmin(sender, dominion);
            GroupDTO group = toGroupDTO(dominion, groupName);
            int page = toIntegrity(pageStr);

            ListView view = ListView.create(10, button(sender, dominionName, groupName));
            view.title(formatString(Language.groupSettingTuiText.title, groupName));
            view.navigator(
                    Line.create()
                            .append(MainMenu.button(sender).build())
                            .append(DominionList.button(sender).build())
                            .append(DominionManage.button(sender, dominionName).build())
                            .append(GroupList.button(sender, dominionName).build())
                            .append(Language.groupSettingTuiText.button)
            );
            view.add(Line.create().append(RenameGroup.button(sender, dominionName, groupName).build()));

            if (group.getFlagValue(Flags.ADMIN)) {
                view.add(createOption(sender, Flags.ADMIN, true, dominion.getName(), group.getNamePlain(), pageStr));
                view.add(createOption(sender, Flags.GLOW, group.getFlagValue(Flags.GLOW), dominion.getName(), group.getNamePlain(), pageStr));
            } else {
                for (PriFlag flag : Flags.getAllPriFlagsEnable()) {
                    view.add(createOption(sender, flag, group.getFlagValue(flag), dominion.getName(), group.getNamePlain(), pageStr));
                }
            }
            view.showOn(sender, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    private static Line createOption(CommandSender sender, PriFlag flag, boolean value, String DominionName, String groupName, String pageStr) {
        if (value) {
            return Line.create()
                    .append(new FunctionalButton("☑") {
                        @Override
                        public void function() {
                            GroupCommand.setGroupFlag(sender, DominionName, groupName, flag.getFlagName(), "false", pageStr);
                        }
                    }.needPermission(defaultPermission).green().build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        } else {
            return Line.create()
                    .append(new FunctionalButton("☐") {
                        @Override
                        public void function() {
                            GroupCommand.setGroupFlag(sender, DominionName, groupName, flag.getFlagName(), "true", pageStr);
                        }
                    }.needPermission(defaultPermission).red().build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        }
    }
}
