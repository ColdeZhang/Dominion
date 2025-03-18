package cn.lunadeer.dominion.v1_21_spigot.events;

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

public class PlayerPVPEvents implements Listener {

    @EventHandler(ignoreCancelled = true) // player vs player
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) {
            return;
        }

        Player attacker = null;
        Entity damager = event.getDamager();
        switch (damager) {
            case Player p -> attacker = p;
            case Projectile proj when proj.getShooter() instanceof Player p -> attacker = p;
            case TNTPrimed tnt when tnt.getSource() instanceof Player p -> attacker = p;
            case Firework ignored -> {
                DominionDTO dom = CacheManager.instance.getDominion(damager.getLocation());
                if (!checkPrivilegeFlag(dom, Flags.PVP, damaged, null)) {
                    event.setCancelled(true);
                }
                return;
            }
            default -> {
            }
        }
        if (attacker == null || damaged == attacker) {
            return;
        }

        DominionDTO dom = CacheManager.instance.getDominion(damaged.getLocation());
        if (!checkPrivilegeFlag(dom, Flags.PVP, attacker, null) || !checkPrivilegeFlag(dom, Flags.PVP, damaged, null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true) // flame arrow
    public void onFlameArrow(EntityCombustByEntityEvent event) {
        if (!(event.getCombuster() instanceof Arrow arrow)) {
            return;
        }
        if (!(event.getEntity() instanceof Player damaged)) {
            return;
        }
        if (!(arrow.getShooter() instanceof Player attacker)) {
            return;
        }
        if (damaged == attacker) {
            return;
        }

        DominionDTO dom = CacheManager.instance.getDominion(arrow.getLocation());
        if (!checkPrivilegeFlag(dom, Flags.PVP, attacker, null) || !checkPrivilegeFlag(dom, Flags.PVP, damaged, null)) {
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
                if (!(entity instanceof Player damaged) || damaged == attacker) {
                    continue;
                }
                if (!checkPrivilegeFlag(dom, Flags.PVP, damaged, null)) {
                    event.setIntensity(damaged, 0);
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
                if (!(entity instanceof Player damaged) || damaged == attacker) {
                    return false;
                }
                return !checkPrivilegeFlag(dom, Flags.PVP, damaged, null);
            });
        } else {
            event.getAffectedEntities().removeIf(entity -> entity instanceof Player damaged && damaged != attacker);
        }
    }

    @EventHandler(ignoreCancelled = true) // fishing
    public void onPlayerFish(PlayerFishEvent event) {
        if (!(event.getCaught() instanceof Player damaged)) {
            return;
        }
        Player attacker = event.getPlayer();
        if (damaged == attacker) {
            return;
        }

        DominionDTO dom = CacheManager.instance.getDominion(damaged.getLocation());
        if (!checkPrivilegeFlag(dom, Flags.PVP, attacker, null) || !checkPrivilegeFlag(dom, Flags.PVP, damaged, null)) {
            event.getHook().remove();
            event.setCancelled(true);
        }
    }
}
