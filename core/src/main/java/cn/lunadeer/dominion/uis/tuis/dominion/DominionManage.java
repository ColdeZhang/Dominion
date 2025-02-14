package cn.lunadeer.dominion.uis.tuis.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.commands.DominionOperateCommand;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.uis.cuis.EditEnterMessage;
import cn.lunadeer.dominion.uis.cuis.EditLeaveMessage;
import cn.lunadeer.dominion.uis.cuis.RenameDominion;
import cn.lunadeer.dominion.uis.cuis.SetMapColor;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.EnvSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.GuestSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.Info;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.GroupList;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberList;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Asserts.assertDominionAdmin;
import static cn.lunadeer.dominion.misc.Converts.*;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class DominionManage {

    public static class DominionManageTuiText extends ConfigurationPart {
        public String title = "Manage {0}";
        public String button = "MANAGE";
        public String setTpButton = "SET TP";
        public String setTpDescription = "Set your current location as tp location.";
    }

    public static SecondaryCommand manage = new SecondaryCommand("manage", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new CommandArguments.OptionalPageArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            show(sender, getArgumentValue(0), getArgumentValue(1));
        }
    }.needPermission(defaultPermission).register();

    public static ListViewButton button(CommandSender sender, String dominionName) {
        return (ListViewButton) new ListViewButton(Language.dominionManageTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, dominionName, pageStr);
            }
        }.needPermission(defaultPermission);
    }

    public static void show(CommandSender sender, String dominionName, String pageStr) {
        try {
            Player player = toPlayer(sender);
            DominionDTO dominion = toDominionDTO(dominionName);
            assertDominionAdmin(player, dominion);
            int page = toIntegrity(pageStr);

            Line size_info = Line.create()
                    .append(Info.button(sender, dominionName).build())
                    .append(Language.sizeInfoTuiText.description);
            Line env_info = Line.create()
                    .append(EnvSetting.button(sender, dominionName).build())
                    .append(Language.envSettingTuiText.description);
            Line flag_info = Line.create()
                    .append(GuestSetting.button(sender, dominionName).build())
                    .append(Language.guestSettingTuiText.description);
            Line member_list = Line.create()
                    .append(MemberList.button(sender, dominionName).build())
                    .append(Language.memberListTuiText.description);
            Line group_list = Line.create()
                    .append(GroupList.button(sender, dominionName).build())
                    .append(Language.groupListTuiText.description);
            Line set_tp = Line.create()
                    .append(new FunctionalButton(Language.dominionManageTuiText.setTpButton) {
                        @Override
                        public void function() {
                            DominionOperateCommand.setTp(sender, dominionName);
                        }
                    }.build())
                    .append(Language.dominionManageTuiText.setTpDescription);
            Line rename = Line.create()
                    .append(RenameDominion.button(sender, dominionName).build())
                    .append(Language.renameDominionCuiText.description);
            Line enter_msg = Line.create()
                    .append(EditEnterMessage.button(sender, dominionName).build())
                    .append(Language.editEnterMessageCuiText.description);
            Line leave_msg = Line.create()
                    .append(EditLeaveMessage.button(sender, dominionName).build())
                    .append(Language.editLeaveMessageCuiText.description);
            Line map_color = Line.create()
                    .append(SetMapColor.button(sender, dominionName).build())
                    .append(Component.text(Language.setMapColorCuiText.description)
                            .append(Component.text(dominion.getColor(),
                                    TextColor.color(dominion.getColorR(), dominion.getColorG(), dominion.getColorB()))));
            ListView view = ListView.create(10, button(sender, dominion.getName()));
            view.title(formatString(Language.dominionManageTuiText.title, dominion.getName()))
                    .navigator(Line.create()
                            .append(MainMenu.button(sender).build())
                            .append(DominionList.button(sender).build())
                            .append(dominion.getName()))
                    .add(size_info)
                    .add(env_info)
                    .add(flag_info)
                    .add(member_list)
                    .add(group_list)
                    .add(set_tp)
                    .add(rename)
                    .add(enter_msg)
                    .add(leave_msg);
            if (Configuration.webMapRenderer.blueMap || Configuration.webMapRenderer.dynmap) {
                view.add(map_color);
            }
            view.showOn(player, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }
}
