package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.events.member.MemberAddedEvent;
import cn.lunadeer.dominion.events.member.MemberRemovedEvent;
import cn.lunadeer.dominion.events.member.MemberSetFlagEvent;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberList;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberSetting;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.command.Argument;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.*;

public class MemberCommand {

    /**
     * Command to add a member to a dominion.
     */
    public static SecondaryCommand addMember = new SecondaryCommand("member_add", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new Argument("player_name", true)
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            addMember(sender, getArgumentValue(0), getArgumentValue(1));
        }
    }.needPermission(defaultPermission).register();

    /**
     * Adds a member to a dominion.
     *
     * @param sender       the command sender
     * @param dominionName the name of the dominion
     * @param playerName   the name of the player to be added
     */
    public static void addMember(CommandSender sender, String dominionName, String playerName) {
        try {
            PlayerDTO player = toPlayerDTO(playerName);
            DominionDTO dominion = toDominionDTO(dominionName);
            new MemberAddedEvent(sender, dominion, player).call();
            MemberList.show(sender, dominionName, "1");
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * Command to set a member's privilege in a dominion.
     */
    public static SecondaryCommand setMemberPrivilege = new SecondaryCommand("member_set_pri", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new CommandArguments.RequiredPlayerArgument(),
            new CommandArguments.PriFlagArgument(),
            new CommandArguments.BollenOption(),
            new CommandArguments.OptionalPageArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            setMemberPrivilege(
                    sender,
                    getArgumentValue(0),
                    getArgumentValue(1),
                    getArgumentValue(2),
                    getArgumentValue(3),
                    getArgumentValue(4)
            );
        }
    }.needPermission(defaultPermission).register();

    /**
     * Sets a member's privilege in a dominion.
     *
     * @param sender       the command sender
     * @param dominionName the name of the dominion
     * @param playerName   the name of the player
     * @param flagName     the name of the privilege flag
     * @param valueStr     the value of the privilege flag
     * @param pageStr      the page number for the member setting display
     */
    public static void setMemberPrivilege(CommandSender sender, String dominionName, String playerName, String flagName, String valueStr, String pageStr) {
        try {
            PriFlag flag = toPriFlag(flagName);
            boolean value = toBoolean(valueStr);
            DominionDTO dominion = toDominionDTO(dominionName);
            MemberDTO member = toMember(dominion, playerName);
            new MemberSetFlagEvent(sender, dominion, member, flag, value).call();
            MemberSetting.show(sender, dominionName, playerName, pageStr);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * Command to remove a member from a dominion.
     */
    public static SecondaryCommand removeMember = new SecondaryCommand("member_remove", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new CommandArguments.RequiredPlayerArgument(),
            new CommandArguments.OptionalPageArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            removeMember(sender, getArgumentValue(0), getArgumentValue(1), getArgumentValue(2));
        }
    }.needPermission(defaultPermission).register();

    /**
     * Removes a member from a dominion.
     *
     * @param sender       the command sender
     * @param dominionName the name of the dominion
     * @param playerName   the name of the player to be removed
     * @param pageStr      the page number for the member list display
     */
    public static void removeMember(CommandSender sender, String dominionName, String playerName, String pageStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            MemberDTO member = toMember(dominion, playerName);
            new MemberRemovedEvent(sender, dominion, member).call();
            MemberList.show(sender, dominionName, pageStr);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
