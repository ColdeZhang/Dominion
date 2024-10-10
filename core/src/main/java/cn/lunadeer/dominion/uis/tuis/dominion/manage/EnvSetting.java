package cn.lunadeer.dominion.uis.tuis.dominion.manage;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getPage;

public class EnvSetting {

    public static void show(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Notification.error(sender, Translation.TUI_EnvSetting_Usage);
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[1]);
            return;
        }
        int page = getPage(args, 2);
        ListView view = ListView.create(10, "/dominion env_setting " + dominion.getName());
        view.title(String.format(Translation.TUI_EnvSetting_Title.trans(), dominion.getName()))
                .navigator(Line.create()
                        .append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build())
                        .append(Button.create(Translation.TUI_Navigation_DominionList).setExecuteCommand("/dominion list").build())
                        .append(Button.create(Translation.TUI_Navigation_Manage).setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append(Translation.TUI_Navigation_EnvSetting));
        for (Flag flag : Flag.getEnvironmentFlagsEnabled()) {
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
