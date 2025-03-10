package cn.lunadeer.dominion.uis.tuis;

import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.commands.MigrationCommand;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.Dominion.adminPermission;
import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;


public class MigrateList {
    public static class MigrateListText extends ConfigurationPart {
        public String title = "Migrate From Residence";
        public String description = "Migrate residence data to dominion.";
        public String button = "MIGRATE";
        public String notEnabled = "Residence migration is not enabled.";
        public String noData = "No data to migrate.";

        public String cantMigrate = "Sub-residence will be migrated with the parent.";
    }

    public static SecondaryCommand migrateList = new SecondaryCommand("migrate_list", List.of(
            new CommandArguments.OptionalPageArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            try {
                show(sender, getArgumentValue(0));
            } catch (Exception e) {
                Notification.error(sender, e.getMessage());
            }
        }
    }.needPermission(defaultPermission).register();

    public static ListViewButton button(CommandSender sender) {
        return (ListViewButton) new ListViewButton(Language.migrateListText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, pageStr);
            }
        }.needPermission(defaultPermission);
    }

    public static void show(CommandSender sender, String pageStr) {
        try {
            if (!Configuration.residenceMigration) {
                Notification.error(sender, Language.migrateListText.notEnabled);
                return;
            }
            Player player = toPlayer(sender);
            int page = toIntegrity(pageStr);
            ListView view = ListView.create(10, button(sender));
            view.title(Language.migrateListText.title);
            view.navigator(Line.create()
                    .append(MainMenu.button(sender).build())
                    .append(Language.migrateListText.button));

            List<ResMigration.ResidenceNode> res_data;

            if (player.hasPermission(adminPermission)) {
                res_data = CacheManager.instance.getResidenceCache().getResidenceData();   // get all residence data
            } else {
                res_data = CacheManager.instance.getResidenceCache().getResidenceData(player.getUniqueId());   // get player's residence data
            }

            if (res_data == null) {
                view.add(Line.create().append(Language.migrateListText.noData));
            } else {
                view.addLines(BuildTreeLines(sender, res_data, 0, page));
            }

            view.showOn(player, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    public static List<Line> BuildTreeLines(CommandSender sender, List<ResMigration.ResidenceNode> dominionTree, Integer depth, int page) {
        List<Line> lines = new ArrayList<>();
        StringBuilder prefix = new StringBuilder();
        prefix.append(" | ".repeat(Math.max(0, depth)));
        for (ResMigration.ResidenceNode node : dominionTree) {
            ListViewButton migrate = MigrationCommand.button(sender, node.name);
            Line line = Line.create();
            if (depth == 0) {
                line.append(migrate.build());
            } else {
                line.append(migrate.setDisabled(Language.migrateListText.cantMigrate).build());
            }
            line.append(prefix + node.name);
            lines.add(line);
            lines.addAll(BuildTreeLines(sender, node.children, depth + 1, page));
        }
        return lines;
    }
}
