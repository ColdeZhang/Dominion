package cn.lunadeer.dominion.utils.VaultConnect;

import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.i18n.Localization;
import net.milkbowl.vault2.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;

public class Vault2 implements VaultInterface {

    private Economy econ = null;
    private String PluginName = null;
    
    @Override
    public boolean init(JavaPlugin plugin) {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            econ = rsp.getProvider();
            PluginName = plugin.getName();
            return true;
        }
        XLogger.err(Localization.Utils_VaultUnlockedNotAvailable);
        return false;
    }

    @Override
    public String currencyNamePlural() {
        return econ.defaultCurrencyNamePlural(PluginName);
    }

    @Override
    public String currencyNameSingular() {
        return econ.defaultCurrencyNameSingular(PluginName);
    }

    @Override
    public void withdrawPlayer(Player player, double amount) {
        econ.withdraw(PluginName, player.getUniqueId(), BigDecimal.valueOf(amount));
    }

    @Override
    public void depositPlayer(Player player, double amount) {
        econ.deposit(PluginName, player.getUniqueId(), BigDecimal.valueOf(amount));
    }

    @Override
    public double getBalance(Player player) {
        return econ.getBalance(PluginName, player.getUniqueId()).doubleValue();
    }
}
