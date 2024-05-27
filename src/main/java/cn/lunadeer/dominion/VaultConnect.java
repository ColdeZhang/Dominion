package cn.lunadeer.dominion;

import cn.lunadeer.minecraftpluginutils.XLogger;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultConnect {

    private Economy econ = null;
    private Permission perms = null;
    private Chat chat = null;
    private JavaPlugin plugin;

    public VaultConnect(JavaPlugin plugin) {
        this.plugin = plugin;
        if (!setupEconomy()) {
            XLogger.err("你没有安装 Vault 前置插件，无法使用经济功能，如果不需要使用经济功能请前往配置文件关闭。");
            return;
        }
        setupPermissions();
        setupChat();
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    public Permission getPermissions() {
        return perms;
    }

    public Chat getChat() {
        return chat;
    }
}
