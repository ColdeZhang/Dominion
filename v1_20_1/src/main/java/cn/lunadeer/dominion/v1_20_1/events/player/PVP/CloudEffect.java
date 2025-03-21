package cn.lunadeer.dominion.v1_20_1.events.player.PVP;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;
import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlagSilence;

public class CloudEffect implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void handler(AreaEffectCloudApplyEvent event) {
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
}
