package cn.lunadeer.dominion.uis.tuis.dominion.manage.group;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.commands.GroupCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.doos.MemberDOO.selectByDominionId;
import static cn.lunadeer.dominion.misc.Asserts.assertDominionAdmin;
import static cn.lunadeer.dominion.misc.Converts.*;

public class SelectMember {

    public static class SelectMemberTuiText extends ConfigurationPart {
        public String title = "Select Member";
        public String description = "Select a member to add to the group.";
        public String back = "BACK";
    }

    public static ListViewButton button(CommandSender sender, String dominionName, String groupName, String backPageStr) {
        return (ListViewButton) new ListViewButton("+") {
            @Override
            public void function(String page) {
                show(sender, dominionName, groupName, backPageStr, page);
            }
        }.needPermission(defaultPermission).green().setHoverText(Language.selectMemberTuiText.description);
    }

    public static void show(CommandSender sender, String dominionName, String groupName, String backPageStr, String pageStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            assertDominionAdmin(sender, dominion);
            int page = toIntegrity(pageStr);

            ListView view = ListView.create(10, button(sender, dominionName, groupName, backPageStr));
            view.title(Language.selectMemberTuiText.title);
            Line sub = Line.create().append(new FunctionalButton(Language.selectMemberTuiText.back) {
                @Override
                public void function() {
                    GroupList.show(sender, dominionName, backPageStr);
                }
            }.needPermission(defaultPermission).build());
            view.subtitle(sub);

            // get data from database directly because cache update may not be in time
            List<MemberDTO> members = new ArrayList<>(selectByDominionId(dominion.getId()));
            for (MemberDTO member : members) {
                if (member.getGroupId() != -1) {
                    continue;
                }
                PlayerDTO p = toPlayerDTO(member.getPlayerUUID());
                view.add(Line.create()
                        .append(new FunctionalButton(p.getLastKnownName()) {
                            @Override
                            public void function() {
                                GroupCommand.addMember(sender, dominionName, groupName, p.getLastKnownName());
                            }
                        }.needPermission(defaultPermission).build()));
            }
            view.showOn(sender, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }
}
