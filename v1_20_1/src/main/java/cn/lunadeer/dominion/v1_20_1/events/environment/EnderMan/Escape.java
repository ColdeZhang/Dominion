package cn.lunadeer.dominion.v1_20_1.events.environment.EnderMan;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;

import static cn.lunadeer.dominion.misc.Others.checkEnvironmentFlag;

public class Escape implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void handler(EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ENDERMAN) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(entity.getLocation());
        checkEnvironmentFlag(dom, Flags.ENDER_MAN, event);
        if (event.getTo() != null) {
            DominionDTO domTo = CacheManager.instance.getDominion(event.getTo());
            checkEnvironmentFlag(domTo, Flags.ENDER_MAN, event);
        }
    }
}
