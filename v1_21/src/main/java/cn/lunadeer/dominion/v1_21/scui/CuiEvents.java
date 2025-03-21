package cn.lunadeer.dominion.v1_21.scui;


import cn.lunadeer.dominion.events.LowestVersion;
import cn.lunadeer.dominion.events.PaperOnly;
import cn.lunadeer.dominion.utils.XVersionManager;
import cn.lunadeer.dominion.utils.scui.CuiManager;
import cn.lunadeer.dominion.utils.scui.CuiView;
import cn.lunadeer.dominion.utils.scui.ItemStackButton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static cn.lunadeer.dominion.utils.Misc.isPaper;
import static cn.lunadeer.dominion.utils.scui.CuiManager.getTokenFromHiddenComponent;

@LowestVersion(XVersionManager.ImplementationVersion.v1_21)
@PaperOnly
public class CuiEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent event) {
        if (!isPaper()) {
            return;
        }
        InventoryView view = event.getView();
        UUID token = getTokenFromHiddenComponent(view.title(), CuiView.viewPrefix);
        if (token == null) {
            return;
        }
        CuiView cuiView = CuiManager.instance.getCUI(token);
        if (cuiView == null) {
            return;
        }
        cuiView.close(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCloseTextInput(InventoryCloseEvent event) {
        if (!isPaper()) {
            return;
        }
        Inventory inv = event.getInventory();
        if (inv.getType() != InventoryType.ANVIL) {
            return;
        }
        AnvilInventory anvilInv = (AnvilInventory) inv;
        ItemStack btn = anvilInv.getSecondItem();
        if (btn == null) {
            return;
        }
        UUID token = getTokenFromHiddenComponent(btn.displayName(), ItemStackButton.btnPrefix);
        if (token == null) {
            return;
        }
        ItemStackButton btnObj = CuiManager.instance.getBtn(token);
        if (btnObj == null) {
            return;
        }
        CuiView view = CuiManager.instance.get(btnObj.getView().getToken());
        if (view == null) {
            return;
        }
        inv.clear();
        view.close(event);
    }
}
