package cn.lunadeer.dominion.uis.tuis.dominion.manage.member;

import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PreFlag;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
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
import static cn.lunadeer.dominion.utils.TuiUtils.noAuthToManage;

public class MemberSetting {
    public static void show(CommandSender sender, String dominionName, String playerName, Integer page) {
        show(sender, new String[]{"", "", dominionName, playerName, page.toString()});
    }

    public static void show(CommandSender sender, String dominionName, String playerName) {
        show(sender, new String[]{"", "", dominionName, playerName});
    }

    public static void show(CommandSender sender, String[] args) {
        if (args.length < 3) {
            Notification.error(sender, Translation.TUI_MemberSetting_Usage);
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[2]);
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[2]);
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        int page = getPage(args, 4);
        String playerName = args[3];
        ListView view = ListView.create(10, "/dominion member setting " + dominion.getName() + " " + playerName);
        PlayerDTO playerDTO = PlayerDTO.select(playerName);
        if (playerDTO == null) {
            Notification.error(sender, Translation.Messages_PlayerNotExist, playerName);
            return;
        }
        MemberDTO privilege = MemberDTO.select(playerDTO.getUuid(), dominion.getId());
        if (privilege == null) {
            Notification.warn(sender, Translation.Messages_PlayerNotMember, playerName, dominion.getName());
            return;
        }
        view.title(String.format(Translation.TUI_MemberSetting_Title.trans(), playerName, dominion.getName()));
        view.navigator(
                Line.create()
                        .append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build())
                        .append(Button.create(Translation.TUI_Navigation_DominionList).setExecuteCommand("/dominion list").build())
                        .append(Button.create(Translation.TUI_Navigation_Manage).setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append(Button.create(Translation.TUI_Navigation_MemberList).setExecuteCommand("/dominion member list " + dominion.getName()).build())
                        .append(Translation.TUI_Navigation_MemberSetting)
        );
        view.add(Line.create().append(Button.createGreen(Translation.TUI_MemberSetting_ApplyTemplateButton)
                .setHoverText(Translation.TUI_MemberSetting_ApplyTemplateDescription)
                .setExecuteCommand("/dominion member select_template " + dominion.getName() + " " + playerName).build()));
        if (privilege.getAdmin()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑").setExecuteCommand(
                            parseCommand(dominion.getName(), playerName, "admin", false, page)
                    ).build())
                    .append(
                            Component.text(Translation.Flags_admin_DisplayName.trans())
                                    .hoverEvent(Component.text(Translation.Flags_admin_Description.trans()))
                    ));
            view.add(createOption(Flags.GLOW, privilege.getFlagValue(Flags.GLOW), playerName, dominion.getName(), page));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐").setExecuteCommand(
                            parseCommand(dominion.getName(), playerName, "admin", true, page)
                    ).build())
                    .append(
                            Component.text(Translation.Flags_admin_DisplayName.trans())
                                    .hoverEvent(Component.text(Translation.Flags_admin_Description.trans()))
                    ));
            for (PreFlag flag : Flags.getAllPreFlagsEnable()) {
                view.add(createOption(flag, privilege.getFlagValue(flag), playerName, dominion.getName(), page));
            }
        }
        view.showOn(player, page);
    }

    private static Line createOption(PreFlag flag, boolean value, String player_name, String dominion_name, int page) {
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
