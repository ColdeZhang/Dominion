package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.doos.PlayerDOO;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.uis.tuis.TitleList;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

import static cn.lunadeer.dominion.misc.Asserts.assertDominionOwner;
import static cn.lunadeer.dominion.misc.Converts.*;

public class GroupTitleCommand {

    public static class GroupTitleCommandText extends ConfigurationPart {
        public String groupNotBelonging = "Don't belong to group {0}.";
        public String usingTitleSuccess = "Using title {0} successfully.";
        public String usingTitleFail = "Failed to use title, reason: {0}";
    }

    public static SecondaryCommand useTitle = new SecondaryCommand("title_use", List.of(
            new CommandArguments.PlayerTitleIdArgument(),
            new CommandArguments.OptionalPageArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            useTitle(sender, getArgumentValue(0), getArgumentValue(1));
        }
    }.needPermission("dominion.default").register();

    /**
     * Uses a title for a player.
     * This method allows a player to use a specific group title. It verifies the player's ownership or membership
     * in the dominion associated with the group title and sets the title for the player if the checks pass.
     *
     * @param sender          The command sender.
     * @param groupTitleIdStr The ID of the group title as a string. -1 for disuse current title.
     * @param pageStr         The page number as a string.
     */
    public static void useTitle(CommandSender sender, String groupTitleIdStr, String pageStr) {
        try {
            Player player = toPlayer(sender);
            int titleId = toIntegrity(groupTitleIdStr);
            PlayerDTO playerDto = toPlayerDTO(player.getUniqueId());
            GroupDTO group = toGroupDTO(titleId);
            DominionDTO dominion = toDominionDTO(group.getDomID());
            try {
                assertDominionOwner(player, dominion);
            } catch (Exception e) {
                MemberDTO member = CacheManager.instance.getMember(dominion, player);
                if (member == null) {
                    throw new DominionException(Language.groupTitleCommandText.groupNotBelonging, group.getNamePlain());
                }
                if (!Objects.equals(member.getGroupId(), group.getId())) {
                    throw new DominionException(Language.groupTitleCommandText.groupNotBelonging, group.getNamePlain());
                }
            }
            ((PlayerDOO) playerDto).setUsingGroupTitleID(group.getId());

            Notification.info(sender, Language.groupTitleCommandText.usingTitleSuccess, groupTitleIdStr);
            TitleList.show(sender, pageStr);
        } catch (Exception e) {
            Notification.error(sender, Language.groupTitleCommandText.usingTitleFail, e.getMessage());
        }
    }

}
