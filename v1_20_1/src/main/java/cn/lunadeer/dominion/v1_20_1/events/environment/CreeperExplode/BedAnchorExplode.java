package cn.lunadeer.dominion.v1_20_1.events.environment.CreeperExplode;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

import static cn.lunadeer.dominion.misc.Others.checkEnvironmentFlag;

public class BedAnchorExplode implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(BlockExplodeEvent event) {
        event.blockList().removeIf(blockState -> {
            DominionDTO dom = CacheManager.instance.getDominion(blockState.getLocation());
            return !checkEnvironmentFlag(dom, Flags.CREEPER_EXPLODE, null);
        });
    }
}
