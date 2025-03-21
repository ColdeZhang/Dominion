package cn.lunadeer.dominion.v1_20_1.events.player.Place;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;

public class NormalBlock implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void handler(BlockPlaceEvent event) {
        DominionDTO dominion = CacheManager.instance.getDominion(event.getBlock().getLocation());
        Player player = event.getPlayer();
        checkPrivilegeFlag(dominion, Flags.PLACE, player, event);
    }
}
