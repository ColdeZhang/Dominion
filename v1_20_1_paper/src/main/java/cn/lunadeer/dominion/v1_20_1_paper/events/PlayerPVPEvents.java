package cn.lunadeer.dominion.v1_20_1_paper.events;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerFishEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;
import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlagSilence;

public class PlayerPVPEvents implements Listener {

    @EventHandler(ignoreCancelled = true) // player vs player
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
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

    @EventHandler(ignoreCancelled = true) // flame arrow
    public void onFlameArrow(EntityCombustByEntityEvent event) {
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

    @EventHandler(ignoreCancelled = true) // potion
    public void onPotionSplash(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player attacker)) {
            return;
        }

        DominionDTO dom = CacheManager.instance.getDominion(event.getPotion().getLocation());
        if (checkPrivilegeFlag(dom, Flags.PVP, attacker, null)) {
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (!(entity instanceof Player victim) || victim == attacker) {
                    continue;
                }
                if (!checkPrivilegeFlagSilence(dom, Flags.PVP, victim, null)) {
                    event.setIntensity(victim, 0);
                }
            }
        } else {
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (!(entity instanceof Player damaged) || damaged == attacker) {
                    continue;
                }
                event.setIntensity(damaged, 0);
            }
        }
    }

    @EventHandler(ignoreCancelled = true) // area effect cloud
    public void onCloudEffects(AreaEffectCloudApplyEvent event) {
        if (!(event.getEntity().getSource() instanceof Player attacker)) {
            return;
        }

        DominionDTO dom = CacheManager.instance.getDominion(event.getEntity().getLocation());
        if ((checkPrivilegeFlag(dom, Flags.PVP, attacker, null))) {
            event.getAffectedEntities().removeIf(entity -> {
                if (!(entity instanceof Player victim) || victim == attacker) {
                    return false;
                }
                return !checkPrivilegeFlagSilence(dom, Flags.PVP, victim, null);
            });
        } else {
            event.getAffectedEntities().removeIf(entity -> entity instanceof Player damaged && damaged != attacker);
        }
    }

    @EventHandler(ignoreCancelled = true) // fishing
    public void onPlayerFish(PlayerFishEvent event) {
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
