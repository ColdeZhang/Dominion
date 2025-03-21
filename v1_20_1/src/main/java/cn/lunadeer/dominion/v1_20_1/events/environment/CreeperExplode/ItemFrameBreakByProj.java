package cn.lunadeer.dominion.v1_20_1.events.environment.CreeperExplode;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import static cn.lunadeer.dominion.misc.Others.checkEnvironmentFlag;

public class ItemFrameBreakByProj implements Listener {
    @EventHandler(priority = EventPriority.LOWEST) // item_frame_proj_damage
    public void handle(HangingBreakByEntityEvent event) {
        if (event.getCause() != HangingBreakEvent.RemoveCause.ENTITY) {
            return;
        }
        Entity remover = event.getRemover();
        if (!(remover instanceof Projectile projectile)) {
            return;
        }
        if (!(projectile.getShooter() instanceof Player)) {
            // if the shooter is not a player, handle in player events
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getEntity().getLocation());
        checkEnvironmentFlag(dom, Flags.ITEM_FRAME_PROJ_DAMAGE, event);
    }
}
