package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Objects;

public class EnvironmentEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST) // creeper_explode
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.CREEPER && entity.getType() != EntityType.WITHER_SKULL){
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(event.getLocation());
        if (dom == null) {
            return;
        }
        if (dom.getCreeperExplode()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // fire_spread
    public void onFireSpread(BlockIgniteEvent event){
        Player player = event.getPlayer();
        if (player != null) {
            // 如果点燃事件没有玩家触发，那么就是火焰蔓延
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(event.getBlock().getLocation());
        if (dom == null) {
            return;
        }
        if (dom.getFireSpread()) {
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
        if (dom_from != null){
            if (Objects.equals(dom_from.getId(), dom_to.getId())){
                return;
            }
        }
        if (dom_to.getFlowInProtection()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST) // tnt_explode
    public void onTntExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.MINECART_TNT && entity.getType() != EntityType.PRIMED_TNT){
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(event.getLocation());
        if (dom == null) {
            return;
        }
        if (dom.getTntExplode()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // wither_spawn
    public void onWitherSpawn(CreatureSpawnEvent event){
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.WITHER) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(entity.getLocation());
        if (dom == null) {
            return;
        }
        if (dom.getWitherSpawn()) {
            return;
        }
        event.setCancelled(true);
    }
}
