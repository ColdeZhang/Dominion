package cn.lunadeer.dominion.tuis.template;

import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

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
        view.title("成员权限模板列表");
        view.navigator(Line.create().append(Button.create("主菜单").setExecuteCommand("/dominion menu").build()).append("模板列表"));

        Button create = Button.create("创建成员权限模板").setExecuteCommand("/dominion cui_template_create")
                .setHoverText("创建一个新的成员权限模板");

        view.add(Line.create().append(create.build()));

        for (PrivilegeTemplateDTO template : templates) {
            Button manage = Button.createGreen("配置").setExecuteCommand("/dominion template setting " + template.getName());
            Button delete = Button.createRed("删除").setExecuteCommand("/dominion template delete " + template.getName());
            Line line = Line.create()
                    .append(delete.build())
                    .append(manage.build())
                    .append(template.getName());
            view.add(line);
        }

        view.showOn(player, page);
    }

}
