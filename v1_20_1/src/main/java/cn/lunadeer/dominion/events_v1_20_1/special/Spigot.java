package cn.lunadeer.dominion.events_v1_20_1.special;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.minecraftpluginutils.Scheduler;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static cn.lunadeer.dominion.utils.EventUtils.checkEnvironmentFlag;

public class Spigot implements Listener {

    static {
        ConcurrentHashMap<UUID, Location> entityMap = new ConcurrentHashMap<>();
        Scheduler.runTaskRepeat(() -> {
            Dominion.instance.getServer().getWorlds().forEach(world -> {
                world.getEntities().forEach(entity -> {
                    if (!(entity instanceof LivingEntity)) {
                        return;
                    }
                    if (!entityMap.containsKey(entity.getUniqueId())) {
                        entityMap.put(entity.getUniqueId(), entity.getLocation());
                    } else {
                        Location lastLoc = entityMap.get(entity.getUniqueId());
                        Location currentLoc = entity.getLocation();
                        DominionDTO dom = Cache.instance.getDominionByLoc(currentLoc);
                        if (!checkEnvironmentFlag(dom, Flags.ANIMAL_MOVE, null) && entity instanceof Animals) {
                            entity.teleport(lastLoc);
                        } else if (!checkEnvironmentFlag(dom, Flags.MONSTER_MOVE, null) && entity instanceof Monster) {
                            entity.teleport(lastLoc);
                        } else {
                            entityMap.put(entity.getUniqueId(), currentLoc);
                        }
                    }
                });
            });
        }, 0, 30);

        Scheduler.runTaskRepeat(() -> {
            entityMap.forEach((uuid, loc) -> {
                Entity e = Dominion.instance.getServer().getEntity(uuid);
                if (e == null || e.isDead()) {
                    entityMap.remove(uuid);
                }
            });
        }, 0, 6000);
    }

}
