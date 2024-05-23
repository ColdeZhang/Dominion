package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

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
        List<PlayerPrivilegeDTO> dominions_admin = PlayerPrivilegeDTO.selectAll(player.getUniqueId());
        for (PlayerPrivilegeDTO privilege : dominions_admin) {
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

}
