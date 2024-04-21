package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class Helper {

    public static List<String> dominionFlags() {
        return Arrays.asList(
                "anchor", "animal_killing", "anvil",
                "beacon", "bed", "brew", "break", "button",
                "cake", "container", "craft", "creeper_explode", "comparer",
                "door", "dye",
                "egg", "enchant", "ender_man", "ender_pearl",
                "feed", "fire_spread", "flow_in_protection",
                "glow",
                "harvest", "honey", "hook", "hopper",
                "ignite",
                "lever",
                "mob_drop_item", "monster_killing", "move",
                "place", "pressure",
                "riding", "repeater",
                "shear", "shoot",
                "tnt_explode", "trade", "trample",
                "vehicle_destroy",
                "vehicle_spawn",
                "wither_spawn");
    }

    public static List<String> playerPrivileges() {
        return Arrays.asList(
                "admin", "anchor", "animal_killing", "anvil",
                "beacon", "bed", "brew", "break", "button",
                "cake", "container", "craft", "comparer",
                "door", "dye",
                "egg", "enchant", "ender_pearl",
                "feed",
                "glow",
                "harvest", "honey", "hook", "hopper",
                "ignite",
                "lever",
                "monster_killing", "move",
                "place", "pressure", "riding", "repeater",
                "shear", "shoot",
                "trade",
                "vehicle_destroy",
                "vehicle_spawn");
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
