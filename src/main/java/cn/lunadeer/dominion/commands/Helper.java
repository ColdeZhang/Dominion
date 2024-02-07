package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.controllers.GroupController;
import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.STUI.Button;
import cn.lunadeer.dominion.utils.STUI.Line;
import cn.lunadeer.dominion.utils.STUI.ListView;
import cn.lunadeer.dominion.utils.STUI.View;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.controllers.Apis.getPlayerCurrentDominion;

public class Helper {

    public static List<String> dominionFlags() {
        return Arrays.asList(
                "anchor", "animal_killing", "anvil", "beacon", "bed", "brew", "button", "cake", "container", "craft",
                "creeper_explode", "diode", "door", "dye", "egg", "enchant", "ender_pearl", "feed", "fire_spread",
                "flow_in_protection", "glow", "grow", "honey", "hook", "ignite", "mob_killing", "move", "place",
                "pressure", "riding", "shear", "shoot", "tnt_explode", "trade", "vehicle_destroy", "wither_spawn",
                "harvest");
    }

    public static List<String> groupPrivileges() {
        return Arrays.asList(
                "anchor", "animal_killing", "anvil", "beacon", "bed", "brew", "button", "cake", "container", "craft",
                "diode", "door", "dye", "egg", "enchant", "ender_pearl", "feed", "glow", "honey", "hook", "ignite",
                "mob_killing", "move", "place", "pressure", "riding", "shear", "shoot", "trade", "vehicle_destroy",
                "harvest");
    }

    public static List<String> playerPrivileges() {
        List<String> privileges = new ArrayList<>(groupPrivileges());
        privileges.add("admin");
        return privileges;
    }

    public static List<String> playerDominions(CommandSender sender) {
        List<String> dominions_name = new ArrayList<>();
        Player player = playerOnly(sender);
        if (player == null) return dominions_name;
        List<DominionDTO> dominions = DominionController.all(player);
        for (DominionDTO dominion : dominions) {
            dominions_name.add(dominion.getName());
        }
        return dominions_name;
    }

    public static List<String> playerGroups(CommandSender sender) {
        List<String> groups_name = new ArrayList<>();
        Player player = playerOnly(sender);
        if (player == null) return groups_name;
        List<PrivilegeTemplateDTO> groups = GroupController.all(player);
        for (PrivilegeTemplateDTO group : groups) {
            groups_name.add(group.getName());
        }
        return groups_name;
    }


}
