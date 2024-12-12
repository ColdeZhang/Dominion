package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.FlagsController;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.EnvSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.GuestSetting;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.CommandSender;

import static cn.lunadeer.dominion.utils.CommandUtils.hasPermission;

public class DominionFlag {

    /**
     * 设置领地权限
     * /dominion set <权限名称> <true/false> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setDominionFlag(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);

        Flag flag = Flags.getFlag(args[1]);
        if (flag == null) {
            Notification.error(sender, Translation.Messages_UnknownFlag, args[1]);
            return;
        }
        if (args.length == 3) {
            FlagsController.setFlag(operator, flag, Boolean.parseBoolean(args[2]));
        } else if (args.length == 4) {
            FlagsController.setFlag(operator, flag, Boolean.parseBoolean(args[2]), args[3]);
        } else if (args.length == 5) {
            FlagsController.setFlag(operator, flag, Boolean.parseBoolean(args[2]), args[3]);
            String[] newArgs = new String[3];
            newArgs[0] = flag instanceof EnvFlag ? "env_info" : "flag_info";
            newArgs[1] = args[3];
            newArgs[2] = args[4];
            if (flag instanceof EnvFlag) {
                EnvSetting.show(sender, newArgs);
            } else {
                GuestSetting.show(sender, newArgs);
            }

        } else {
            Notification.error(sender, Translation.Commands_Dominion_SetFlagUsage);
        }
    }

}
