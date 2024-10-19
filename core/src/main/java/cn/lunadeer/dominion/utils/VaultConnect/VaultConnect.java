package cn.lunadeer.dominion.utils.VaultConnect;

import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.i18n.Localization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultConnect implements Listener {

    public static VaultConnect instance;
    private VaultInterface vaultInstance = null;
    private JavaPlugin plugin;

    public VaultConnect(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnable(ServiceRegisterEvent event) {
    }

    public boolean economyAvailable() {
        if (vaultInstance == null) {
            Plugin vaultPlugin = this.plugin.getServer().getPluginManager().getPlugin("Vault");
            if (vaultPlugin == null) {
                XLogger.err(Localization.Utils_VaultNotAvailable);
                return false;
            }
            if (vaultPlugin.getDescription().getAuthors().contains("creatorfromhell")) {
                vaultInstance = new Vault2();
            } else {
                vaultInstance = new Vault();
            }
            if (!vaultInstance.init(plugin)) {
                vaultInstance = null;
                XLogger.err(Localization.Utils_NoEconomyPlugin);
                return false;
            }
        }
        XLogger.debug("Vault connected.");
        return true;
    }

    public String currencyNamePlural() {
        if (economyAvailable()) {
            return vaultInstance.currencyNamePlural();
        }
        XLogger.warn(Localization.Utils_NoEconomyPlugin);
        return "";
    }

    public String currencyNameSingular() {
        if (economyAvailable()) {
            return vaultInstance.currencyNameSingular();
        }
        XLogger.warn(Localization.Utils_NoEconomyPlugin);
        return "";
    }

    public void withdrawPlayer(Player player, double amount) {
        if (economyAvailable()) {
            vaultInstance.withdrawPlayer(player, amount);
            return;
        }
        XLogger.warn(Localization.Utils_NoEconomyPlugin);
    }

    public void depositPlayer(Player player, double amount) {
        if (economyAvailable()) {
            vaultInstance.depositPlayer(player, amount);
            return;
        }
        XLogger.warn(Localization.Utils_NoEconomyPlugin);
    }

    public double getBalance(Player player) {
        if (economyAvailable()) {
            return vaultInstance.getBalance(player);
        }
        XLogger.warn(Localization.Utils_NoEconomyPlugin);
        return 0;
    }

    private static boolean foundClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
