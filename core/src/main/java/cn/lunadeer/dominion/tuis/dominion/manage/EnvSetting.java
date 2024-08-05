package cn.lunadeer.dominion.tuis.dominion.manage;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class EnvSetting {

    public static void show(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Notification.error(sender, "用法: /dominion env_setting <领地名称> [页码]");
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Notification.error(sender, "领地 %s 不存在", args[1]);
            return;
        }
        int page = getPage(args, 2);
        ListView view = ListView.create(10, "/dominion env_setting " + dominion.getName());
        view.title("领地 " + dominion.getName() + " 环境设置")
                .navigator(Line.create()
                        .append(Button.create("主菜单").setExecuteCommand("/dominion menu").build())
                        .append(Button.create("我的领地").setExecuteCommand("/dominion list").build())
                        .append(Button.create("管理界面").setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append("环境设置"));
        for (Flag flag : Flag.getDominionOnlyFlagsEnabled()) {
            view.add(createOption(flag, dominion.getFlagValue(flag), dominion.getName(), page));
        }
        view.showOn(player, page);
    }

    private static Line createOption(Flag flag, boolean value, String dominion_name, int page) {
        if (value) {
            return Line.create()
                    .append(Button.createGreen("☑").setExecuteCommand("/dominion set " + flag.getFlagName() + " false " + dominion_name + " " + page).build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        } else {
            return Line.create()
                    .append(Button.createRed("☐").setExecuteCommand("/dominion set " + flag.getFlagName() + " true " + dominion_name + " " + page).build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        }
    }

}
