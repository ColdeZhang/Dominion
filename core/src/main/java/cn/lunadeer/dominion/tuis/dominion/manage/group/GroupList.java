package cn.lunadeer.dominion.tuis.dominion.manage.group;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.tuis.Apis;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.noAuthToManage;

public class GroupList {

    public static void show(CommandSender sender, String dominionName) {
        show(sender, new String[]{"", "", dominionName});
    }

    public static void show(CommandSender sender, String dominionName, int page) {
        show(sender, new String[]{"", "", dominionName, String.valueOf(page)});
    }

    public static void show(CommandSender sender, String[] args) {
        if (args.length < 3) {
            Notification.error(sender, "用法: /dominion group list <领地名称> [页码]");
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
        int page = Apis.getPage(args, 3);
        List<GroupDTO> groups = GroupDTO.selectByDominionId(dominion.getId());
        ListView view = ListView.create(10, "/dominion group list " + dominion.getName());
        view.title("权限组列表");
        view.navigator(
                Line.create()
                        .append(Button.create("主菜单").setExecuteCommand("/dominion menu").build())
                        .append(Button.create("我的领地").setExecuteCommand("/dominion list").build())
                        .append(Button.create("管理界面").setExecuteCommand("/dominion manage " + dominion.getName()).build())
                        .append("权限组列表")
        );

        Button create_btn = Button.createGreen("创建权限组")
                .setHoverText("创建一个新的权限组")
                .setExecuteCommand("/dominion cui_create_group " + dominion.getName());
        view.add(new Line().append(create_btn.build()));

        for (GroupDTO group : groups) {
            Line line = new Line();
            Button del = Button.createRed("删除")
                    .setHoverText("删除权限组 " + group.getName())
                    .setExecuteCommand("/dominion group delete " + dominion.getName() + " " + group.getName());
            Button edit = Button.create("编辑")
                    .setHoverText("编辑权限组 " + group.getName())
                    .setExecuteCommand("/dominion group setting " + dominion.getName() + " " + group.getName());
            Button add = Button.createGreen("+")
                    .setHoverText("添加成员到权限组 " + group.getName())
                    .setExecuteCommand("/dominion group select_member " + dominion.getName() + " " + group.getName() + " " + page);
            line.append(del.build()).append(edit.build()).append(group.getName()).append(add.build());
            view.add(line);
            List<MemberDTO> players = MemberDTO.selectByGroupId(group.getId());
            XLogger.debug("players: " + players.size());
            for (MemberDTO playerPrivilege : players) {
                PlayerDTO p = PlayerDTO.select(playerPrivilege.getPlayerUUID());
                if (p == null) continue;
                Button remove = Button.createRed("-")
                        .setHoverText("把 " + p.getLastKnownName() + " 移出权限组 " + group.getName())
                        .setExecuteCommand("/dominion group remove_member " + dominion.getName() + " " + group.getName() + " " + p.getLastKnownName() + " " + page);
                Line playerLine = new Line().setDivider("");
                playerLine.append(Component.text("        "));
                playerLine.append(remove.build()).append(" |  " + p.getLastKnownName());
                view.add(playerLine);
            }
            view.add(new Line().append(""));
        }
        view.showOn(player, page);
    }

}
