package cn.lunadeer.dominion.utils.VaultConnect;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface VaultInterface {

    public boolean init(JavaPlugin plugin);

    public String currencyNamePlural();


    public String currencyNameSingular();

    public void withdrawPlayer(Player player, double amount);

    public void depositPlayer(Player player, double amount);

    public double getBalance(Player player);

}
