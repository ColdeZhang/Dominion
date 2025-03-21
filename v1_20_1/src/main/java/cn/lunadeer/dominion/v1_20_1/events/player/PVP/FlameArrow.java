package cn.lunadeer.dominion.v1_20_1.events.player.PVP;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;
import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlagSilence;

public class FlameArrow implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void handler(EntityCombustByEntityEvent event) {
        if (!(event.getCombuster() instanceof Arrow arrow)) {
            return;
        }
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        if (!(arrow.getShooter() instanceof Player attacker)) {
            return;
        }
        if (victim == attacker) {
            return;
        }

        DominionDTO dom = CacheManager.instance.getDominion(arrow.getLocation());
        if (!checkPrivilegeFlag(dom, Flags.PVP, attacker, null) || !checkPrivilegeFlagSilence(dom, Flags.PVP, victim, null)) {
            event.setCancelled(true);
        }
    }
}
