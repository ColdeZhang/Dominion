package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.getDominionNameArg_1;
import static cn.lunadeer.dominion.tuis.Apis.noAuthToManage;

public class DominionManage {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominionNameArg_1(player, args);
        if (dominion == null) {
            Dominion.notification.error(sender, "你不在任何领地内，请指定领地名称 /dominion manage <领地名称>");
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        Line size_info = Line.create()
                .append(Button.create("详细信息").setExecuteCommand("/dominion info " + dominion.getName()).build())
                .append("查看领地详细信息");
        Line flag_info = Line.create()
                .append(Button.create("权限设置").setExecuteCommand("/dominion flag_info " + dominion.getName()).build())
                .append("管理领地默认权限");
        Line privilege_list = Line.create()
                .append(Button.create("玩家权限").setExecuteCommand("/dominion privilege_list " + dominion.getName()).build())
                .append("管理玩家特权");
        Line set_tp = Line.create()
                .append(Button.create("设置传送点").setExecuteCommand("/dominion set_tp_location " + dominion.getName()).build())
                .append("设置当前位置为此领地传送点");
        Line rename = Line.create()
                .append(Button.create("重命名").setExecuteCommand("/dominion cui_rename " + dominion.getName()).build())
                .append("重命名领地");
        Line join_msg = Line.create()
                .append(Button.create("编辑欢迎提示语").setExecuteCommand("/dominion cui_edit_join_message " + dominion.getName()).build())
                .append("当玩家进入领地时显示的消息");
        Line leave_msg = Line.create()
                .append(Button.create("编辑离开提示语").setExecuteCommand("/dominion cui_edit_leave_message " + dominion.getName()).build())
                .append("当玩家离开领地时显示的消息");
        ListView view = ListView.create(10, "/dominion manage " + dominion.getName());
        view.title("领地 " + dominion.getName() + " 管理界面")
                .navigator(Line.create()
                        .append(Button.create("主菜单").setExecuteCommand("/dominion menu").build())
                        .append(Button.create("我的领地").setExecuteCommand("/dominion list").build())
                        .append(dominion.getName()))
                .add(size_info)
                .add(flag_info)
                .add(privilege_list)
                .add(set_tp)
                .add(rename)
                .add(join_msg)
                .add(leave_msg)
                .showOn(player, 1);
    }
}
