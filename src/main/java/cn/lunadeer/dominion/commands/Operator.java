package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.command.CommandSender;

import static cn.lunadeer.dominion.commands.Apis.notOpOrConsole;

public class Operator {

    public static void reloadCache(CommandSender sender, String[] args){
        if (notOpOrConsole(sender)) return;
        Notification.info(sender, "正在从数据库重新加载领地缓存...");
        Cache.instance.loadDominions();
        Notification.info(sender, "正在从数据库重新加载玩家权限缓存...");
        Cache.instance.loadPlayerPrivileges();
        Notification.info(sender, "缓存刷新完成");
    }

}
