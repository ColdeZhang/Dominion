package cn.lunadeer.dominion.tuis.dominion;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.tuis.Apis.*;

public class DominionManage {
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominionNameArg_1(player, args);
        if (dominion == null) {
            Notification.error(sender, "你不在任何领地内，请指定领地名称 /dominion manage <领地名称>");
            return;
        }
        if (noAuthToManage(player, dominion)) return;
        int page = getPage(args, 2);
        Line size_info = Line.create()
                .append(Button.create("详细信息").setExecuteCommand("/dominion info " + dominion.getName()).build())
                .append("查看领地详细信息");
        Line env_info = Line.create()
                .append(Button.create("环境设置").setExecuteCommand("/dominion env_setting " + dominion.getName()).build())
                .append("设置领地内的一些非玩家相关效果");
        Line flag_info = Line.create()
                .append(Button.create("访客权限").setExecuteCommand("/dominion guest_setting " + dominion.getName()).build())
                .append("访客在此领地的权限");
        Line privilege_list = Line.create()
                .append(Button.create("成员管理").setExecuteCommand("/dominion member list " + dominion.getName()).build())
                .append("管理此领地成员的权限");
        Line group_list = Line.create()
                .append(Button.create("权限组").setExecuteCommand("/dominion group list " + dominion.getName()).build())
                .append("管理此领地的权限组");
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
        Line map_color = Line.create()
                .append(Button.create("设置颜色").setExecuteCommand("/dominion cui_set_map_color " + dominion.getName()).build())
                .append(Component.text("设置卫星地图上的地块颜色")
                        .append(Component.text(dominion.getColor(),
                                TextColor.color(dominion.getColorR(), dominion.getColorG(), dominion.getColorB()))));
        ListView view = ListView.create(10, "/dominion manage " + dominion.getName());
        view.title("领地 " + dominion.getName() + " 管理界面")
                .navigator(Line.create()
                        .append(Button.create("主菜单").setExecuteCommand("/dominion menu").build())
                        .append(Button.create("我的领地").setExecuteCommand("/dominion list").build())
                        .append(dominion.getName()))
                .add(size_info)
                .add(env_info)
                .add(flag_info)
                .add(privilege_list)
                .add(group_list)
                .add(set_tp)
                .add(rename)
                .add(join_msg)
                .add(leave_msg);
        if (Dominion.config.getBlueMap()) {
            view.add(map_color);
        }
        view.showOn(player, page);
    }
}
