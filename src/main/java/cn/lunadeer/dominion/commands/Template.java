package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.TemplateController;
import cn.lunadeer.dominion.tuis.template.TemplateList;
import cn.lunadeer.dominion.tuis.template.TemplateManage;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class Template {

    /**
     * 删除权限模板
     * /dominion template_delete <模板名称> [页码]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void deleteTemplate(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length < 2) {
            Notification.error(sender, "用法: /dominion template_delete <模板名称>");
            return;
        }
        TemplateController.deleteTemplate(operator, args[1]);
        String[] newArgs = new String[2];
        newArgs[0] = "template_list";
        if (args.length == 3) {
            newArgs[1] = args[2];
        } else {
            newArgs[1] = "1";
        }
        TemplateList.show(sender, newArgs);
    }

    /**
     * 创建权限模板
     * /dominion template_create <模板名称> [页码]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void createTemplate(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length < 2) {
            Notification.error(sender, "用法: /dominion template_create <模板名称>");
            return;
        }
        TemplateController.createTemplate(operator, args[1]);
        String[] newArgs = new String[2];
        newArgs[0] = "template_list";
        newArgs[1] = "1";
        TemplateList.show(sender, newArgs);
    }

    /**
     * 编辑模板
     * /dominion template_set_flag <模板名称> <权限名称> <true/false> [页码]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setTemplateFlag(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length < 4) {
            Notification.error(sender, "用法: /dominion template_set_flag <模板名称> <权限名称> <true/false>");
            return;
        }
        boolean value;
        if (args[3].equalsIgnoreCase("true")) {
            value = true;
        } else if (args[3].equalsIgnoreCase("false")) {
            value = false;
        } else {
            Notification.error(sender, "权限值必须是true或false");
            return;
        }
        TemplateController.setTemplateFlag(operator, args[1], args[2], value);
        String[] newArgs = new String[3];
        newArgs[0] = "template_manage";
        newArgs[1] = args[1];
        if (args.length == 5) {
            newArgs[2] = args[4];
        } else {
            newArgs[2] = "1";
        }
        TemplateManage.show(sender, newArgs);
    }

}
