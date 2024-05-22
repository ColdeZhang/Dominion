package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import com.destroystokyo.paper.event.entity.EndermanEscapeEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

import static org.bukkit.Material.FARMLAND;

public class EnvironmentEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST) // creeper_explode
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.CREEPER
                && entity.getType() != EntityType.WITHER_SKULL
                && entity.getType() != EntityType.FIREBALL
                && entity.getType() != EntityType.ENDER_CRYSTAL
        ) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(event.getLocation());
        if (dom == null) {
            return;
        }
        if (dom.getFlagValue(Flag.CREEPER_EXPLODE)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // fire_spread
    public void onFireSpread(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            // 如果点燃事件没有玩家触发，那么就是火焰蔓延
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(event.getBlock().getLocation());
        if (dom == null) {
            return;
        }
        if (dom.getFlagValue(Flag.FIRE_SPREAD)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // flow_in_protection
    public void onLiquidFlowIn(BlockFromToEvent event) {
        Location from = event.getBlock().getLocation();
        Location to = event.getToBlock().getLocation();
        DominionDTO dom_to = Cache.instance.getDominion(to);
        if (dom_to == null) {
            return;
        }
        DominionDTO dom_from = Cache.instance.getDominion(from);
        if (dom_from != null) {
            if (Objects.equals(dom_from.getId(), dom_to.getId())) {
                return;
            }
        }
        if (dom_to.getFlagValue(Flag.FLOW_IN_PROTECTION)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST) // mob_drop_item
    public void onMobDropItem(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(entity.getLocation());
        if (dom == null) {
            return;
        }
        if (dom.getFlagValue(Flag.MOB_DROP_ITEM)) {
            return;
        }
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST) // tnt_explode
    public void onTntExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.MINECART_TNT && entity.getType() != EntityType.PRIMED_TNT) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(event.getLocation());
        if (dom == null) {
            return;
        }
        if (dom.getFlagValue(Flag.TNT_EXPLODE)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // trample
    public void onFarmlandTrample(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (block.getType() != FARMLAND) {
            return;
        }
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        Location location = block.getLocation();
        DominionDTO dom = Cache.instance.getDominion(location);
        if (dom == null) {
            return;
        }
        if (dom.getFlagValue(Flag.TRAMPLE)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // wither_spawn
    public void onWitherSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.WITHER) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(entity.getLocation());
        if (dom == null) {
            return;
        }
        if (dom.getFlagValue(Flag.WITHER_SPAWN)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // ender_man spawn
    public void onEnderManSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ENDERMAN) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(entity.getLocation());
        if (dom == null) {
            return;
        }
        if (dom.getFlagValue(Flag.ENDER_MAN)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // ender_man escape
    public void onEnderManEscape(EndermanEscapeEvent event){
        Entity entity = event.getEntity();
        DominionDTO dom = Cache.instance.getDominion(entity.getLocation());
        if (dom == null) {
            return;
        }
        if (dom.getFlagValue(Flag.ENDER_MAN)) {
            return;
        }
        event.setCancelled(true);
    }
}
