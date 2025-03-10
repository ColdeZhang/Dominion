package cn.lunadeer.dominion.v1_21_spigot.events;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.managers.TeleportManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.material.Colorable;

import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;

public class PlayerEvents implements Listener {
    @EventHandler(priority = EventPriority.LOWEST) // anchor
    public void onRespawnAnchor(PlayerRespawnEvent event) {
        Player bukkitPlayer = event.getPlayer();
        if (!event.isAnchorSpawn()) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getRespawnLocation());
        if (!checkPrivilegeFlag(dom, Flags.ANCHOR, bukkitPlayer, null)) {
            if (bukkitPlayer.getRespawnLocation() != null) {
                event.setRespawnLocation(bukkitPlayer.getRespawnLocation());
            } else {
                event.setRespawnLocation(bukkitPlayer.getWorld().getSpawnLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST) // anchor
    public void onAnchorInteractive(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Material clicked = block.getType();
        if (clicked != Material.RESPAWN_ANCHOR) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.ANCHOR, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // animal_killing
    public void onAnimalKilling(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player bukkitPlayer)) {
            return;
        }
        // 如果不是动物 则不处理
        if (!(event.getEntity() instanceof Animals)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getEntity().getLocation());
        checkPrivilegeFlag(dom, Flags.ANIMAL_KILLING, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // anvil
    public void onAnvilUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.ANVIL) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getClickedBlock().getLocation());
        checkPrivilegeFlag(dom, Flags.ANVIL, event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // beacon
    public void onBeaconUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.BEACON) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getClickedBlock().getLocation());
        checkPrivilegeFlag(dom, Flags.BEACON, event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // bed
    public void onBedUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player bukkitPlayer = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!(Tag.BEDS.isTagged(block.getType()))) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.BED, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // brew
    public void onBrewUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.BREWING_STAND) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getClickedBlock().getLocation());
        checkPrivilegeFlag(dom, Flags.BREW, event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // break
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (onBreak(player, event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // break - item frame
    public void onItemFrameBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }
        Entity entity = event.getEntity();
        if (event.getCause() != HangingBreakEvent.RemoveCause.ENTITY) {
            return;
        }
        if (entity instanceof ItemFrame) {
            if (((ItemFrame) entity).getItem().getType() != Material.AIR) {
                if (!hasContainerPermission((Player) event.getRemover(), entity.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (onBreak((Player) event.getRemover(), entity.getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // break - armor stand
    public void onArmorStandBreak(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ArmorStand)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (onBreak((Player) event.getDamager(), entity.getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    public static boolean onBreak(Player player, Location location) {
        DominionDTO dom = CacheManager.instance.getDominion(location);
        return checkPrivilegeFlag(dom, Flags.BREAK_BLOCK, player, null);
    }

    @EventHandler(priority = EventPriority.LOWEST) // button
    public void onButton(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) {
            return;
        }
        Block block = event.getClickedBlock();
        if (!Tag.BUTTONS.isTagged(block.getType())) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.BUTTON, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // cake
    public void eatCake(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Material clicked = block.getType();
        if (clicked != Material.CAKE) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.CAKE, player, event);
    }

    // 检查是否有容器权限
    private static boolean hasContainerPermission(Player player, Location loc) {
        DominionDTO dom;
        if (loc == null) {
            dom = null;
        } else {
            dom = CacheManager.instance.getDominion(loc);
        }
        return checkPrivilegeFlag(dom, Flags.CONTAINER, player, null);
    }

    @EventHandler(priority = EventPriority.LOWEST) // container
    public void openContainer(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.CHEST &&
                event.getClickedBlock().getType() != Material.BARREL &&
                event.getClickedBlock().getType() != Material.SHULKER_BOX) {
            return;
        }
        if (hasContainerPermission(event.getPlayer(), event.getClickedBlock().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // container (armor stand)
    public void manipulateArmorStand(PlayerArmorStandManipulateEvent event) {
        Player bukkitPlayer = event.getPlayer();
        if (hasContainerPermission(bukkitPlayer, event.getRightClicked().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // container （item frame put）
    public void putSomeOnItemFrame(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof ItemFrame itemFrame)) {
            return;
        }
        if (itemFrame.getItem().getType() != Material.AIR) {
            return;
        }
        Player bukkitPlayer = event.getPlayer();
        if (hasContainerPermission(bukkitPlayer, entity.getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // container （item frame get）
    public void removeSomeOnItemFrame(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemFrame itemFrame)) {
            return;
        }
        if (itemFrame.getItem().getType() == Material.AIR) {
            return;
        }
        if (!(event.getDamager() instanceof Player bukkitPlayer)) {
            return;
        }
        if (hasContainerPermission(bukkitPlayer, entity.getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // container （item frame get）
    public void removeSomeOnItemFrameByArrow(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemFrame itemFrame)) {
            return;
        }
        if (itemFrame.getItem().getType() == Material.AIR) {
            return;
        }
        if (!(event.getDamager() instanceof Arrow arrow)) {
            return;
        }
        if (!(arrow.getShooter() instanceof Player bukkitPlayer)) {
            return;
        }
        if (hasContainerPermission(bukkitPlayer, itemFrame.getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // craft
    public void onCraft(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.CRAFTING_TABLE) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getClickedBlock().getLocation());
        checkPrivilegeFlag(dom, Flags.CRAFT, event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // crafter
    public void onCrafterOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.CRAFTER) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getClickedBlock().getLocation());
        checkPrivilegeFlag(dom, Flags.CRAFTER, event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // comparer
    public void comparerChange(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Material clicked = event.getClickedBlock().getType();
        if (clicked != Material.COMPARATOR) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(event.getClickedBlock().getLocation());
        checkPrivilegeFlag(dom, Flags.COMPARER, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // door
    public void doorUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!Tag.DOORS.isTagged(block.getType()) && !Tag.TRAPDOORS.isTagged(block.getType())) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(event.getClickedBlock().getLocation());
        checkPrivilegeFlag(dom, Flags.DOOR, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // dragon_egg
    public void touchDragonEdd(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (block.getType() != Material.DRAGON_EGG) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.DRAGON_EGG, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // dye
    public void dyeEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Colorable)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(entity.getLocation());
        checkPrivilegeFlag(dom, Flags.DYE, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // edit sign
    public void onSignOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!(Tag.SIGNS.isTagged(block.getType()))) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.EDIT_SIGN, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // edit sign
    public void onSignEdit(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.EDIT_SIGN, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // egg
    public void onThrowingEgg(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) {
            return;
        }
        if (projectile.getType() != EntityType.EGG) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(projectile.getLocation());
        checkPrivilegeFlag(dom, Flags.EGG, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // enchant
    public void onEnchant(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.ENCHANTING_TABLE) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getClickedBlock().getLocation());
        checkPrivilegeFlag(dom, Flags.ENCHANT, event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // ender_pearl
    public void onThrowingEndPearl(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) {
            return;
        }
        if (projectile.getType() != EntityType.ENDER_PEARL) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(projectile.getLocation());
        checkPrivilegeFlag(dom, Flags.ENDER_PEARL, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // feed
    public void onFeedAnimal(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Animals)) {
            return;
        }
        // if shearing sheep instead
        if (event.getPlayer().getInventory().getItem(event.getHand()).getType() == Material.SHEARS) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(event.getRightClicked().getLocation());
        checkPrivilegeFlag(dom, Flags.FEED, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // harvest
    public void onHarvest(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.COCOA &&
                block.getType() != Material.WHEAT &&
                block.getType() != Material.CARROTS &&
                block.getType() != Material.POTATOES &&
                block.getType() != Material.BEETROOTS &&
                block.getType() != Material.NETHER_WART &&
                block.getType() != Material.SWEET_BERRY_BUSH &&
                block.getType() != Material.MELON &&
                block.getType() != Material.PUMPKIN &&
                block.getType() != Material.SUGAR_CANE &&
                block.getType() != Material.BAMBOO &&
                block.getType() != Material.CACTUS &&
                block.getType() != Material.CHORUS_PLANT &&
                block.getType() != Material.CHORUS_FLOWER &&
                block.getType() != Material.KELP &&
                block.getType() != Material.KELP_PLANT) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.HARVEST, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // honey
    public void honeyInteractive(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Material clicked = block.getType();
        if (clicked != Material.BEEHIVE && clicked != Material.BEE_NEST) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.HONEY, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // hook
    public void onHook(PlayerFishEvent event) {
        Entity caught = event.getCaught();
        if (caught == null) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(caught.getLocation());
        checkPrivilegeFlag(dom, Flags.HOOK, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // hopper
    public void openHopper(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.HOPPER &&
                event.getClickedBlock().getType() != Material.DROPPER &&
                event.getClickedBlock().getType() != Material.DISPENSER &&
                event.getClickedBlock().getType() != Material.FURNACE &&
                event.getClickedBlock().getType() != Material.BLAST_FURNACE &&
                event.getClickedBlock().getType() != Material.SMOKER &&
                event.getClickedBlock().getType() != Material.FLOWER_POT
        ) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getClickedBlock().getLocation());
        checkPrivilegeFlag(dom, Flags.HOPPER, event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // ignite
    public void onPlayerIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getBlock().getLocation());
        checkPrivilegeFlag(dom, Flags.IGNITE, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // item_frame_interactive
    public void onItemFrameInteractive(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof ItemFrame itemFrame)) {
            return;
        }
        if (itemFrame.getItem().getType() == Material.AIR) {
            // 为空则当作容器处理见 putSomeOnItemFrame
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(entity.getLocation());
        checkPrivilegeFlag(dom, Flags.ITEM_FRAME_INTERACTIVE, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // lever
    public void onLever(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Material clicked = block.getType();
        if (clicked != Material.LEVER) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.LEVER, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // monster_killing
    public void onMonsterKilling(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player bukkitPlayer)) {
            return;
        }
        // 如果不是怪物 则不处理
        Entity entity = event.getEntity();
        if (!(entity instanceof Monster)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(entity.getLocation());
        checkPrivilegeFlag(dom, Flags.MONSTER_KILLING, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // move
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getPlayerCurrentDominion(player);
        if (!checkPrivilegeFlag(dom, Flags.MOVE, player, null)) {
            Location to = player.getLocation();
            assert dom != null;
            int x1 = Math.abs(to.getBlockX() - dom.getCuboid().x1());
            int x2 = Math.abs(to.getBlockX() - dom.getCuboid().x2());
            int z1 = Math.abs(to.getBlockZ() - dom.getCuboid().z1());
            int z2 = Math.abs(to.getBlockZ() - dom.getCuboid().z2());
            // find min distance
            int min = Math.min(Math.min(x1, x2), Math.min(z1, z2));
            if (min == x1) {
                to.setX(dom.getCuboid().x1() - 2);
            } else if (min == x2) {
                to.setX(dom.getCuboid().x2() + 2);
            } else if (min == z1) {
                to.setZ(dom.getCuboid().z1() - 2);
            } else {
                to.setZ(dom.getCuboid().z2() + 2);
            }
            TeleportManager.doTeleportSafely(player, to);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST) // note_block
    public void onNoteBlockClicked(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        Material clicked = block.getType();
        if (clicked != Material.NOTE_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.NOTE_BLOCK, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // place
    public void onPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (onPlace(player, event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // place - lava or water
    public void onPlaceLavaOrWater(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (onPlace(player, event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // place - item frame
    public void placeItemFrame(HangingPlaceEvent event) {
        Entity entity = event.getEntity();
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (onPlace(player, entity.getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // place - armor stand
    public void placeArmorStand(EntityPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof ArmorStand)) {
            return;
        }
        if (onPlace(player, entity.getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    public static boolean onPlace(Player player, Location location) {
        DominionDTO dom = CacheManager.instance.getDominion(location);
        return checkPrivilegeFlag(dom, Flags.PLACE, player, null);
    }

    @EventHandler(priority = EventPriority.LOWEST) // pressure
    public void onPressure(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!Tag.PRESSURE_PLATES.isTagged(block.getType())) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.PRESSURE, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // riding
    public void onRiding(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getMount().getLocation());
        checkPrivilegeFlag(dom, Flags.RIDING, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // repeater
    public void onRepeaterChange(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Block block = event.getClickedBlock();
        Material clicked = block.getType();
        if (clicked != Material.REPEATER) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(block.getLocation());
        checkPrivilegeFlag(dom, Flags.REPEATER, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // shear
    public void onShear(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        DominionDTO dom = CacheManager.instance.getDominion(event.getEntity().getLocation());
        checkPrivilegeFlag(dom, Flags.SHEAR, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // shoot
    public void onShootArrowSnowball(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) {
            return;
        }
        if (projectile.getType() == EntityType.ENDER_PEARL || projectile.getType() == EntityType.EGG) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(projectile.getLocation());
        checkPrivilegeFlag(dom, Flags.SHOOT, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // shoot - wind_charge knock back
    public void onWindChargeKnockBack(EntityKnockbackByEntityEvent event) {
        Entity entity = event.getSourceEntity();
        if (!(entity instanceof WindCharge windCharge)) {
            return;
        }
        if (!(windCharge.getShooter() instanceof Player player)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(windCharge.getLocation());
        checkPrivilegeFlag(dom, Flags.SHOOT, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // trade
    public void onTrade(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.VILLAGER &&
                event.getRightClicked().getType() != EntityType.WANDERING_TRADER) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getRightClicked().getLocation());
        checkPrivilegeFlag(dom, Flags.TRADE, event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // vehicle_destroy
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player player)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getVehicle().getLocation());
        checkPrivilegeFlag(dom, Flags.VEHICLE_DESTROY, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST) // vehicle_spawn
    public void onVehicleSpawn(EntityPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof Vehicle)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(entity.getLocation());
        checkPrivilegeFlag(dom, Flags.VEHICLE_SPAWN, player, event);
    }

    @EventHandler(priority = EventPriority.LOWEST)  // villager_killing
    public void onVillagerKilling(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        if (!(event.getEntity() instanceof Villager)) {
            return;
        }
        DominionDTO dom = CacheManager.instance.getDominion(event.getEntity().getLocation());
        checkPrivilegeFlag(dom, Flags.VILLAGER_KILLING, player, event);
    }
}
