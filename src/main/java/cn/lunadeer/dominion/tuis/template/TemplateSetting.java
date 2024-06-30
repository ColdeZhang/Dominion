package cn.lunadeer.dominion.tuis.template;

import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class TemplateSetting {

    // /dominion template setting <模板名称> [页码]
    public static void show(CommandSender sender, String templateName, int page) {
        show(sender, new String[]{"", "", templateName, String.valueOf(page)});
    }

    public static void show(CommandSender sender, String templateName) {
        show(sender, new String[]{"", "", templateName});
    }

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args, 3);
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(player.getUniqueId(), args[2]);
        if (template == null) {
            Notification.error(sender, "模板 %s 不存在", args[2]);
            return;
        }

        ListView view = ListView.create(10, "/dominion template manage " + template.getName());
        view.title("模板 " + args[1] + " 权限管理");
        view.navigator(Line.create()
                .append(Button.create("主菜单").setExecuteCommand("/dominion menu").build())
                .append(Button.create("模板列表").setExecuteCommand("/dominion template list").build())
                .append("模板管理")
        );

        // /dominion template_set_flag <模板名称> <权限名称> <true/false> [页码]

        if (template.getAdmin()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑").setExecuteCommand("/dominion template set_flag " + template.getName() + " admin false " + page).build())
                    .append("管理员"));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐").setExecuteCommand("/dominion template set_flag " + template.getName() + " admin true " + page).build())
                    .append("管理员"));
        }
        for (Flag flag : Flag.getPrivilegeFlagsEnabled()) {
            view.add(createOption(flag, template.getFlagValue(flag), template.getName(), page));
        }
        view.showOn(player, page);
    }

    private static Line createOption(Flag flag, boolean value, String templateName, int page) {
        if (value) {
            return Line.create()
                    .append(Button.createGreen("☑").setExecuteCommand("/dominion template set_flag " + templateName + " " + flag.getFlagName() + " false " + page).build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        } else {
            return Line.create()
                    .append(Button.createRed("☐").setExecuteCommand("/dominion template set_flag " + templateName + " " + flag.getFlagName() + " true " + page).build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        }
    }
}
