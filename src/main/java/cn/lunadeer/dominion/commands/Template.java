package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.TemplateController;
import cn.lunadeer.dominion.tuis.template.TemplateList;
import cn.lunadeer.dominion.tuis.template.TemplateSetting;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static cn.lunadeer.dominion.Commands.boolOptions;
import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.commands.Helper.allTemplates;
import static cn.lunadeer.dominion.commands.Helper.playerPrivileges;
import static cn.lunadeer.dominion.tuis.Apis.getPage;

public class Template {

    /**
     * 创建权限模板
     * /dominion template create <模板名称> [页码]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void createTemplate(CommandSender sender, String[] args) {
        try {
            if (args.length < 3) {
                Notification.error(sender, "用法: /dominion template create <模板名称>");
                return;
            }
            Player player = playerOnly(sender);
            if (player == null) return;
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
            TemplateController.createTemplate(operator, args[2]);
            TemplateList.show(sender);
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * 删除权限模板
     * /dominion template delete <模板名称> [页码]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void deleteTemplate(CommandSender sender, String[] args) {
        try {
            if (args.length < 3) {
                Notification.error(sender, "用法: /dominion template delete <模板名称>");
                return;
            }
            Player player = playerOnly(sender);
            if (player == null) return;
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
            TemplateController.deleteTemplate(operator, args[2]);
            TemplateList.show(sender, getPage(args, 3));
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * 编辑模板
     * /dominion template set_flag <模板名称> <权限名称> <true/false> [页码]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setTemplateFlag(CommandSender sender, String[] args) {
        try {
            if (args.length < 5) {
                Notification.error(sender, "用法: /dominion template set_flag <模板名称> <权限名称> <true/false>");
                return;
            }
            Player player = playerOnly(sender);
            if (player == null) return;
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
            String templateName = args[2];
            String flagName = args[3];
            boolean value = Boolean.parseBoolean(args[4]);
            TemplateController.setTemplateFlag(operator, templateName, flagName, value);
            TemplateSetting.show(sender, templateName, getPage(args, 5));
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }


    public static void handle(@NotNull CommandSender sender, @NotNull String[] args) {
        switch (args[1]) {
            case "list":
                TemplateList.show(sender, args);
                break;
            case "setting":
                TemplateSetting.show(sender, args);
                break;
            case "delete":
                Template.deleteTemplate(sender, args);
                break;
            case "create":
                Template.createTemplate(sender, args);
                break;
            case "set_flag":
                Template.setTemplateFlag(sender, args);
                break;
        }
    }

    public static @Nullable List<String> handleTab(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 2) {
            return Arrays.asList("list", "setting", "delete", "create", "set_flag");
        }
        if (args.length == 3) {
            switch (args[1]) {
                case "create":
                    return Collections.singletonList("输入模板名称");
                case "delete":
                case "set_flag":
                case "setting":
                    return allTemplates(sender);
                case "list":
                    return Collections.singletonList("页码(可选)");
            }
        }
        if (args.length == 4) {
            switch (args[1]) {
                case "set_flag":
                    return playerPrivileges();
                case "setting":
                    return Collections.singletonList("页码(可选)");
            }
        }
        if (args.length == 5) {
            switch (args[1]) {
                case "set_flag":
                    return boolOptions();
            }
        }
        return null;
    }

}
