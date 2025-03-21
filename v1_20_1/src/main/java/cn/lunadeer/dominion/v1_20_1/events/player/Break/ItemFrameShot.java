package cn.lunadeer.dominion.v1_20_1.events.player.Break;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;

public class ItemFrameShot implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void handler(ProjectileHitEvent event) {
        Entity hit = event.getHitEntity();
        if (hit == null) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }
        if (!(hit instanceof Hanging)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(hit.getLocation());
        checkPrivilegeFlag(dom, Flags.BREAK_BLOCK, player, event);
    }
}
