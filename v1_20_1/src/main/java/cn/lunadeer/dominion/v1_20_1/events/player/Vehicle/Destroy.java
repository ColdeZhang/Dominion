package cn.lunadeer.dominion.v1_20_1.events.player.Vehicle;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;

public class Destroy implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void handler(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player player)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getVehicle().getLocation());
        checkPrivilegeFlag(dom, Flags.VEHICLE_DESTROY, player, event);
    }
}
