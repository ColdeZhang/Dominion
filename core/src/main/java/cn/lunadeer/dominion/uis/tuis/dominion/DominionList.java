package cn.lunadeer.dominion.uis.tuis.dominion;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.DominionNode;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.commands.DominionOperateCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.ViewStyles;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;

public class DominionList {

    public static class DominionListTuiText extends ConfigurationPart {
        public String title = "Your Dominions";
        public String button = "DOMINIONS";
        public String description = "List all of your dominions.";
        public String deleteButton = "DELETE";
        public String adminSection = "Your admin dominions section.";
    }

    public static SecondaryCommand list = new SecondaryCommand("list", List.of(
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
        return (ListViewButton) new ListViewButton(Language.dominionListTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, pageStr);
            }
        }.needPermission(defaultPermission);
    }

    public static void show(CommandSender sender, String pageStr) {
        try {
            Player player = toPlayer(sender);
            int page = toIntegrity(pageStr);
            ListView view = ListView.create(10, button(sender));

            view.title(Language.dominionListTuiText.title);
            view.navigator(Line.create()
                    .append(MainMenu.button(sender).build())
                    .append(Language.dominionListTuiText.button));
            view.addLines(BuildTreeLines(sender, DominionNode.BuildNodeTree(-1, Cache.instance.getPlayerDominions(player.getUniqueId())), 0));
            List<DominionDTO> admin_dominions = Cache.instance.getPlayerAdminDominions(player.getUniqueId());
            if (!admin_dominions.isEmpty()) {
                view.add(Line.create().append(""));
                view.add(Line.create().append(Component.text(Language.dominionListTuiText.adminSection, ViewStyles.main_color)));
            }
            for (DominionDTO dominion : admin_dominions) {
                TextComponent manage = DominionManage.button(sender, dominion.getName()).build();
                view.add(Line.create().append(manage).append(dominion.getName()));
            }
            view.showOn(player, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    public static List<Line> BuildTreeLines(CommandSender sender, List<DominionNode> dominionTree, Integer depth) {
        List<Line> lines = new ArrayList<>();
        StringBuilder prefix = new StringBuilder();
        prefix.append(" | ".repeat(Math.max(0, depth)));
        for (DominionNode node : dominionTree) {
            TextComponent manage = DominionManage.button(sender, node.getDominion().getName()).green().build();
            TextComponent delete = new FunctionalButton(Language.dominionListTuiText.deleteButton) {
                @Override
                public void function() {
                    DominionOperateCommand.delete(sender, node.getDominion().getName(), "");
                }
            }.red().build();
            Line line = Line.create().append(delete).append(manage).append(prefix + node.getDominion().getName());
            lines.add(line);
            lines.addAll(BuildTreeLines(sender, node.getChildren(), depth + 1));
        }
        return lines;
    }
}
