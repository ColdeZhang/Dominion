package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class DominionFlagInfo {

    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length < 2) {
            Dominion.notification.error(sender, "用法: /dominion flag_info <领地名称> [页码]");
            return;
        }
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Dominion.notification.error(sender, "领地 %s 不存在", args[1]);
            return;
        }
        int page = 1;
        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (Exception ignored) {
            }
        }
        ListView view = ListView.create(10, "/dominion flag_info " + dominion.getName());
        view.title("领地 " + dominion.getName() + " 默认权限")
                .navigator(Line.create()
                        .append(Button.create("主菜单").setExecuteCommand("/dominion menu").build())
                        .append(Button.create("我的领地").setExecuteCommand("/dominion list").build())
                        .append(Button.create("管理界面").setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append("权限列表"));
        for (Flag flag : Flag.getDominionFlags()) {
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
