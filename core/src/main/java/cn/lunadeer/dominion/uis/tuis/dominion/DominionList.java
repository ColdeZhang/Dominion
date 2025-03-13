package cn.lunadeer.dominion.uis.tuis.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.cache.DominionNode;
import cn.lunadeer.dominion.cache.server.ServerCache;
import cn.lunadeer.dominion.commands.DominionOperateCommand;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.managers.MultiServerManager;
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
import static cn.lunadeer.dominion.managers.TeleportManager.teleportToDominion;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class DominionList {

    public static class DominionListTuiText extends ConfigurationPart {
        public String title = "Your Dominions";
        public String button = "DOMINIONS";
        public String description = "List all of your dominions.";
        public String deleteButton = "DELETE";
        public String adminSection = "Your admin dominions section.";
        public String serverSection = "Server {0} dominions section.";
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
            List<DominionNode> dominionNodes = CacheManager.instance.getCache().getDominionCache().getPlayerDominionNodes(player.getUniqueId());
            // Show dominions on current server
            view.addLines(BuildTreeLines(sender, dominionNodes, 0));
            // Show admin dominions on this server
            List<DominionDTO> admin_dominions = CacheManager.instance.getCache().getDominionCache().getPlayerAdminDominionDTOs(player.getUniqueId());
            if (!admin_dominions.isEmpty()) {
                view.add(Line.create().append(""));
                view.add(Line.create().append(Component.text(Language.dominionListTuiText.adminSection, ViewStyles.main_color)));
                for (DominionDTO dominion : admin_dominions) {
                    TextComponent manage = DominionManage.button(sender, dominion.getName()).build();
                    view.add(Line.create().append(manage).append(dominion.getName()));
                }
            }
            // Show dominions on other servers
            if (Configuration.multiServer.enable) {
                for (ServerCache serverCache : CacheManager.instance.getOtherServerCaches().values()) {
                    view.add(Line.create().append(""));
                    view.add(Line.create().append(
                            Component.text(
                                    formatString(
                                            Language.dominionListTuiText.serverSection, MultiServerManager.instance.getServerName(serverCache.getServerId())
                                    ), ViewStyles.main_color
                            ))
                    );
                    view.addLines(BuildTreeLines(sender, serverCache.getDominionCache().getPlayerDominionNodes(player.getUniqueId()), 0));
                    // Show admin dominions on other servers
                    List<DominionDTO> admin_dominions_others = serverCache.getDominionCache().getPlayerAdminDominionDTOs(player.getUniqueId());
                    if (!admin_dominions_others.isEmpty()) {
                        for (DominionDTO dominion : admin_dominions_others) {
                            TextComponent manage = DominionManage.button(sender, dominion.getName()).build();
                            view.add(Line.create().append(manage).append(dominion.getName()));
                        }
                    }
                }
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
            TextComponent tp = new FunctionalButton("TP") {
                @Override
                public void function() {
                    if (sender instanceof Player player)
                        teleportToDominion(player, node.getDominion());
                }
            }.build();
            Line line = Line.create().append(delete).append(manage).append(tp).append(prefix + node.getDominion().getName());
            lines.add(line);
            lines.addAll(BuildTreeLines(sender, node.getChildren(), depth + 1));
        }
        return lines;
    }
}
