package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.controllers.Apis.getPlayerCurrentDominion;

public class Apis {

    public static int getPage(String[] args) {
        int page = 1;
        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (Exception e) {
                return 1;
            }
        }
        return page;
    }

    /**
     * 尝试从 arg[1] 获取领地名称
     * 如果没有此参数则会尝试获取玩家当前所在位置
     *
     * @param player 玩家
     * @param args   参数
     * @return 领地信息
     */
    public static DominionDTO getDominionNameArg_1(Player player, String[] args) {
        if (args.length >= 2) {
            return DominionDTO.select(args[1]);
        } else {
            return getPlayerCurrentDominion(player);
        }
    }

    /**
     * 尝试从 arg[2] 获取领地名称
     * 如果没有此参数则会尝试获取玩家当前所在位置
     *
     * @param player 玩家
     * @param args   参数
     * @return 领地信息
     */
    public static DominionDTO getDominionNameArg_2(Player player, String[] args) {
        if (args.length >= 3) {
            return DominionDTO.select(args[2]);
        } else {
            return getPlayerCurrentDominion(player);
        }
    }

    public static boolean noAuthToManage(Player player, DominionDTO dominion) {
        if (player.isOp()) return false;
        if (!dominion.getOwner().equals(player.getUniqueId())) {
            PlayerPrivilegeDTO privileges = PlayerPrivilegeDTO.select(player.getUniqueId(), dominion.getId());
            if (privileges == null || !privileges.getAdmin()) {
                Notification.error(player, "你不是领地 %s 的拥有者或管理员，无权访问此页面", dominion.getName());
                return true;
            }
        }
        return false;
    }

    public static void printHelp(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args);
        ListView view = ListView.create(10, "/dominion help");
        view.title("领地插件命令帮助 <>表示必填参数 []表示可选参数")
                .add(Line.create().append("打开交互菜单").append(Button.create("/dominion menu").setExecuteCommand("/dominion menu").build()))
                .add(Line.create().append("查看帮助").append(Button.create("/dominion help [页码]").setExecuteCommand("/dominion help 1").build()))
                .add(Line.create().append("创建领地").append("/dominion create <领地名称>"))
                .add(Line.create().append("自动创建领地").append("/dominion auto_create <领地名称>"))
                .add(Line.create().append("创建子领地").append("/dominion create_sub <子领地名称> [父领地名称]"))
                .add(Line.create().append("自动创建子领地").append("/dominion auto_create_sub <子领地名称> [父领地名称]"))
                .add(Line.create().append("管理领地").append("/dominion manage <领地名称>"))
                .add(Line.create().append("扩张领地").append("/dominion expand [大小] [领地名称]"))
                .add(Line.create().append("缩小领地").append("/dominion contract [大小] [领地名称]"))
                .add(Line.create().append("设置进入领地的提示语").append("/dominion set_enter_msg <提示语> [领地名称]"))
                .add(Line.create().append("设置离开领地的提示语").append("/dominion set_leave_msg <提示语> [领地名称]"))
                .add(Line.create().append("设置领地传送点").append("/dominion set_tp_location [领地名称]"))
                .add(Line.create().append("传送到领地").append("/dominion tp <领地名称>"))
                .add(Line.create().append("重命名领地").append("/dominion rename <原领地名称> <新领地名称>"))
                .add(Line.create().append("转让领地").append("/dominion give <领地名称> <玩家名称> [force]"))
                .add(Line.create().append("删除领地").append("/dominion delete <领地名称> [force]"))
                // 管理员指令
                .add(Line.create().append("---[管理员指令]---"))
                .add(Line.create().append("刷新缓存").append(Button.create("/dominion reload_cache").setExecuteCommand("/dominion reload_cache").build()))
                .add(Line.create().append("重载配置").append(Button.create("/dominion reload_config").setExecuteCommand("/dominion reload_config").build()))
                // 以下指令主要被用于 ui 触发
                .add(Line.create().append("---[以下主要用于UI触发]---"))
                .add(Line.create().append("列出所有领地").append(Button.create("/dominion list [页码]").setExecuteCommand("/dominion list").build()))
                .add(Line.create().append("查看领地信息").append(Button.create("/dominion info [领地名称]").setExecuteCommand("/dominion info").build()))
                .add(Line.create().append("查看领地权限信息").append(Button.create("/dominion flag_info <领地名称> [页码]").setExecuteCommand("/dominion flag_info").build()))
                .add(Line.create().append("设置领地权限").append("/dominion set <权限名称> <true/false> [领地名称]"))
                .add(Line.create().append("创建玩家特权").append("/dominion create_privilege <玩家名称> [领地名称]"))
                .add(Line.create().append("设置玩家特权").append("/dominion set_privilege <玩家名称> <权限名称> <true/false> [领地名称]"))
                .add(Line.create().append("重置玩家特权").append("/dominion clear_privilege <玩家名称> [领地名称]"))
                .add(Line.create().append("查看领地玩家特权列表").append("/dominion privilege_list [领地名称] [页码]"))
                .add(Line.create().append("查看玩家特权信息").append("/dominion privilege_info <玩家名称> [领地名称] [页码]"))
                .add(Line.create().append("系统配置").append(Button.create("/dominion config [页码]").setExecuteCommand("/dominion config").build()))
                .showOn(player, page);
    }

}
