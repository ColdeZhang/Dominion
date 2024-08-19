package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.tuis.TitleList;
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
            Notification.error(sender, "用法: /dominion use_title <权限组ID>");
            return;
        }
        try {
            int id = Integer.parseInt(args[1]);
            PlayerDTO player = PlayerDTO.get(bukkit_player);
            if (id == -1) {
                player.setUsingGroupTitleID(id);
                Notification.info(sender, "成功卸下权限组称号");
            } else {
                GroupDTO group = Cache.instance.getGroup(id);
                if (group == null) {
                    Notification.error(sender, "权限组不存在");
                    return;
                }
                DominionDTO dominion = Cache.instance.getDominion(group.getDomID());
                if (dominion == null) {
                    Notification.error(sender, "权限组 %s 所属领地不存在", group.getName());
                    return;
                }
                if (!dominion.getOwner().equals(bukkit_player.getUniqueId())) {
                    MemberDTO member = Cache.instance.getMember(bukkit_player, dominion);
                    if (member == null) {
                        Notification.error(sender, "你不是 %s 的成员，无法使用其称号", dominion.getName());
                        return;
                    }
                    if (!Objects.equals(member.getGroupId(), group.getId())) {
                        Notification.error(sender, "你不属于权限组 %s，无法使用其称号", group.getName());
                        return;
                    }
                }
                player.setUsingGroupTitleID(group.getId());
                Notification.info(sender, "成功使用权限组 %s 称号", group.getName());
            }
            int page = getPage(args, 2);
            TitleList.show(sender, page);
        } catch (Exception e) {
            Notification.error(sender, "使用称号失败: " + e.getMessage());
        }
    }

}
