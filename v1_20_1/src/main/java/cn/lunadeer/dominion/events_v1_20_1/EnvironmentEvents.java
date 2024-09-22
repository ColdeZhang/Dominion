package cn.lunadeer.dominion.events_v1_20_1;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static cn.lunadeer.dominion.utils.EventUtils.checkFlag;
import static org.bukkit.Material.FARMLAND;

public class EnvironmentEvents implements Listener {
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

    @EventHandler(priority = EventPriority.HIGHEST) // creeper_explode - bed anchor
    public void onBedAnchorExplosion(BlockExplodeEvent event) {
        BlockState block = event.getExplodedBlockState();
        if (block == null) {
            return;
        }
        event.blockList().removeIf(blockState -> {
            DominionDTO dom = Cache.instance.getDominionByLoc(blockState.getLocation());
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

    @EventHandler(priority = EventPriority.HIGHEST) // creeper_explode - item frame
    public void onItemFrameShot(ProjectileHitEvent event) {
        Entity hit = event.getHitEntity();
        if (hit == null) {
            return;
        }
        if (event.getEntity().getShooter() instanceof Player) {
            return;
        }
        if (!(hit instanceof Hanging)) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(hit.getLocation());
        checkFlag(dom, Flag.CREEPER_EXPLODE, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // creeper_explode - armor stand
    public void onArmorStandExploded(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ARMOR_STAND) {
            return;
        }
        if (isNotExplodeEntity(event.getDamager())) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(entity.getLocation());
        checkFlag(dom, Flag.CREEPER_EXPLODE, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // item_frame_proj_damage
    public void removeSomeOnItemFrameByArrow(HangingBreakByEntityEvent event) {
        if (event.getCause() != HangingBreakEvent.RemoveCause.ENTITY) {
            return;
        }
        Entity remover = event.getRemover();
        if (!(remover instanceof Projectile projectile)) {
            return;
        }
        if (!(projectile.getShooter() instanceof Player)) {
            // 玩家破坏由 玩家 break 权限控制
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(event.getEntity().getLocation());
        checkFlag(dom, Flag.ITEM_FRAME_PROJ_DAMAGE, event);
    }

    private static boolean isNotExplodeEntity(Entity damager) {
        return damager.getType() != EntityType.CREEPER
                && damager.getType() != EntityType.WITHER_SKULL
                && damager.getType() != EntityType.FIREBALL
                && damager.getType() != EntityType.ENDER_CRYSTAL
                && damager.getType() != EntityType.SMALL_FIREBALL
                && damager.getType() != EntityType.DRAGON_FIREBALL;
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
        if (entity.getType() != EntityType.MINECART_TNT && entity.getType() != EntityType.PRIMED_TNT) {
            return;
        }
        event.blockList().removeIf(block -> {
            DominionDTO dom = Cache.instance.getDominionByLoc(block.getLocation());
            return !checkFlag(dom, Flag.TNT_EXPLODE, null);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST) // tnt_explode - entity
    public void onArmorStandExplodedByTnt(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity harmer = event.getDamager();
        if (harmer.getType() != EntityType.MINECART_TNT && harmer.getType() != EntityType.PRIMED_TNT) {
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

    /*
    TRIG_PRESSURE_PROJ("trig_pressure_proj", "投掷物触发压力板", "投掷物（箭/风弹/雪球）是否可以触发压力板", false, true, true),
    TRIG_PRESSURE_MOB("trig_pressure_mob", "生物触发压力板", "生物（不包含玩家）是否可以触发压力板", false, true, true),
    TRIG_PRESSURE_DROP("trig_pressure_drop", "掉落物触发压力板", "掉落物是否可以触发压力板", false, true, true),
     */
    @EventHandler(priority = EventPriority.HIGHEST) // trig_pressure_proj
    public void onPressurePlateTriggeredByProjectile(EntityInteractEvent event) {
        if (!(event.getEntity() instanceof Projectile)) {
            return;
        }
        Block block = event.getBlock();
        if (!Tag.PRESSURE_PLATES.isTagged(block.getType())) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(block.getLocation());
        checkFlag(dom, Flag.TRIG_PRESSURE_PROJ, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // trig_pressure_mob
    public void onPressurePlateTriggeredByMob(EntityInteractEvent event) {
        if (!(event.getEntity() instanceof Mob)) {
            return;
        }
        Block block = event.getBlock();
        if (!Tag.PRESSURE_PLATES.isTagged(block.getType())) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(block.getLocation());
        checkFlag(dom, Flag.TRIG_PRESSURE_MOB, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // trig_pressure_drop
    public void onPressurePlateTriggeredByDrop(EntityInteractEvent event) {
        if (!(event.getEntity() instanceof Item)) {
            return;
        }
        Block block = event.getBlock();
        if (!Tag.PRESSURE_PLATES.isTagged(block.getType())) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominionByLoc(block.getLocation());
        checkFlag(dom, Flag.TRIG_PRESSURE_DROP, event);
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

    @EventHandler(priority = EventPriority.HIGHEST) // wither_spawn - explode
    public void onWitherSpawnExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.WITHER) {
            return;
        }
        event.blockList().removeIf(block -> {
            DominionDTO dom = Cache.instance.getDominionByLoc(block.getLocation());
            return !checkFlag(dom, Flag.WITHER_SPAWN, null);
        });
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHopper(InventoryMoveItemEvent event) {    // hopper_outside
        Inventory hopper = event.getDestination();
        Inventory inventory = event.getSource();
        DominionDTO hopperDom = Cache.instance.getDominionByLoc(hopper.getLocation());
        DominionDTO inventoryDom = Cache.instance.getDominionByLoc(inventory.getLocation());
        if (hopperDom == null && inventoryDom != null) {
            checkFlag(inventoryDom, Flag.HOPPER_OUTSIDE, event);
        }
        if (hopperDom != null && inventoryDom != null) {
            if (!hopperDom.getId().equals(inventoryDom.getId())) {
                checkFlag(inventoryDom, Flag.HOPPER_OUTSIDE, event);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPushedByPiston(BlockPistonExtendEvent event) {   // piston_outside
        Block piston = event.getBlock();
        DominionDTO pistonDom = Cache.instance.getDominionByLoc(piston.getLocation());
        BlockFace direction = event.getDirection();
        Block endBlockAfterPush = piston.getRelative(direction, event.getBlocks().size() + 1);
        DominionDTO endBlockDom = Cache.instance.getDominionByLoc(endBlockAfterPush.getLocation());
        if (pistonDom != null && endBlockDom == null) {
            checkFlag(pistonDom, Flag.PISTON_OUTSIDE, event);
        }
        if (pistonDom == null && endBlockDom != null) {
            checkFlag(endBlockDom, Flag.PISTON_OUTSIDE, event);
        }
        if (pistonDom != null && endBlockDom != null) {
            if (!pistonDom.getId().equals(endBlockDom.getId())) {
                if (!endBlockDom.getFlagValue(Flag.PISTON_OUTSIDE) || !pistonDom.getFlagValue(Flag.PISTON_OUTSIDE)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGravityBlockFalling(EntityChangeBlockEvent event) {   // gravity_block
        Entity entity = event.getEntity();
        if (!(entity instanceof FallingBlock)) {
            return;
        }
        Block block = event.getBlock();
        if (event.getTo().isAir()) {
            fallingBlockMap.put(entity.getUniqueId(), block.getLocation());
        } else {
            Location locStart = fallingBlockMap.get(entity.getUniqueId());
            if (locStart == null) {
                return;
            }
            fallingBlockMap.remove(entity.getUniqueId());
            Location locEnd = block.getLocation();
            DominionDTO domStart = Cache.instance.getDominionByLoc(locStart);
            DominionDTO domEnd = Cache.instance.getDominionByLoc(locEnd);
            if (domEnd == null) {
                return;
            }
            if (domStart != null && domStart.getId().equals(domEnd.getId())) {
                return;
            }
            if (!checkFlag(domEnd, Flag.GRAVITY_BLOCK, null)) {
                event.setCancelled(true);
                locEnd.getWorld().dropItemNaturally(locEnd, new ItemStack(((FallingBlock) entity).getBlockData().getMaterial()));
                entity.remove();
            }
        }
    }

    private static final Map<UUID, Location> fallingBlockMap = new java.util.HashMap<>();
}
