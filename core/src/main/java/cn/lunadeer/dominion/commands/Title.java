package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.TitleList;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static cn.lunadeer.dominion.utils.CommandUtils.hasPermission;
import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getPage;

public class Title {

    public static void use_title(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        Player bukkit_player = playerOnly(sender);
        if (bukkit_player == null) return;
        if (args.length < 2) {
            Notification.error(sender, Translation.Commands_Title_UseTitleUsage);
            return;
        }
        try {
            int id = Integer.parseInt(args[1]);
            PlayerDTO player = PlayerDTO.get(bukkit_player);
            if (id == -1) {
                player.setUsingGroupTitleID(id);
                Notification.info(sender, Translation.Commands_Title_RemoveTitleSuccess);
            } else {
                GroupDTO group = Cache.instance.getGroup(id);
                if (group == null) {
                    Notification.error(sender, Translation.Commands_Title_GroupNotExist);
                    return;
                }
                DominionDTO dominion = Cache.instance.getDominion(group.getDomID());
                if (dominion == null) {
                    Notification.error(sender, Translation.Commands_Title_GroupDominionNotExist, group.getNamePlain());
                    return;
                }
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
                player.setUsingGroupTitleID(group.getId());
                Notification.info(sender, Translation.Commands_Title_UseTitleSuccess, group.getNamePlain());
            }
            int page = getPage(args, 2);
            TitleList.show(sender, page);
        } catch (Exception e) {
            Notification.error(sender, Translation.Commands_Title_UseTitleFailed, e.getMessage());
        }
    }

}
