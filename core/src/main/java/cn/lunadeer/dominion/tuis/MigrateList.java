package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class MigrateList {

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;

        if (!Dominion.config.getResidenceMigration()) {
            Notification.error(sender, "Residence 迁移功能没有开启");
            return;
        }

        int page = Apis.getPage(args, 1);

        ListView view = ListView.create(10, "/dominion migrate_list");

        view.title("从 Residence 迁移数据");
        view.navigator(Line.create().append(Button.create("主菜单").setExecuteCommand("/dominion menu").build()).append("Res数据"));

        List<ResMigration.ResidenceNode> res_data = Cache.instance.getResidenceData(player.getUniqueId());

        if (res_data == null) {
            view.add(Line.create().append("你没有可迁移的数据"));
        } else {
            view.addLines(BuildTreeLines(res_data, 0, page));
        }

        view.showOn(player, page);
    }

    public static List<Line> BuildTreeLines(List<ResMigration.ResidenceNode> dominionTree, Integer depth, int page) {
        List<Line> lines = new ArrayList<>();
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            prefix.append(" | ");
        }
        for (ResMigration.ResidenceNode node : dominionTree) {
            TextComponent migrate = Button.create("迁移").setExecuteCommand("/dominion migrate " + node.name + " " + page).build();
            Line line = Line.create();
            if (depth == 0) {
                line.append(migrate);
            } else {
                line.append(Component.text("[迁移]",
                                Style.style(TextColor.color(190, 190, 190),
                                        TextDecoration.STRIKETHROUGH))
                        .hoverEvent(Component.text("子领地无法手动迁移，会随父领地自动迁移")));
            }
            line.append(prefix + node.name);
            lines.add(line);
            lines.addAll(BuildTreeLines(node.children, depth + 1, page));
        }
        return lines;
    }
}
