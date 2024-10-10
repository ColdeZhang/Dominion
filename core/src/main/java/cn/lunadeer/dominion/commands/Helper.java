package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.dtos.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class Helper {

    public static List<String> dominionFlags() {
        List<Flag> flags = Flag.getDominionFlagsEnabled();
        return Arrays.asList(flags.stream().map(Flag::getFlagName).toArray(String[]::new));
    }

    public static List<String> playerPrivileges() {
        List<Flag> flags = Flag.getPrivilegeFlagsEnabled();
        return Arrays.asList(flags.stream().map(Flag::getFlagName).toArray(String[]::new));
    }

    /**
     * 获取玩家可管理的领地列表
     *
     * @param sender 命令发送者
     * @return 领地列表
     */
    public static List<String> playerDominions(CommandSender sender) {
        List<String> dominions_name = new ArrayList<>();
        Player player = playerOnly(sender);
        if (player == null) return dominions_name;
        dominions_name.addAll(playerOwnDominions(sender));
        dominions_name.addAll(playerAdminDominions(sender));
        return dominions_name;
    }

    public static List<String> dominionGroups(String dominionName) {
        List<String> groups_name = new ArrayList<>();
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) return groups_name;
        List<GroupDTO> groups = GroupDTO.selectByDominionId(dominion.getId());
        for (GroupDTO group : groups) {
            groups_name.add(group.getNamePlain());
        }
        return groups_name;
    }

    public static List<String> groupPlayers(String domName, String groupName) {
        List<String> players_name = new ArrayList<>();
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) return players_name;
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group == null) return players_name;
        List<MemberDTO> privileges = MemberDTO.selectByDomGroupId(dominion.getId(), group.getId());
        for (MemberDTO privilege : privileges) {
            PlayerDTO player = PlayerDTO.select(privilege.getPlayerUUID());
            if (player == null) continue;
            players_name.add(player.getLastKnownName());
        }
        return players_name;
    }

    public static List<String> playerOwnDominions(CommandSender sender) {
        List<String> dominions_name = new ArrayList<>();
        Player player = playerOnly(sender);
        if (player == null) return dominions_name;
        List<DominionDTO> dominions_own = DominionController.all(player);
        for (DominionDTO dominion : dominions_own) {
            dominions_name.add(dominion.getName());
        }
        return dominions_name;
    }

    public static List<String> playerAdminDominions(CommandSender sender) {
        List<String> dominions_name = new ArrayList<>();
        Player player = playerOnly(sender);
        if (player == null) return dominions_name;
        List<MemberDTO> dominions_admin = MemberDTO.selectAll(player.getUniqueId());
        for (MemberDTO privilege : dominions_admin) {
            if (privilege.getAdmin()) {
                DominionDTO dom = DominionDTO.select(privilege.getDomID());
                if (dom == null) continue;
                dominions_name.add(dom.getName());
            }
        }
        return dominions_name;
    }

    public static List<String> allDominions() {
        List<String> dominions_name = new ArrayList<>();
        List<DominionDTO> dominions = DominionController.all();
        for (DominionDTO dominion : dominions) {
            dominions_name.add(dominion.getName());
        }
        return dominions_name;
    }

    public static List<String> allTemplates(CommandSender sender) {
        List<String> templates_name = new ArrayList<>();
        Player player = playerOnly(sender);
        if (player == null) return templates_name;
        List<PrivilegeTemplateDTO> templates = PrivilegeTemplateDTO.selectAll(player.getUniqueId());
        for (PrivilegeTemplateDTO template : templates) {
            templates_name.add(template.getName());
        }
        return templates_name;
    }

}
