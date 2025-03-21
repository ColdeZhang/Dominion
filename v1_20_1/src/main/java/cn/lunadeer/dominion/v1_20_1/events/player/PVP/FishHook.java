package cn.lunadeer.dominion.v1_20_1.events.player.PVP;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;
import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlagSilence;

public class FishHook implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void handler(PlayerFishEvent event) {
        if (!(event.getCaught() instanceof Player victim)) {
            return;
        }
        Player attacker = event.getPlayer();
        if (victim == attacker) {
            return;
        }

        DominionDTO dom = CacheManager.instance.getDominion(victim.getLocation());
        if (!checkPrivilegeFlag(dom, Flags.PVP, attacker, null) || !checkPrivilegeFlagSilence(dom, Flags.PVP, victim, null)) {
            event.getHook().remove();
            event.setCancelled(true);
        }
    }
}
