package cn.lunadeer.dominion.utils.scui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.UUID;

public interface CuiView {

    public static final String viewPrefix = "cui:view:";

    public void open(Player audience);

    public UUID getToken();

    public void close(InventoryCloseEvent event);

    public void register();

    public void unregister();

    public void refresh();

    public void setButton(Integer slot, ItemStackButton button);
}
