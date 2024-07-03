package cn.lunadeer.dominion.tuis.dominion.manage.member;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getPage;
import static cn.lunadeer.dominion.tuis.Apis.noAuthToManage;

public class MemberSetting {
    public static void show(CommandSender sender, String dominionName, String playerName, Integer page) {
        show(sender, new String[]{"", "", dominionName, playerName, page.toString()});
    }

    public static void show(CommandSender sender, String dominionName, String playerName) {
        show(sender, new String[]{"", "", dominionName, playerName});
    }

    public static void show(CommandSender sender, String[] args) {
        if (args.length < 3) {
            Notification.error(sender, "用法: /dominion member setting <领地名称> <玩家名称> [页码]");
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[2]);
        if (dominion == null) {
            Notification.error(sender, "领地 %s 不存在", args[2]);
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        int page = getPage(args, 4);
        String playerName = args[3];
        ListView view = ListView.create(10, "/dominion member setting " + dominion.getName() + " " + playerName);
        PlayerDTO playerDTO = PlayerDTO.select(playerName);
        if (playerDTO == null) {
            Notification.error(sender, "玩家 %s 不存在", playerName);
            return;
        }
        MemberDTO privilege = MemberDTO.select(playerDTO.getUuid(), dominion.getId());
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
                        .append(Button.create("成员列表").setExecuteCommand("/dominion member list " + dominion.getName()).build())
                        .append("成员权限")
        );
        view.add(Line.create().append(Button.createGreen("套用模板")
                .setHoverText("选择一个权限模板套用")
                .setExecuteCommand("/dominion member select_template " + dominion.getName() + " " + playerName).build()));
        if (privilege.getAdmin()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑").setExecuteCommand(
                            parseCommand(dominion.getName(), playerName, "admin", false, page)
                    ).build())
                    .append("管理员"));
            view.add(createOption(Flag.GLOW, privilege.getFlagValue(Flag.GLOW), playerName, dominion.getName(), page));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐").setExecuteCommand(
                            parseCommand(dominion.getName(), playerName, "admin", true, page)
                    ).build())
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
                    .append(Button.createGreen("☑").setExecuteCommand(
                            parseCommand(dominion_name, player_name, flag.getFlagName(), false, page)
                    ).build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        } else {
            return Line.create()
                    .append(Button.createRed("☐").setExecuteCommand(
                            parseCommand(dominion_name, player_name, flag.getFlagName(), true, page)
                    ).build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        }
    }

    private static String parseCommand(String dominionName, String playerName, String flagName, boolean value, int page) {
        return String.format("/dominion member set_flag %s %s %s %s %d", dominionName, playerName, flagName, value, page);
    }
}
