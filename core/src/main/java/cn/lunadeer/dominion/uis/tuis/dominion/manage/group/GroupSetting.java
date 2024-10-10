package cn.lunadeer.dominion.uis.tuis.dominion.manage.group;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.utils.TuiUtils;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.noAuthToManage;

public class GroupSetting {
    public static void show(CommandSender sender, String dominionName, String groupName) {
        show(sender, new String[]{"", "", dominionName, groupName});
    }

    public static void show(CommandSender sender, String dominionName, String groupName, Integer page) {
        show(sender, new String[]{"", "", dominionName, groupName, page.toString()});
    }

    public static void show(CommandSender sender, String[] args) {
        if (args.length < 4) {
            Notification.error(sender, Translation.TUI_GroupSetting_Usage);
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
        int page = TuiUtils.getPage(args, 4);
        GroupDTO group = GroupDTO.select(dominion.getId(), args[3]);
        if (group == null) {
            Notification.error(sender, Translation.Messages_GroupNotExist, args[2], args[3]);
            return;
        }

        ListView view = ListView.create(10, "/dominion group setting " + dominion.getName() + " " + group.getNamePlain());
        view.title(Component.text(Translation.TUI_GroupSetting_TitleL.trans())
                .append(group.getNameColoredComponent())
                .append(Component.text(Translation.TUI_GroupSetting_TitleR.trans())));
        view.navigator(
                Line.create()
                        .append(Button.create(Translation.TUI_Navigation_Menu).setExecuteCommand("/dominion menu").build())
                        .append(Button.create(Translation.TUI_Navigation_DominionList).setExecuteCommand("/dominion list").build())
                        .append(Button.create(Translation.TUI_Navigation_Manage).setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append(Button.create(Translation.TUI_Navigation_GroupList).setExecuteCommand("/dominion group list " + dominion.getName()).build())
                        .append(Translation.TUI_Navigation_GroupSetting)
        );
        Button rename_btn = Button.create(Translation.TUI_GroupSetting_RenameButton)
                .setHoverText(String.format(Translation.TUI_GroupSetting_RenameDescription.trans(), group.getNamePlain()))
                .setExecuteCommand("/dominion cui_rename_group " + dominion.getName() + " " + group.getNamePlain());
        view.add(Line.create().append(rename_btn.build()));

        if (group.getAdmin()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑")
                            .setExecuteCommand(parseCommand(dominion.getName(), group.getNamePlain(), "admin", false, page))
                            .build())
                    .append(
                            Component.text(Translation.Flags_admin_DisplayName.trans())
                                    .hoverEvent(Component.text(Translation.Flags_admin_Description.trans()))
                    ));
            view.add(createOption(Flag.GLOW, group.getFlagValue(Flag.GLOW), dominion.getName(), group.getNamePlain(), page));
        } else {
            view.add(Line.create()
                    .append(Button.createRed("☐")
                            .setExecuteCommand(parseCommand(dominion.getName(), group.getNamePlain(), "admin", true, page))
                            .build())
                    .append(
                            Component.text(Translation.Flags_admin_DisplayName.trans())
                                    .hoverEvent(Component.text(Translation.Flags_admin_Description.trans()))
                    ));
            for (Flag flag : Flag.getPrivilegeFlagsEnabled()) {
                view.add(createOption(flag, group.getFlagValue(flag), dominion.getName(), group.getNamePlain(), page));
            }
        }
        view.showOn(player, page);
    }

    private static Line createOption(Flag flag, boolean value, String DominionName, String groupName, int page) {
        if (value) {
            return Line.create()
                    .append(Button.createGreen("☑")
                            .setExecuteCommand(parseCommand(DominionName, groupName, flag.getFlagName(), false, page))
                            .build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        } else {
            return Line.create()
                    .append(Button.createRed("☐")
                            .setExecuteCommand(parseCommand(DominionName, groupName, flag.getFlagName(), true, page))
                            .build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        }
    }

    private static String parseCommand(String dominionName, String groupName, String flagName, boolean value, int page) {
        return String.format("/dominion group set_flag %s %s %s %s %d", dominionName, groupName, flagName, value, page);
    }
}
