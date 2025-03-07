package cn.lunadeer.dominion.uis.tuis;

import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import org.bukkit.command.CommandSender;

import java.util.List;

import static cn.lunadeer.dominion.Dominion.adminPermission;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.uis.tuis.dominion.DominionList.BuildTreeLines;

public class AllDominion {

    public static class AllDominionTuiText extends ConfigurationPart {
        public String title = "All Dominions";
        public String description = "List all dominions.";
        public String button = "LIST ALL";
    }

    public static SecondaryCommand listAll = new SecondaryCommand("list_all", List.of(
            new CommandArguments.OptionalPageArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            show(sender, getArgumentValue(0));
        }
    }.needPermission(adminPermission).register();

    public static ListViewButton button(CommandSender sender) {
        return (ListViewButton) new ListViewButton(Language.allDominionTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, pageStr);
            }
        }.needPermission(adminPermission);
    }

    public static void show(CommandSender sender, String pageStr) {
        try {
            int page = toIntegrity(pageStr);
            ListView view = ListView.create(10, button(sender));

            view.title(Language.allDominionTuiText.title);
            view.navigator(Line.create()
                    .append(MainMenu.button(sender).build())
                    .append(Language.allDominionTuiText.button));
            view.addLines(BuildTreeLines(sender, CacheManager.instance.getCache().getDominionCache().getAllDominionNodes(), 0));
            view.showOn(sender, page);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }


    }
}
