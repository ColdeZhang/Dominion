package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.noAuthToManage;

public class GroupManage {
    public static void show(CommandSender sender, String[] args) {
        if (args.length < 3) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Notification.error(sender, "领地 %s 不存在", args[1]);
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        int page = Apis.getPage(args, 3);
        GroupDTO group = GroupDTO.select(dominion.getId(), args[2]);
        if (group == null) {
            Notification.error(sender, "权限组 %s 不存在", args[2]);
            return;
        }

        ListView view = ListView.create(10, "/dominion group_manage " + dominion.getName() + " " + group.getName());
        view.title("权限组 " + group.getName() + " 管理");
        view.navigator(
                Line.create()
                        .append(Button.create("主菜单").setExecuteCommand("/dominion menu").build())
                        .append(Button.create("我的领地").setExecuteCommand("/dominion list").build())
                        .append(Button.create("管理界面").setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append(Button.create("权限组列表").setExecuteCommand("/dominion group_list" + dominion.getName()).build())
        );
        Button rename_btn = Button.create("重命名")
                .setHoverText("重命名权限组 " + group.getName())
                .setExecuteCommand("/dominion cui_rename_group " + dominion.getName() + " " + group.getName());
        view.add(Line.create().append(rename_btn.build()));

        if (group.getAdmin()) {
            view.add(Line.create()
                    .append(Button.createGreen("☑")
                            .setExecuteCommand(String.format("/dominion set_group_flag %s %s admin false %s", dominion.getName(), group.getName(), page))
                            .build())
                    .append("管理员"));
            view.add(createOption(Flag.GLOW, group.getFlagValue(Flag.GLOW), dominion.getName(), group.getName(), page));
        } else {
            view.add(Line.create()
                    .append(Button.createGreen("☐")
                            .setExecuteCommand(String.format("/dominion set_group_flag %s %s admin true %s", dominion.getName(), group.getName(), page))
                            .build())
                    .append("管理员"));
            for (Flag flag : Flag.getPrivilegeFlagsEnabled()) {
                view.add(createOption(flag, group.getFlagValue(flag), dominion.getName(), group.getName(), page));
            }
        }
        view.showOn(player, page);
    }

    private static Line createOption(Flag flag, boolean value, String DominionName, String groupName, int page) {
        if (value) {
            return Line.create()
                    .append(Button.createGreen("☑")
                            .setExecuteCommand(String.format("/dominion set_group_flag %s %s %s false %s", DominionName, groupName, flag.getFlagName(), page))
                            .build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        } else {
            return Line.create()
                    .append(Button.createRed("☐")
                            .setExecuteCommand(String.format("/dominion set_group_flag %s %s %s true %s", DominionName, groupName, flag.getFlagName(), page))
                            .build())
                    .append(Component.text(flag.getDisplayName()).hoverEvent(Component.text(flag.getDescription())));
        }
    }
}
