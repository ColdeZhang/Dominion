package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.tuis.MigrateList;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class Migration {

    public static void migrate(CommandSender sender, String[] args) {
        try {
            Player player = playerOnly(sender);
            if (player == null) return;

            if (!Dominion.config.getResidenceMigration()) {
                Notification.error(sender, "Residence 迁移功能没有开启");
                return;
            }

            if (args.length < 2) {
                Notification.error(sender, "用法: /dominion migrate <res领地名称>");
                return;
            }
            String resName = args[1];
            List<ResMigration.ResidenceNode> res_data = Cache.instance.getResidenceData(player.getUniqueId());
            if (res_data == null) {
                Notification.error(sender, "你没有可迁移的数据");
                return;
            }
            ResMigration.ResidenceNode resNode = res_data.stream().filter(node -> node.name.equals(resName)).findFirst().orElse(null);
            if (resNode == null) {
                Notification.error(sender, "未找到指定的 Residence 领地");
                return;
            }
            if (!resNode.owner.equals(player.getUniqueId())) {
                Notification.error(sender, "你不是该领地的所有者，无法迁移此领地");
                return;
            }
            create(player, resNode, "");
            if (args.length == 3 ) {
                int parentId = Integer.parseInt(args[2]);
                String[] newArgs = new String[2];
                newArgs[0] = "migrate_list";
                newArgs[1] = String.valueOf(parentId);
                MigrateList.show(sender, newArgs);
            }
        } catch (Exception e) {
            Notification.error(sender, "迁移失败: " + e.getMessage());
        }
    }

    private static void create(Player player, ResMigration.ResidenceNode node, String parentName) {
        BukkitPlayerOperator operator = new BukkitPlayerOperator(player);
        operator.getResponse().thenAccept(result -> {
            if (Objects.equals(result.getStatus(), BukkitPlayerOperator.Result.SUCCESS)) {
                DominionDTO dominion = DominionDTO.select(node.name);
                if (dominion == null) {
                    return;
                }
                dominion.setTpLocation(node.tpLoc)
                        .setJoinMessage(node.joinMessage)
                        .setLeaveMessage(node.leaveMessage);
                for (String msg : result.getMessages()) {
                    Notification.info(player, msg);
                }
                Notification.info(player, "领地 " + node.name + " 已从 Residence 迁移至 Dominion");
                if (node.children != null) {
                    for (ResMigration.ResidenceNode child : node.children) {
                        create(player, child, node.name);
                    }
                }
            } else if (Objects.equals(result.getStatus(), BukkitPlayerOperator.Result.WARNING)) {
                for (String msg : result.getMessages()) {
                    Notification.warn(player, msg);
                }
            } else {
                for (String msg : result.getMessages()) {
                    Notification.error(player, msg);
                }
            }
        });
        DominionController.create(operator, node.name, node.loc1, node.loc2, parentName, true);
    }
}
