package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class TuiUtils {

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
                Notification.error(player, Translation.TUI_NotDominionOwnerOrAdminForPage, dominion.getName());
                return true;
            }
        }
        return false;
    }

    public static boolean notOp(CommandSender sender) {
        if (!sender.isOp()) {
            Notification.error(sender, Translation.Messages_PageNoPermission);
            return true;
        }
        return false;
    }

    public static void printHelp(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        int page = getPage(args, 1);
        ListView view = ListView.create(10, "/dominion help");
        view.title(Translation.TUI_CommandHelp_Title.trans())
                .subtitle(Line.create().append(Translation.TUI_CommandHelp_SubTitle.trans()))
                // todo ...
                .showOn(player, page);
    }

}
