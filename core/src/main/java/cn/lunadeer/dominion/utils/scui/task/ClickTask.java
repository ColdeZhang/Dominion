package cn.lunadeer.dominion.utils.scui.task;

import cn.lunadeer.dominion.utils.scui.CuiView;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ClickTask {

    public void run(CuiView view, InventoryClickEvent event);

}

