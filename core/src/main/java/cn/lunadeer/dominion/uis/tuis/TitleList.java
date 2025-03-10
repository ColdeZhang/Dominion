package cn.lunadeer.dominion.uis.tuis;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.commands.GroupTitleCommand;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class TitleList {

    public static class TitleListTuiText extends ConfigurationPart {
        public String title = "Group Title List";
        public String description = "List of group titles you can use.";
        public String button = "TITLES";
        public String useButton = "USE";
        public String disuseButton = "DISUSE";
        public String fromDominion = "From dominion {0}";
    }

    public static SecondaryCommand titleList = new SecondaryCommand("title_list", List.of(
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
        return (ListViewButton) new ListViewButton(Language.titleListTuiText.button) {
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

            view.title(Language.titleListTuiText.title);
            view.navigator(Line.create()
                    .append(MainMenu.button(sender).build())
                    .append(Language.titleListTuiText.button));

            List<GroupDTO> groups = CacheManager.instance.getPlayerCache().getPlayerGroupTitleList(player.getUniqueId());
            Integer usingId = CacheManager.instance.getPlayerCache().getPlayerUsingTitleId(player.getUniqueId());
            GroupDTO using = CacheManager.instance.getGroup(usingId);

            for (GroupDTO group : groups) {
                DominionDTO dominion = CacheManager.instance.getDominion(group.getDomID());
                if (dominion == null) {
                    continue;
                }
                Line line = Line.create();
                line.append(Component.text(group.getId() + ". "));
                if (using != null && using.getId().equals(group.getId())) {
                    line.append(new FunctionalButton(Language.titleListTuiText.useButton) {
                        @Override
                        public void function() {
                            GroupTitleCommand.useTitle(sender, "-1", pageStr);
                        }
                    }.needPermission(defaultPermission).red().build());
                } else {
                    line.append(new FunctionalButton(Language.titleListTuiText.disuseButton) {
                        @Override
                        public void function() {
                            GroupTitleCommand.useTitle(sender, group.getId().toString(), pageStr);
                        }
                    }.needPermission(defaultPermission).green().build());
                }
                line.append(group.getNameColoredComponent().hoverEvent(Component.text(formatString(Language.titleListTuiText.fromDominion, dominion.getName()))));
                view.add(line);
            }

            view.showOn(player, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

}
