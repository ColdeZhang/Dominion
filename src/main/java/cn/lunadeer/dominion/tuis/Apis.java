package cn.lunadeer.dominion.tuis;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class Apis {

    public static int getPage(String[] args, int pos) {
        int page = 1;
        if (args.length > pos) {
            try {
                page = Integer.parseInt(args[pos]);
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
            return Cache.instance.getPlayerCurrentDominion(player);
        }
    }

    public static boolean noAuthToManage(Player player, DominionDTO dominion) {
        if (player.isOp()) return false;
        if (!dominion.getOwner().equals(player.getUniqueId())) {
            MemberDTO privileges = MemberDTO.select(player.getUniqueId(), dominion.getId());
            if (privileges == null || !privileges.getAdmin()) {
                Notification.error(player, "你不是领地 %s 的拥有者或管理员，无权访问此页面", dominion.getName());
                return true;
            }
        }
        return false;
    }

    public static boolean notOp(CommandSender sender) {
        if (!sender.isOp()) {
            Notification.error(sender, "你没有权限访问此页面");
            return true;
        }
        return false;
    }

    public static void printHelp(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args, 1);
        ListView view = ListView.create(10, "/dominion help");
        view.title("领地插件命令帮助 <>表示必填参数 []表示可选参数")
                .add(Line.create().append("打开交互菜单").append("/dominion menu"))
                .add(Line.create().append("查看帮助").append("/dominion help [页码]"))
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
                .showOn(player, page);
    }

}
