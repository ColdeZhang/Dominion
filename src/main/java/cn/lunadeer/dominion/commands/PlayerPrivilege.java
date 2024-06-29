package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.PrivilegeController;
import cn.lunadeer.dominion.tuis.dominion.manage.member.MemberList;
import cn.lunadeer.dominion.tuis.dominion.manage.member.MemberSetting;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.controllers.PrivilegeController.*;

public class PlayerPrivilege {

    /**
     * 创建玩家特权
     * /dominion create_privilege <玩家名称> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void createPlayerPrivilege(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length != 2 && args.length != 3 && args.length != 4) {
            Notification.error(sender, "用法: /dominion create_privilege <玩家名称> [领地名称]");
            return;
        }
        if (args.length == 2) {
            createPrivilege(operator, args[1]);
        } else {
            createPrivilege(operator, args[1], args[2]);
        }
        if (args.length == 4) {
            String[] newArgs = new String[2];
            newArgs[0] = "privilege_list";
            newArgs[1] = args[2];
            MemberList.show(sender, newArgs);
        }
    }

    /**
     * 设置玩家权限
     * /dominion set_privilege <玩家名称> <权限名称> <true/false> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setPlayerPrivilege(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length == 4) {
            setPrivilege(operator, args[1], args[2], Boolean.parseBoolean(args[3]));
        } else if (args.length == 5) {
            setPrivilege(operator, args[1], args[2], Boolean.parseBoolean(args[3]), args[4]);
        } else if (args.length == 6) {
            setPrivilege(operator, args[1], args[2], Boolean.parseBoolean(args[3]), args[4]);
            String[] newArgs = new String[4];
            newArgs[0] = "privilege_info";
            newArgs[1] = args[1];
            newArgs[2] = args[4];
            newArgs[3] = args[5];
            MemberSetting.show(sender, newArgs);
        } else {
            Notification.error(sender, "用法: /dominion set_privilege <玩家名称> <权限名称> <true/false> [领地名称]");
        }
    }

    /**
     * 重置玩家权限
     * /dominion clear_privilege <玩家名称> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void clearPlayerPrivilege(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length != 2 && args.length != 3 && args.length != 4) {
            Notification.error(sender, "用法: /dominion clear_privilege <玩家名称> [领地名称]");
            return;
        }
        if (args.length == 2) {
            clearPrivilege(operator, args[1]);
        } else {
            clearPrivilege(operator, args[1], args[2]);
        }
        if (args.length == 4) {
            String[] newArgs = new String[3];
            newArgs[0] = "privilege_list";
            newArgs[1] = args[2];
            MemberList.show(sender, newArgs);
        }
    }

    /**
     * 应用权限模板
     * /dominion apply_template <玩家名称> <领地名称> <模板名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void applyTemplate(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length < 4) {
            Notification.error(sender, "用法: /dominion apply_template <玩家名称> <领地名称> <模板名称>");
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        String playerName = args[1];
        String dominionName = args[2];
        String templateName = args[3];
        PrivilegeController.applyTemplate(operator, dominionName, playerName, templateName);
        String[] newArgs = new String[3];
        newArgs[0] = "member_setting";
        newArgs[1] = playerName;
        newArgs[2] = dominionName;
        MemberSetting.show(sender, newArgs);
    }

}
