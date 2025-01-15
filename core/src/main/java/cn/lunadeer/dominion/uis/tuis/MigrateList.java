package cn.lunadeer.dominion.uis.tuis;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.dominion.utils.TuiUtils;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class MigrateList {

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;

        if (!Dominion.config.getResidenceMigration()) {
            Notification.error(sender, Translation.Commands_Residence_MigrationDisabled);
            return;
        }

        int page = TuiUtils.getPage(args, 1);

        ListView view = ListView.create(10, "/dominion migrate_list");

        view.title(Translation.TUI_Migrate_Title);
        view.navigator(Line.create().append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build()).append(Translation.TUI_Navigation_MigrateList));

        List<ResMigration.ResidenceNode> res_data;

        if (player.hasPermission("dominion.admin")) {
            res_data = Cache.instance.getResidenceData();   // get all residence data
        } else {
            res_data = Cache.instance.getResidenceData(player.getUniqueId());   // get player's residence data
        }

        if (res_data == null) {
            view.add(Line.create().append(Translation.TUI_Migrate_NoData));
        } else {
            view.addLines(BuildTreeLines(res_data, 0, page));
        }

        view.showOn(player, page);
    }

    public static List<Line> BuildTreeLines(List<ResMigration.ResidenceNode> dominionTree, Integer depth, int page) {
        List<Line> lines = new ArrayList<>();
        StringBuilder prefix = new StringBuilder();
        prefix.append(" | ".repeat(Math.max(0, depth)));
        for (ResMigration.ResidenceNode node : dominionTree) {
            Button migrate = Button.create(Translation.TUI_Migrate_Button).setExecuteCommand("/dominion migrate " + node.name + " " + page);
            Line line = Line.create();
            if (depth == 0) {
                line.append(migrate.build());
            } else {
                line.append(migrate.setDisabled(Translation.TUI_Migrate_SubDominion).build());
            }
            line.append(prefix + node.name);
            lines.add(line);
            lines.addAll(BuildTreeLines(node.children, depth + 1, page));
        }
        return lines;
    }
}
