package cn.lunadeer.dominion.v1_20_1.events.player.Shoot;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;

public class ArrowsLaunch implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void handler(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) {
            return;
        }
        if (Tag.ENTITY_TYPES_ARROWS.isTagged(projectile.getType())) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(projectile.getLocation());
        checkPrivilegeFlag(dom, Flags.SHOOT, player, event);
    }
}
