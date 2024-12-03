package cn.lunadeer.dominion.uis.tuis;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.ViewStyles;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class Menu {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;

        int page = 1;
        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
            }
        }

        Line create = Line.create()
                .append(Button.create(Translation.TUI_Menu_CreateDominionButton).setExecuteCommand("/dominion cui_create").build())
                .append(Translation.TUI_Menu_CreateDominionDescription);
        Line list = Line.create()
                .append(Button.create(Translation.TUI_Menu_MyDominionButton).setExecuteCommand("/dominion list").build())
                .append(Translation.TUI_Menu_MyDominionDescription);
        Line title = Line.create()
                .append(Button.create(Translation.TUI_Menu_TitleListButton).setExecuteCommand("/dominion title_list").build())
                .append(Translation.TUI_Menu_TitleListDescription);
        Line template = Line.create()
                .append(Button.create(Translation.TUI_Menu_TemplateListButton).setExecuteCommand("/dominion template list").build())
                .append(Translation.TUI_Menu_TemplateListDescription);
        Line help = Line.create()
                .append(Button.create(Translation.TUI_Menu_CommandHelpButton).setOpenURL(
                        String.format("https://dominion.lunadeer.cn/%s/command-list.html", Dominion.config.getLanguage())
                ).build())
                .append(Translation.TUI_Menu_CommandHelpDescription);
        Line link = Line.create()
                .append(Button.create(Translation.TUI_Menu_DocumentButton).setOpenURL(
                        String.format("https://dominion.lunadeer.cn/%s", Dominion.config.getLanguage())
                ).build())
                .append(Translation.TUI_Menu_DocumentDescription);
        Line migrate = Line.create()
                .append(Button.create(Translation.TUI_Menu_MigrateButton).setExecuteCommand("/dominion migrate_list").build())
                .append(Translation.TUI_Menu_MigrateDescription);
        Line all = Line.create()
                .append(Button.create(Translation.TUI_Menu_AllDominionButton).setExecuteCommand("/dominion all_dominion").build())
                .append(Translation.TUI_Menu_AllDominionDescription);
        Line reload_cache = Line.create()
                .append(Button.create(Translation.TUI_Menu_ReloadCacheButton).setExecuteCommand("/dominion reload_cache").build())
                .append(Translation.TUI_Menu_ReloadCacheDescription);
        Line reload_config = Line.create()
                .append(Button.create(Translation.TUI_Menu_ReloadConfigButton).setExecuteCommand("/dominion reload_config").build())
                .append(Translation.TUI_Menu_ReloadConfigDescription);
        ListView view = ListView.create(10, "/dominion menu");
        view.title(Translation.TUI_Menu_Title);
        view.navigator(Line.create().append(Translation.TUI_Navigation_Menu));
        view.add(create);
        view.add(list);
        if (Dominion.config.getGroupTitleEnable()) view.add(title);
        view.add(template);
        view.add(help);
        view.add(link);
        if (Dominion.config.getResidenceMigration()) {
            view.add(migrate);
        }
        if (player.hasPermission("dominion.admin")) {
            view.add(Line.create().append(""));
            view.add(Line.create().append(Component.text(Translation.TUI_Menu_OpOnlySection.trans(), ViewStyles.main_color)));
            view.add(all);
            view.add(reload_cache);
            view.add(reload_config);
        }
        view.showOn(player, page);
    }
}
