package cn.lunadeer.dominion.utils.VaultConnect;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public interface VaultInterface {

    public boolean init(JavaPlugin plugin);

    public String currencyNamePlural();


    public String currencyNameSingular();

    public void withdrawPlayer(OfflinePlayer player, double amount);

    public void depositPlayer(OfflinePlayer player, double amount);

    public double getBalance(OfflinePlayer player);

}
