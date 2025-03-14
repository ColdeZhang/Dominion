package cn.lunadeer.dominion.uis.tuis.dominion.manage.member;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.commands.MemberCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionList;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.*;
import static cn.lunadeer.dominion.utils.Misc.formatString;


public class MemberSetting {

    public static class MemberSettingTuiText extends ConfigurationPart {
        public String title = "{0} Member Setting";
        public String description = "Set member's privilege of dominion.";
        public String button = "SETTING";
    }

    public static SecondaryCommand setting = new SecondaryCommand("member_setting", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new CommandArguments.RequiredPlayerArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            show(sender, getArgumentValue(0), getArgumentValue(1), "1");
        }
    }.needPermission(defaultPermission).register();

    public static ListViewButton button(CommandSender sender, String dominionName, String playerName) {
        return (ListViewButton) new ListViewButton(Language.memberSettingTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, dominionName, playerName, pageStr);
            }
        }.needPermission(defaultPermission).setHoverText(Language.memberSettingTuiText.description);
    }

    public static void show(CommandSender sender, String dominionName, String playerName, String pageStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            MemberDTO member = toMemberDTO(dominion, playerName);
            int page = toIntegrity(pageStr);
            ListView view = ListView.create(10, button(sender, dominionName, playerName));
            view.title(formatString(Language.memberSettingTuiText.title, playerName));
            view.navigator(
                    Line.create()
                            .append(MainMenu.button(sender).build())
                            .append(DominionList.button(sender).build())
                            .append(DominionManage.button(sender, dominionName).build())
                            .append(MemberList.button(sender, dominionName).build())
                            .append(Language.memberSettingTuiText.button)
            );
            view.add(Line.create().append(SelectTemplate.button(sender, dominionName, playerName).build()));
            if (member.getFlagValue(Flags.ADMIN)) {
                view.add(createOption(sender, Flags.ADMIN, true, playerName, dominion.getName(), page));
                view.add(createOption(sender, Flags.GLOW, member.getFlagValue(Flags.GLOW), playerName, dominion.getName(), page));
            } else {
                for (PriFlag flag : Flags.getAllPriFlagsEnable()) {
                    view.add(createOption(sender, flag, member.getFlagValue(flag), playerName, dominion.getName(), page));
                }
            }
            view.showOn(sender, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    private static Line createOption(CommandSender sender, PriFlag flag, boolean value, String player_name, String dominion_name, int page) {
        if (value) {
            return Line.create()
                    .append(new FunctionalButton("☑") {
                        @Override
                        public void function() {
                            MemberCommand.setMemberPrivilege(sender, dominion_name, player_name, flag.getFlagName(), "false", String.valueOf(page));
                        }
                    }.needPermission(defaultPermission).green().build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription()))
                    );
        } else {
            return Line.create()
                    .append(new FunctionalButton("☐") {
                        @Override
                        public void function() {
                            MemberCommand.setMemberPrivilege(sender, dominion_name, player_name, flag.getFlagName(), "true", String.valueOf(page));
                        }
                    }.needPermission(defaultPermission).red().build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription()))
                    );
        }
    }
}
