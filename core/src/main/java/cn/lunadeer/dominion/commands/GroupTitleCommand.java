package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.configuration.Language;
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

import static cn.lunadeer.dominion.misc.Converts.*;

public class GroupTitleCommand {

    public static class GroupTitleCommandText extends ConfigurationPart {
        public String groupNotBelonging = "Don't belong to group {0}.";
        public String usingTitleSuccess = "Using title {0} successfully.";
        public String usingTitleFail = "Failed to use title, reason: {0}";
    }

    public static SecondaryCommand useTitle = new SecondaryCommand("useTitle", List.of(
            new CommandArguments.PlayerGroupsArgument(),
            new CommandArguments.OptionalPageArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            useTitle(sender, getArgumentValue(0), getArgumentValue(1));
        }
    }.needPermission("dominion.default").register();

    public static void useTitle(CommandSender sender, String groupPlainName, String pageStr) {
        try {
            Player player = toPlayer(sender);
            int page = toIntegrity(pageStr);
            PlayerDTO playerDto = toPlayerDTO(player.getUniqueId());
            GroupDTO group = Cache.instance.getBelongGroupsOf(playerDto.getUuid()).stream()
                    .filter(g -> g.getNamePlain().equals(groupPlainName))
                    .findFirst()
                    .orElse(null);
            if (group == null) {
                throw new DominionException(Language.groupTitleCommandText.groupNotBelonging, groupPlainName);
            }
            DominionDTO dominion = toDominionDTO(group.getDominionId());
            if (!dominion.getOwner().equals(bukkit_player.getUniqueId())) {
                MemberDTO member = Cache.instance.getMember(bukkit_player, dominion);
                if (member == null) {
                    Notification.error(sender, Translation.Commands_Title_NotDominionMember, dominion.getName());
                    return;
                }
                if (!Objects.equals(member.getGroupId(), group.getId())) {
                    Notification.error(sender, Translation.Commands_Title_NotGroupMember, group.getNamePlain());
                    return;
                }
            }
            ((cn.lunadeer.dominion.dtos.PlayerDTO) player).setUsingGroupTitleID(group.getId());

            Notification.info(sender, Language.groupTitleCommandText.usingTitleSuccess, groupPlainName);
            TitleList.show(sender, page);
        } catch (Exception e) {
            Notification.error(sender, Language.groupTitleCommandText.usingTitleFail, e.getMessage());
        }
    }

}
