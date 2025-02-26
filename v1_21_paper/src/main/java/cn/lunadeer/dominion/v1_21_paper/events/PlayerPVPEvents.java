package cn.lunadeer.dominion.v1_21_paper.events;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
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

    @EventHandler(ignoreCancelled = true) // 玩家攻击
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) {
            return;
        }

        Player attacker = null;
        Entity damager = event.getDamager();
        if (damager instanceof Player p) {
            attacker = p;
        } else if (damager instanceof Projectile proj && proj.getShooter() instanceof Player p) {
            attacker = p;
        } else if (damager instanceof TNTPrimed tnt && tnt.getSource() instanceof Player p) {
            attacker = p;
        } else if (damager instanceof Firework) {
            DominionDTO dom = Cache.instance.getDominionByLoc(damager.getLocation());
            if (!checkPrivilegeFlag(dom, Flags.PVP, damaged)) {
                event.setCancelled(true);
            }
            return;
        }
        if (attacker == null || damaged == attacker) {
            return;
        }

        DominionDTO dom = Cache.instance.getDominionByLoc(damaged.getLocation());
        if (!checkPrivilegeFlag(dom, Flags.PVP, attacker, null) || !checkPrivilegeFlag(dom, Flags.PVP, damaged)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true) // 火焰箭
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

        DominionDTO dom = Cache.instance.getDominionByLoc(arrow.getLocation());
        if (!checkPrivilegeFlag(dom, Flags.PVP, attacker, null) || !checkPrivilegeFlag(dom, Flags.PVP, damaged)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true) // 药水
    public void onPotionSplash(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player attacker)) {
            return;
        }

        DominionDTO dom = Cache.instance.getDominionByLoc(event.getPotion().getLocation());
        if (checkPrivilegeFlag(dom, Flags.PVP, attacker, null)) {
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (!(entity instanceof Player damaged) || damaged == attacker) {
                    continue;
                }
                if (!checkPrivilegeFlag(dom, Flags.PVP, damaged)) {
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

    @EventHandler(ignoreCancelled = true) // 药水云
    public void onCloudEffects(AreaEffectCloudApplyEvent event) {
        if (!(event.getEntity().getSource() instanceof Player attacker)) {
            return;
        }

        DominionDTO dom = Cache.instance.getDominionByLoc(event.getEntity().getLocation());
        if ((checkPrivilegeFlag(dom, Flags.PVP, attacker, null))) {
            event.getAffectedEntities().removeIf(entity -> {
                if (!(entity instanceof Player damaged) || damaged == attacker) {
                    return false;
                }
                return !checkPrivilegeFlag(dom, Flags.PVP, damaged);
            });
        } else {
            event.getAffectedEntities().removeIf(entity -> entity instanceof Player damaged && damaged != attacker);
        }
    }

    @EventHandler(ignoreCancelled = true) // 钓鱼
    public void onPlayerFish(PlayerFishEvent event) {
        if (!(event.getCaught() instanceof Player damaged)) {
            return;
        }
        Player attacker = event.getPlayer();
        if (damaged == attacker) {
            return;
        }

        DominionDTO dom = Cache.instance.getDominionByLoc(damaged.getLocation());
        if (!checkPrivilegeFlag(dom, Flags.PVP, attacker, null) || !checkPrivilegeFlag(dom, Flags.PVP, damaged)) {
            event.getHook().remove();
            event.setCancelled(true);
        }
    }
}
