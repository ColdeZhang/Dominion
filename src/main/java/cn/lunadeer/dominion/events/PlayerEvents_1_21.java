package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryType;

import static cn.lunadeer.dominion.events.Apis.checkFlag;
import static cn.lunadeer.dominion.events.Apis.getInvDominion;

public class PlayerEvents_1_21 extends PlayerEvents_1_20_1 {

    @EventHandler(priority = EventPriority.HIGHEST) // crafter
    public void onCrafterOpen(InventoryOpenEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getType() != InventoryType.CRAFTER) {
            return;
        }
        if (!(event.getPlayer() instanceof Player bukkitPlayer)) {
            return;
        }
        DominionDTO dom = getInvDominion(bukkitPlayer, inv);
        checkFlag(dom, Flag.CRAFTER, bukkitPlayer, event);
    }
}
