package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

import static cn.lunadeer.dominion.events.Apis.checkFlag;
import static org.bukkit.Material.FARMLAND;

public class EnvironmentEvents_1_20_1 implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST) // creeper_explode
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        XLogger.debug("EntityExplodeEvent: " + entity.getType());
        if (isNotExplodeEntity(entity)) {
            return;
        }
        XLogger.debug("blockList" + event.blockList().size());
        event.blockList().removeIf(block -> {
            DominionDTO dom = Cache.instance.getDominionByLoc(block.getLocation());
            return !checkFlag(dom, Flag.CREEPER_EXPLODE, null);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST) // creeper_explode - item frame
    public void onItemFrameExploded(HangingBreakByEntityEvent event) {
        Entity entity = event.getEntity();
        if (event.getCause() != HangingBreakEvent.RemoveCause.EXPLOSION) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(entity.getLocation());
        checkFlag(dom, Flag.CREEPER_EXPLODE, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // creeper_explode - armor stand
    public void onArmorStandExploded(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ARMOR_STAND) {
            return;
        }
        if (event.getDamager() instanceof Player) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(entity.getLocation());
        checkFlag(dom, Flag.CREEPER_EXPLODE, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // creeper_explode - other projectiles
    public void removeSomeOnItemFrameByArrow(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemFrame)) {
            return;
        }
        ItemFrame itemFrame = (ItemFrame) entity;
        if (!(event.getDamager() instanceof Projectile)) {
            return;
        }
        Projectile arrow = (Projectile) event.getDamager();
        if (arrow.getShooter() instanceof Player) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(itemFrame.getLocation());
        checkFlag(dom, Flag.CREEPER_EXPLODE, event);
    }

    private static boolean isNotExplodeEntity(Entity damager) {
        return damager.getType() != EntityType.CREEPER
                && damager.getType() != EntityType.WITHER_SKULL
                && damager.getType() != EntityType.FIREBALL
                && damager.getType().getTypeId() != 200;
        // 200 -> end_crystal
    }

    @EventHandler(priority = EventPriority.HIGHEST) // dragon_break_block
    public void onDragonBreakBlock(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ENDER_DRAGON) {
            return;
        }
        event.blockList().removeIf(block -> {
            DominionDTO dom = Cache.instance.getDominionByLoc(block.getLocation());
            return !checkFlag(dom, Flag.DRAGON_BREAK_BLOCK, null);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST) // fire_spread
    public void onFireSpread(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            // 如果点燃事件没有玩家触发，那么就是火焰蔓延
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(event.getBlock().getLocation());
        checkFlag(dom, Flag.FIRE_SPREAD, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // flow_in_protection
    public void onLiquidFlowIn(BlockFromToEvent event) {
        Location from = event.getBlock().getLocation();
        Location to = event.getToBlock().getLocation();
        DominionDTO dom_to = Cache.instance.getDominionByLoc(to);
        if (dom_to == null) {
            return;
        }
        DominionDTO dom_from = Cache.instance.getDominionByLoc(from);
        if (dom_from != null) {
            if (Objects.equals(dom_from.getId(), dom_to.getId())) {
                return;
            }
        }
        checkFlag(dom_to, Flag.FLOW_IN_PROTECTION, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // mob_drop_item
    public void onMobDropItem(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(entity.getLocation());
        if (dom == null) {
            return;
        }
        if (!Flag.MOB_DROP_ITEM.getEnable()) {
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
        // 45 -> minecart_tnt, 20 -> primed_tnt
        if (entity.getType().getTypeId() != 45 && entity.getType().getTypeId() != 20) {
            return;
        }
        event.blockList().removeIf(block -> {
            DominionDTO dom = Cache.instance.getDominionByLoc(block.getLocation());
            return !checkFlag(dom, Flag.TNT_EXPLODE, null);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST) // tnt_explode - armor stand
    public void onArmorStandExplodedByTnt(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ARMOR_STAND) {
            return;
        }
        Entity damager = event.getDamager();
        // 45 -> minecart_tnt, 20 -> primed_tnt
        if (entity.getType().getTypeId() != 45 && entity.getType().getTypeId() != 20) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(entity.getLocation());
        checkFlag(dom, Flag.TNT_EXPLODE, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // trample
    public void onFarmlandTrample(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (block.getType() != FARMLAND) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(block.getLocation());
        checkFlag(dom, Flag.TRAMPLE, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // wither_spawn
    public void onWitherSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.WITHER) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(entity.getLocation());
        checkFlag(dom, Flag.WITHER_SPAWN, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // ender_man spawn
    public void onEnderManSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ENDERMAN) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(entity.getLocation());
        checkFlag(dom, Flag.ENDER_MAN, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // ender_man escape
    public void onEnderManEscape(EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ENDERMAN) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(entity.getLocation());
        checkFlag(dom, Flag.ENDER_MAN, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // monster_spawn
    public void onMonsterSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Monster)) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(entity.getLocation());
        checkFlag(dom, Flag.MONSTER_SPAWN, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // animal_spawn
    public void onAnimalSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Animals)) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(entity.getLocation());
        checkFlag(dom, Flag.ANIMAL_SPAWN, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // villager_spawn
    public void onVillagerSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.VILLAGER) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(entity.getLocation());
        checkFlag(dom, Flag.VILLAGER_SPAWN, event);
    }
}
