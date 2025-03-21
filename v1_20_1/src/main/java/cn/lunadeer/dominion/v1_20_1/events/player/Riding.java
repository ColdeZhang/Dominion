package cn.lunadeer.dominion.v1_20_1.events.player;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;

public class Riding implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void handler(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getMount().getLocation());
        checkPrivilegeFlag(dom, Flags.RIDING, player, event);
    }
}
