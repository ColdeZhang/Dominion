package cn.lunadeer.dominion.v1_21.scui;

import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;

public class OpenAnvilInventory {
    public static AnvilInventory open(Player audience, String title) {
        InventoryView inv_view = audience.openAnvil(null, true);
        if (inv_view == null) {
            XLogger.debug("inv_view is null");
            return null;
        }
        if (inv_view.getTopInventory().getType() != InventoryType.ANVIL) {
            XLogger.debug("inv_view.getTopInventory().getType() != InventoryType.ANVIL");
            return null;
        }
        inv_view.setTitle(title);
        return (AnvilInventory) inv_view.getTopInventory();
    }
}
