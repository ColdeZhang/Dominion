package cn.lunadeer.dominion.utils.VaultConnect;

import cn.lunadeer.dominion.utils.XLogger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Vault implements VaultInterface {

    private Economy econ = null;

    @Override
    public boolean init(JavaPlugin plugin) {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            econ = rsp.getProvider();
            return true;
        }
        XLogger.error("Vault not available.");
        return false;
    }

    @Override
    public String currencyNamePlural() {
        return econ.currencyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return econ.currencyNameSingular();
    }

    @Override
    public void withdrawPlayer(Player player, double amount) {
        econ.withdrawPlayer(player, amount);
    }

    @Override
    public void depositPlayer(Player player, double amount) {
        econ.depositPlayer(player, amount);
    }

    @Override
    public double getBalance(Player player) {
        return econ.getBalance(player);
    }
}
