package cn.lunadeer.dominion.tuis.dominion.manage.member;

import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.CommandParser;
import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class SelectTemplate {

    public static void show(CommandSender sender, String[] args) {
        if (args.length < 4) {
            // /dominion member select_template <领地名称> <玩家名称>  [页码]
            Notification.error(sender, "用法: /dominion member select_template <领地名称> <玩家名称>  [页码]");
            return;
        }

        Player player = playerOnly(sender);
        if (player == null) return;

        String dominionName = args[2];
        String playerName = args[3];


        int page = getPage(args, 4);

        List<PrivilegeTemplateDTO> templates = PrivilegeTemplateDTO.selectAll(player.getUniqueId());

        ListView view = ListView.create(10, "/dominion member select_template " + dominionName + " " + playerName);
        view.title("选择一个模板");
        Line sub = Line.create()
                .append("套用在领地 " + dominionName + " 的成员 " + playerName + " 身上")
                .append(Button.create("返回").setExecuteCommand("/dominion member setting " + dominionName + " " + playerName).build());
        view.subtitle(sub);

        for (PrivilegeTemplateDTO template : templates) {
            // /dominion member apply_template <领地名称> <成员名称> <模板名称>
            view.add(Line.create()
                    .append(Button.create("选择")
                            .setExecuteCommand(CommandParser("/dominion member apply_template %s %s %s", dominionName, playerName, template.getName()))
                            .build())
                    .append(Component.text(template.getName())));
        }
        view.showOn(player, page);
    }

}
