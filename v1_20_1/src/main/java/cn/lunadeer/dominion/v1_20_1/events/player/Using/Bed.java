package cn.lunadeer.dominion.v1_20_1.events.player.Using;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;

public class Bed implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void handler(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player bukkitPlayer = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!(Tag.BEDS.isTagged(block.getType()))) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.BED, bukkitPlayer, event);
    }
}
