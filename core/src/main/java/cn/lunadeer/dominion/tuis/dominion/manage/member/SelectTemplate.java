package cn.lunadeer.dominion.tuis.dominion.manage.member;

import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.utils.CommandUtils.CommandParser;
import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getPage;

public class SelectTemplate {

    public static void show(CommandSender sender, String[] args) {
        if (args.length < 4) {
            // /dominion member select_template <领地名称> <玩家名称>  [页码]
            Notification.error(sender, Translation.TUI_SelectTemplate_Usage);
            return;
        }

        Player player = playerOnly(sender);
        if (player == null) return;

        String dominionName = args[2];
        String playerName = args[3];


        int page = getPage(args, 4);

        List<PrivilegeTemplateDTO> templates = PrivilegeTemplateDTO.selectAll(player.getUniqueId());

        ListView view = ListView.create(10, "/dominion member select_template " + dominionName + " " + playerName);
        view.title(Translation.TUI_SelectTemplate_Title);
        Line sub = Line.create()
                .append(String.format(Translation.TUI_SelectTemplate_Description.trans(), dominionName, playerName))
                .append(Button.create(Translation.TUI_BackButton).setExecuteCommand("/dominion member setting " + dominionName + " " + playerName).build());
        view.subtitle(sub);

        for (PrivilegeTemplateDTO template : templates) {
            // /dominion member apply_template <领地名称> <成员名称> <模板名称>
            view.add(Line.create()
                    .append(Button.create(Translation.TUI_SelectButton)
                            .setExecuteCommand(CommandParser("/dominion member apply_template %s %s %s", dominionName, playerName, template.getName()))
                            .build())
                    .append(Component.text(template.getName())));
        }
        view.showOn(player, page);
    }

}
