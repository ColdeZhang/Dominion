package cn.lunadeer.dominion.v1_20_1.events.player.Break;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;
import static cn.lunadeer.dominion.misc.Others.isCrop;

public class NormalBlock implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void handler(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (isCrop(event.getBlock().getType())) return;
        DominionDTO dom = CacheManager.instance.getDominion(event.getBlock().getLocation());
        checkPrivilegeFlag(dom, Flags.BREAK_BLOCK, player, event);
    }
}
