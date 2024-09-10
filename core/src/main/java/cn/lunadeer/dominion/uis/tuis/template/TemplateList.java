package cn.lunadeer.dominion.uis.tuis.template;

import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getPage;

public class TemplateList {

    public static void show(CommandSender sender) {
        show(sender, 1);
    }

    public static void show(CommandSender sender, int page) {
        show(sender, new String[]{"", "", String.valueOf(page)});
    }

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args, 2);
        ListView view = ListView.create(10, "/dominion template list");

        List<PrivilegeTemplateDTO> templates = PrivilegeTemplateDTO.selectAll(player.getUniqueId());
        view.title(Translation.TUI_TemplateList_Title);
        view.navigator(Line.create().append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build()).append(Translation.TUI_Navigation_TemplateList));

        Button create = Button.create(Translation.TUI_TemplateList_CreateButton).setExecuteCommand("/dominion cui_template_create")
                .setHoverText(Translation.TUI_TemplateList_CreateDescription);

        view.add(Line.create().append(create.build()));

        for (PrivilegeTemplateDTO template : templates) {
            Button manage = Button.createGreen(Translation.TUI_EditButton).setExecuteCommand("/dominion template setting " + template.getName());
            Button delete = Button.createRed(Translation.TUI_DeleteButton).setExecuteCommand("/dominion template delete " + template.getName());
            Line line = Line.create()
                    .append(delete.build())
                    .append(manage.build())
                    .append(template.getName());
            view.add(line);
        }

        view.showOn(player, page);
    }

}
