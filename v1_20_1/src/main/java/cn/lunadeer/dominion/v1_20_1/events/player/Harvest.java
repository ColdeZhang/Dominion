package cn.lunadeer.dominion.v1_20_1.events.player;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;
import static cn.lunadeer.dominion.misc.Others.isCrop;

public class Harvest implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void handler(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!isCrop(block.getType())) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.HARVEST, player, event);
    }
}
