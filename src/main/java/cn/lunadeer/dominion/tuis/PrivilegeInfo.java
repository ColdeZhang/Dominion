package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getDominionNameArg_2;
import static cn.lunadeer.dominion.tuis.Apis.noAuthToManage;

public class PrivilegeInfo {
    // /dominion privilege_info <玩家名称> [领地名称] [页码]
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominionNameArg_2(player, args);
        int page = 1;
        if (args.length == 4) {
            try {
                page = Integer.parseInt(args[3]);
            } catch (Exception ignored) {
            }
        }
        String playerName = args[1];
        if (dominion == null) {
            Notification.error(sender, "你不在任何领地内，请指定领地名称 /dominion privilege_info <玩家名称> [领地名称]");
            return;
        }
        ListView view = ListView.create(10, "/dominion privilege_info " + playerName + " " + dominion.getName());
        if (noAuthToManage(player, dominion)) return;
        PlayerDTO playerDTO = PlayerDTO.select(playerName);
        if (playerDTO == null) {
            Notification.error(sender, "玩家 %s 不存在", playerName);
            return;
        }
        PlayerPrivilegeDTO privilege = PlayerPrivilegeDTO.select(playerDTO.getUuid(), dominion.getId());
        if (privilege == null) {
            Notification.warn(sender, "玩家 %s 不是领地 %s 的成员", playerName, dominion.getName());
            return;
        }
        view.title("玩家 " + playerName + " 在领地 " + dominion.getName() + " 的权限设置");
        view.navigator(
                Line.create()
                        .append(Button.create("主菜单").setExecuteCommand("/dominion menu").build())
                        .append(Button.create("我的领地").setExecuteCommand("/dominion list").build())
                        .append(Button.create("管理界面").setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append(Button.create("成员列表").setExecuteCommand("/dominion privilege_list " + dominion.getName()).build())
                        .append("成员权限")
        );
        if (privilege.getAdmin()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑").setExecuteCommand("/dominion set_privilege " + playerName + " admin false " + dominion.getName() + " " + page).build())
                    .append("管理员"));
            view.add(createOption(Flag.GLOW, privilege.getFlagValue(Flag.GLOW), playerName, dominion.getName(), page));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐").setExecuteCommand("/dominion set_privilege " + playerName + " admin true " + dominion.getName() + " " + page).build())
                    .append("管理员"));
            for (Flag flag : Flag.getPrivilegeFlagsEnabled()) {
                view.add(createOption(flag, privilege.getFlagValue(flag), playerName, dominion.getName(), page));
            }
        }
        view.showOn(player, page);
    }

    private static Line createOption(Flag flag, boolean value, String player_name, String dominion_name, int page) {
        if (value) {
            return Line.create()
                    .append(Button.createGreen("☑").setExecuteCommand("/dominion set_privilege " + player_name + " " + flag.getFlagName() + " false " + dominion_name + " " + page).build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        } else {
            return Line.create()
                    .append(Button.createRed("☐").setExecuteCommand("/dominion set_privilege " + player_name + " " + flag.getFlagName() + " true " + dominion_name + " " + page).build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        }
    }
}
