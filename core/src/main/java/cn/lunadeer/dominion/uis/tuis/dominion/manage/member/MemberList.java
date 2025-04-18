package cn.lunadeer.dominion.uis.tuis.dominion.manage.member;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
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
import cn.lunadeer.dominion.utils.stui.components.buttons.Button;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.doos.MemberDOO.selectByDominionId;
import static cn.lunadeer.dominion.misc.Asserts.assertDominionAdmin;
import static cn.lunadeer.dominion.misc.Asserts.assertDominionOwner;
import static cn.lunadeer.dominion.misc.Converts.toDominionDTO;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class MemberList {

    public static class MemberListTuiText extends ConfigurationPart {
        public String title = "{0} Member List";
        public String description = "List of members of this dominion.";
        public String button = "MEMBERS";
        public String remove = "REMOVE";
        public String removeDescription = "Remove this member from this dominion.";

        public String ownerOnly = "Only owner can manage admin member.";
        public String groupOnly = "This member belong to group {0} so you can't manage it separately.";

        public String tagAdmin = "Admin can manage members and groups of this dominion.";
        public String tagNormal = "Normal members.";
        public String tagBan = "Who don't have MOVE privilege.";
        public String tagGroup = "This player belong to a group, you can't manage it separately.";
    }

    public static SecondaryCommand list = new SecondaryCommand("member_list", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new CommandArguments.OptionalPageArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            show(sender, getArgumentValue(0), getArgumentValue(1));
        }
    }.needPermission(defaultPermission).register();

    public static ListViewButton button(CommandSender sender, String dominionName) {
        return (ListViewButton) new ListViewButton(Language.memberListTuiText.button) {
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
            view.title(formatString(Language.memberListTuiText.title, dominion.getName()));
            view.navigator(
                    Line.create()
                            .append(MainMenu.button(sender).build())
                            .append(DominionList.button(sender).build())
                            .append(DominionManage.button(sender, dominionName).build())
                            .append(Language.memberListTuiText.button)
            );
            view.add(Line.create()
                    .append(SelectPlayer.button(sender, dominionName).build())
            );

            // get data from database directly because cache update may not be in time
            List<MemberDTO> members = new ArrayList<>(selectByDominionId(dominion.getId()));
            for (MemberDTO member : members) {
                PlayerDTO p_player = member.getPlayer();
                GroupDTO group = CacheManager.instance.getGroup(member.getGroupId());
                Line line = Line.create();
                // Tag
                if (group != null) {
                    line.append(groupTag);
                } else if (member.getFlagValue(Flags.ADMIN)) {
                    line.append(adminTag);
                } else {
                    if (!member.getFlagValue(Flags.MOVE)) {
                        line.append(banTag);
                    } else {
                        line.append(normalTag);
                    }
                }

                Button prev = MemberSetting.button(sender, dominionName, p_player.getLastKnownName()).green();
                Button remove = new FunctionalButton(Language.memberListTuiText.remove) {
                    @Override
                    public void function() {
                        MemberCommand.removeMember(sender, dominionName, p_player.getLastKnownName(), pageStr);
                    }
                }.setHoverText(Language.memberListTuiText.removeDescription).red();

                boolean disable = false;
                try {
                    assertDominionOwner(sender, dominion);
                } catch (Exception e) {
                    // not owner then the sender is admin so he should not remove other admin
                    disable = member.getFlagValue(Flags.ADMIN);
                }
                if (disable) {
                    prev.setDisabled(Language.memberListTuiText.ownerOnly);
                    remove.setDisabled(Language.memberListTuiText.ownerOnly);
                }
                if (group != null) {
                    prev.setDisabled(formatString(Language.memberListTuiText.groupOnly, group.getNamePlain()));
                }

                line.append(remove.build());
                line.append(prev.build());
                line.append(p_player.getLastKnownName());
                view.add(line);
            }
            view.showOn(sender, page);

        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }

    }

    private static final TextComponent adminTag = Component.text("[A]", Style.style(TextColor.color(97, 97, 210)))
            .hoverEvent(Component.text(Language.memberListTuiText.tagAdmin));
    private static final TextComponent normalTag = Component.text("[N]", Style.style(TextColor.color(255, 255, 255)))
            .hoverEvent(Component.text(Language.memberListTuiText.tagNormal));
    private static final TextComponent banTag = Component.text("[B]", Style.style(TextColor.color(255, 67, 0)))
            .hoverEvent(Component.text(Language.memberListTuiText.tagBan));
    private static final TextComponent groupTag = Component.text("[G]", Style.style(TextColor.color(0, 185, 153)))
            .hoverEvent(Component.text(Language.memberListTuiText.tagGroup));
}
