package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.FlagsController;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.tuis.dominion.manage.EnvSetting;
import cn.lunadeer.dominion.tuis.dominion.manage.GuestSetting;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class DominionFlag {

    /**
     * 设置领地权限
     * /dominion set <权限名称> <true/false> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setDominionFlag(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length == 3) {
            FlagsController.setFlag(operator, args[1], Boolean.parseBoolean(args[2]));
        } else if (args.length == 4) {
            FlagsController.setFlag(operator, args[1], Boolean.parseBoolean(args[2]), args[3]);
        } else if (args.length == 5) {
            FlagsController.setFlag(operator, args[1], Boolean.parseBoolean(args[2]), args[3]);
            String[] newArgs = new String[3];
            newArgs[0] = Flag.isDominionOnlyFlag(args[1]) ? "env_info" : "flag_info";
            newArgs[1] = args[3];
            newArgs[2] = args[4];
            if (Flag.isDominionOnlyFlag(args[1])) {
                EnvSetting.show(sender, newArgs);
            } else {
                GuestSetting.show(sender, newArgs);
            }

        } else {
            Notification.error(sender, "用法: /dominion set <权限名称> <true/false> [领地名称]");
        }
    }

}
