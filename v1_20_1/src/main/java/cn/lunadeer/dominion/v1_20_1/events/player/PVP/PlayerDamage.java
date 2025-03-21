package cn.lunadeer.dominion.v1_20_1.events.player.PVP;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;
import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlagSilence;

public class PlayerDamage implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void handler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        Player attacker = null;
        Entity attacker_entity = event.getDamager();
        if (attacker_entity instanceof Player p) {
            attacker = p;
        } else if (attacker_entity instanceof Projectile proj && proj.getShooter() instanceof Player p) {
            attacker = p;
        } else if (attacker_entity instanceof TNTPrimed tnt && tnt.getSource() instanceof Player p) {
            attacker = p;
        } else if (attacker_entity instanceof Firework) {
            DominionDTO dom = CacheManager.instance.getDominion(attacker_entity.getLocation());
            if (!checkPrivilegeFlagSilence(dom, Flags.PVP, victim, null)) {
                event.setCancelled(true);
            }
            return;
        }
        if (attacker == null || victim == attacker) {
            return;
        }

        DominionDTO dom = CacheManager.instance.getDominion(victim.getLocation());
        if (!checkPrivilegeFlag(dom, Flags.PVP, attacker, null) || !checkPrivilegeFlagSilence(dom, Flags.PVP, victim, null)) {
            event.setCancelled(true);
        }
    }
}
