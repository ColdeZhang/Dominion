package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.events.dominion.DominionCreateEvent;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.MigrateList;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

import static cn.lunadeer.dominion.utils.CommandUtils.hasPermission;
import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class Migration {

    public static void migrate(CommandSender sender, String[] args) {
        try {
            if (!hasPermission(sender, "dominion.default")) {
                return;
            }
            Player player = playerOnly(sender);
            if (player == null) return;

            if (!Dominion.config.getResidenceMigration()) {
                Notification.error(sender, Translation.Commands_Residence_MigrationDisabled);
                return;
            }

            if (args.length < 2) {
                Notification.error(sender, Translation.Commands_Residence_MigrateUsage);
                return;
            }
            String resName = args[1];
            List<ResMigration.ResidenceNode> res_data;
            if (player.hasPermission("dominion.admin")) {
                res_data = Cache.instance.getResidenceData();   // get all residence data
            } else {
                res_data = Cache.instance.getResidenceData(player.getUniqueId());   // get player's residence data
            }
            if (res_data == null) {
                Notification.error(sender, Translation.Commands_Residence_NoMigrationData);
                return;
            }
            ResMigration.ResidenceNode resNode = res_data.stream().filter(node -> node.name.equals(resName)).findFirst().orElse(null);
            if (resNode == null) {
                Notification.error(sender, Translation.Commands_Residence_NoResidenceDominion);
                return;
            }
            if (!resNode.owner.equals(player.getUniqueId()) && !player.hasPermission("dominion.admin")) {
                Notification.error(sender, Translation.Commands_Residence_ResidenceNotOwner);
                return;
            }
            create(player, resNode, null);
            if (args.length == 3) {
                int parentId = Integer.parseInt(args[2]);
                String[] newArgs = new String[2];
                newArgs[0] = "migrate_list";
                newArgs[1] = String.valueOf(parentId);
                MigrateList.show(sender, newArgs);
            }
        } catch (Exception e) {
            Notification.error(sender, Translation.Commands_Residence_MigrateFailed, e.getMessage());
        }
    }

    private static void create(Player player, ResMigration.ResidenceNode node, DominionDTO parent) {
        BukkitPlayerOperator operator = new BukkitPlayerOperator(player);
        DominionCreateEvent event = new DominionCreateEvent(operator, node.name, node.owner, node.loc1, node.loc2, parent);
        event.setSkipEconomy(true);
        event.callEvent();
        if (!event.isCancelled()) {
            Notification.info(player, Translation.Commands_Residence_MigrateSuccess, node.name);
            DominionDTO dominionCreated = event.getDominion();
            Objects.requireNonNull(Objects.requireNonNull(dominionCreated.setTpLocation(node.tpLoc))
                            .setJoinMessage(node.joinMessage))
                    .setLeaveMessage(node.leaveMessage);
            if (node.children != null) {
                for (ResMigration.ResidenceNode child : node.children) {
                    create(player, child, dominionCreated);
                }
            }
        }
    }
}
