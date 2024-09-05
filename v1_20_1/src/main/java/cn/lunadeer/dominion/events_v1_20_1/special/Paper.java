package cn.lunadeer.dominion.events_v1_20_1.special;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static cn.lunadeer.dominion.utils.EventUtils.checkFlag;

public class Paper implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST) // monster_move
    public void onMonsterPathfinding(EntityPathfindEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Monster)) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(event.getLoc());
        checkFlag(dom, Flag.MONSTER_MOVE, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // animal_move
    public void onAnimalPathfinding(EntityPathfindEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Animals)) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(event.getLoc());
        checkFlag(dom, Flag.ANIMAL_MOVE, event);
    }

}
