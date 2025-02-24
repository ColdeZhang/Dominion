package cn.lunadeer.dominion.uis.tuis;

import cn.lunadeer.dominion.commands.AdministratorCommand;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.uis.cuis.CreateDominion;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionList;
import cn.lunadeer.dominion.uis.tuis.template.TemplateList;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.ListView;
import cn.lunadeer.dominion.utils.stui.ViewStyles;
import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.UrlButton;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.Dominion.adminPermission;
import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;


public class MainMenu {

    public static class MenuTuiText extends ConfigurationPart {
        public String title = "Dominion Menu";
        public String button = "MENU";
        public String adminOnlySection = "Only admin can see this section";
        public String documentButton = "DOCUMENT";
        public String documentDescription = "Open the documentation external link.";
        public String commandHelpButton = "COMMAND HELP";
        public String commandHelpDescription = "Open the command help external link.";
    }

    public static SecondaryCommand menu = new SecondaryCommand("menu", List.of(
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
        return (ListViewButton) new ListViewButton(Language.menuTuiText.button) {
            @Override
            public void function(String pageStr) {
                show(sender, pageStr);
            }
        }.needPermission(defaultPermission);
    }

    public static void show(CommandSender sender, String pageStr) {
        Player player = toPlayer(sender);
        int page = toIntegrity(pageStr);

        Line create = Line.create()
                .append(CreateDominion.button(sender).build())
                .append(Language.createDominionCuiText.description);
        Line list = Line.create()
                .append(DominionList.button(sender).build())
                .append(Language.dominionListTuiText.description);
        Line title = Line.create()
                .append(TitleList.button(sender).build())
                .append(Language.titleListTuiText.description);
        Line template = Line.create()
                .append(TemplateList.button(sender).build())
                .append(Language.templateListTuiText.description);
        Line help = Line.create()
                .append(new UrlButton(Language.menuTuiText.commandHelpButton, Configuration.externalLinks.commandHelp).build())
                .append(Language.menuTuiText.commandHelpDescription);
        Line link = Line.create()
                .append(new UrlButton(Language.menuTuiText.documentButton, Configuration.externalLinks.documentation).build())
                .append(Language.menuTuiText.documentDescription);
        Line migrate = Line.create()
                .append(MigrateList.button(sender).build())
                .append(Language.migrateListText.description);
        Line all = Line.create()
                .append(AllDominion.button(sender).build())
                .append(Language.allDominionTuiText.description);
        Line reload_cache = Line.create()
                .append(AdministratorCommand.reloadCacheButton(sender).build())
                .append(Language.administratorCommandText.reloadCacheDescription);
        Line reload_config = Line.create()
                .append(AdministratorCommand.reloadConfigButton(sender).build())
                .append(Language.administratorCommandText.reloadConfigDescription);
        ListView view = ListView.create(10, button(sender));
        view.title(Language.menuTuiText.title);
        view.navigator(Line.create().append(Language.menuTuiText.button));
        view.add(create);
        view.add(list);
        if (Configuration.groupTitle.enable) view.add(title);
        view.add(template);
        if (!Configuration.externalLinks.commandHelp.isEmpty()) view.add(help);
        if (!Configuration.externalLinks.documentation.isEmpty()) view.add(link);
        if (Configuration.residenceMigration) {
            view.add(migrate);
        }
        if (player.hasPermission(adminPermission)) {
            view.add(Line.create().append(""));
            view.add(Line.create().append(Component.text(Language.menuTuiText.adminOnlySection, ViewStyles.main_color)));
            view.add(all);
            view.add(reload_cache);
            view.add(reload_config);
        }
        view.showOn(player, page);
    }
}
