package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class SelectTemplate {

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;

        String playerName = args[1];
        String dominionName = args[2];

        int page = 1;
        if (args.length == 5) {
            try {
                page = Integer.parseInt(args[4]);
            } catch (Exception ignored) {
            }
        }

        List<PrivilegeTemplateDTO> templates = PrivilegeTemplateDTO.selectAll(player.getUniqueId());

        ListView view = ListView.create(10, "/dominion select_template " + playerName + " " + dominionName);
        view.title("选择一个模板");
        Line sub = Line.create()
                .append("套用在领地 " + dominionName + " 的成员 " + playerName + " 身上")
                .append(Button.create("返回").setExecuteCommand("/dominion privilege_info " + playerName + " " + dominionName).build());
        view.subtitle(sub);

        for (PrivilegeTemplateDTO template : templates) {
            // /dominion apply_template <玩家名称> <领地名称> <模板名称>
            view.add(Line.create()
                    .append(Button.create("选择")
                            .setExecuteCommand("/dominion apply_template " + playerName + " " + dominionName + " " + template.getName())
                            .build())
                    .append(Component.text(template.getName())));
        }
        view.showOn(player, page);
    }

}
