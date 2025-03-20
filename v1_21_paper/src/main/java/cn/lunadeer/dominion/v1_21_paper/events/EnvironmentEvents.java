package cn.lunadeer.dominion.v1_21_paper.events;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import static cn.lunadeer.dominion.misc.Others.checkEnvironmentFlag;

public class EnvironmentEvents extends cn.lunadeer.dominion.v1_21_spigot.events.EnvironmentEvents {
    @EventHandler(priority = EventPriority.LOWEST) // monster_move
    public void onMonsterPathfinding(EntityPathfindEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Monster)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getLoc());
        checkEnvironmentFlag(dom, Flags.MONSTER_MOVE, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // animal_move
    public void onAnimalPathfinding(EntityPathfindEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Animals)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getLoc());
        checkEnvironmentFlag(dom, Flags.ANIMAL_MOVE, event);
    }

    static {
        entityMoveTask.cancel();
        entityMoveCleanTask.cancel();
    }
}
