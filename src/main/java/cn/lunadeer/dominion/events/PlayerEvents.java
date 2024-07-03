package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.Teleport;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Colorable;
import org.spigotmc.event.entity.EntityMountEvent;

import static cn.lunadeer.dominion.events.Apis.checkFlag;
import static cn.lunadeer.dominion.events.Apis.getInvDominion;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player bukkitPlayer = event.getPlayer();
        PlayerDTO player = PlayerDTO.get(bukkitPlayer);
        player.onJoin(bukkitPlayer.getName()); // update name
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player bukkitPlayer = event.getPlayer();
        Cache.instance.onPlayerQuit(bukkitPlayer);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // anchor
    public void onRespawnAnchor(PlayerRespawnEvent event) {
        Player bukkitPlayer = event.getPlayer();
        if (!event.isAnchorSpawn()) {
            return;
        }
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        if (!checkFlag(dom, Flag.ANCHOR, bukkitPlayer, null)) {
            if (bukkitPlayer.getBedSpawnLocation() != null) {
                event.setRespawnLocation(bukkitPlayer.getBedSpawnLocation());
            } else {
                event.setRespawnLocation(bukkitPlayer.getWorld().getSpawnLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST) // animal_killing
    public void onAnimalKilling(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        // 如果不是动物 则不处理
        if (!(event.getEntity() instanceof Animals)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getDamager();
        DominionDTO dom = Cache.instance.getDominion(event.getEntity().getLocation());
        checkFlag(dom, Flag.ANIMAL_KILLING, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // anvil
    public void onAnvilUse(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        checkFlag(dom, Flag.ANVIL, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // beacon
    public void onBeaconUse(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.BEACON) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        checkFlag(dom, Flag.BEACON, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // bed
    public void onBedUse(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
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
        DominionDTO dom = Cache.instance.getDominion(block.getLocation());
        checkFlag(dom, Flag.BED, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // brew
    public void onBrewUse(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        checkFlag(dom, Flag.BREW, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // break
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (onBreak(player, event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // break - item frame
    public void onItemFrameBreak(HangingBreakByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemFrame)) {
            return;
        }
        ItemFrame itemFrame = (ItemFrame) entity;
        Entity remover = event.getRemover();
        if (!(remover instanceof Player)) {
            return;
        }
        if (onBreak((Player) event.getRemover(), itemFrame.getLocation())) {
            return;
        }
        itemFrame.remove();
        event.setCancelled(true);
    }

    public static boolean onBreak(Player player, Location location) {
        DominionDTO dom = Cache.instance.getDominion(location);
        return checkFlag(dom, Flag.BREAK_BLOCK, player, null);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // button
    public void onButton(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
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
        DominionDTO dom = Cache.instance.getDominion(block.getLocation());
        checkFlag(dom, Flag.BUTTON, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // cake
    public void eatCake(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
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
        DominionDTO dom = Cache.instance.getDominion(block.getLocation());
        checkFlag(dom, Flag.CAKE, player, event);
    }

    // 检查是否有容器权限
    private static boolean hasContainerPermission(Player player, Location loc) {
        DominionDTO dom;
        if (loc == null) {
            dom = null;
        } else {
            dom = Cache.instance.getDominion(loc);
        }
        return checkFlag(dom, Flag.CONTAINER, player, null);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // container
    public void openContainer(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST &&
                event.getInventory().getType() != InventoryType.BARREL &&
                event.getInventory().getType() != InventoryType.SHULKER_BOX) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getPlayer();
        if (hasContainerPermission(bukkitPlayer, event.getInventory().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // container (armor stand)
    public void manipulateArmorStand(PlayerArmorStandManipulateEvent event) {
        Player bukkitPlayer = event.getPlayer();
        if (hasContainerPermission(bukkitPlayer, event.getRightClicked().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // container （item frame）
    public void putSomeOnItemFrame(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof ItemFrame)) {
            return;
        }
        Player bukkitPlayer = event.getPlayer();
        if (hasContainerPermission(bukkitPlayer, event.getRightClicked().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // container （item frame）
    public void removeSomeOnItemFrame(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemFrame)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getDamager();
        if (hasContainerPermission(bukkitPlayer, event.getEntity().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // craft
    public void onCraft(InventoryOpenEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getType() != InventoryType.WORKBENCH) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getPlayer();
        DominionDTO dom = getInvDominion(bukkitPlayer, inv);
        checkFlag(dom, Flag.CRAFT, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // comparer
    public void comparerChange(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
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
        DominionDTO dom = Cache.instance.getDominion(event.getClickedBlock().getLocation());
        checkFlag(dom, Flag.COMPARER, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // door
    public void doorUse(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!Tag.DOORS.isTagged(block.getType())) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getDominion(event.getClickedBlock().getLocation());
        checkFlag(dom, Flag.DOOR, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // dragon_egg
    public void touchDragonEdd(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (block.getType() != Material.DRAGON_EGG) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getDominion(block.getLocation());
        checkFlag(dom, Flag.DRAGON_EGG, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // dye
    public void dyeEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Colorable)) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(entity.getLocation());
        checkFlag(dom, Flag.DYE, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // edit sign
    public void onSignOpen(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
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
        DominionDTO dom = Cache.instance.getDominion(block.getLocation());
        checkFlag(dom, Flag.EDIT_SIGN, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // edit sign
    public void onSignEdit(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        DominionDTO dom = Cache.instance.getDominion(block.getLocation());
        checkFlag(dom, Flag.EDIT_SIGN, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // egg
    public void onThrowingEgg(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        if (event.getEntity().getType() != EntityType.EGG) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        checkFlag(dom, Flag.EGG, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // enchant
    public void onEnchant(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.ENCHANTING) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getPlayer();
        DominionDTO dom = getInvDominion(bukkitPlayer, event.getInventory());
        checkFlag(dom, Flag.ENCHANT, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // ender_pearl
    public void onThrowingEndPearl(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        if (event.getEntity().getType() != EntityType.ENDER_PEARL) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        checkFlag(dom, Flag.ENDER_PEARL, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // feed
    public void onFeedAnimal(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Animals)) {
            return;
        }
        // if shearing sheep instead
        if (event.getPlayer().getInventory().getItem(event.getHand()).getType() == Material.SHEARS) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getDominion(event.getRightClicked().getLocation());
        checkFlag(dom, Flag.FEED, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // harvest
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
        DominionDTO dom = Cache.instance.getDominion(block.getLocation());
        checkFlag(dom, Flag.HARVEST, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // honey
    public void honeyInteractive(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
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
        DominionDTO dom = Cache.instance.getDominion(block.getLocation());
        checkFlag(dom, Flag.HONEY, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // hook
    public void onHook(PlayerFishEvent event) {
        Entity caught = event.getCaught();
        if (caught == null) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getDominion(caught.getLocation());
        checkFlag(dom, Flag.HOOK, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // hopper
    public void openHopper(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.HOPPER &&
                event.getInventory().getType() != InventoryType.DROPPER &&
                event.getInventory().getType() != InventoryType.DISPENSER &&
                event.getInventory().getType() != InventoryType.FURNACE &&
                event.getInventory().getType() != InventoryType.BLAST_FURNACE &&
                event.getInventory().getType() != InventoryType.SMOKER
        ) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getPlayer();
        DominionDTO dom = getInvDominion(bukkitPlayer, event.getInventory());
        checkFlag(dom, Flag.HOPPER, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // ignite
    public void onPlayerIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(event.getBlock().getLocation());
        checkFlag(dom, Flag.IGNITE, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // lever
    public void onLever(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
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
        DominionDTO dom = Cache.instance.getDominion(block.getLocation());
        checkFlag(dom, Flag.LEVER, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // monster_killing
    public void onMonsterKilling(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        // 如果不是怪物 则不处理
        Entity entity = event.getEntity();
        if (!(entity instanceof Monster)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getDamager();
        DominionDTO dom = Cache.instance.getDominion(entity.getLocation());
        checkFlag(dom, Flag.MONSTER_KILLING, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // move
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (!checkFlag(dom, Flag.MOVE, player, null)) {
            Location to = player.getLocation();
            int x1 = Math.abs(to.getBlockX() - dom.getX1());
            int x2 = Math.abs(to.getBlockX() - dom.getX2());
            int z1 = Math.abs(to.getBlockZ() - dom.getZ1());
            int z2 = Math.abs(to.getBlockZ() - dom.getZ2());
            // find min distance
            int min = Math.min(Math.min(x1, x2), Math.min(z1, z2));
            if (min == x1) {
                to.setX(dom.getX1() - 2);
            } else if (min == x2) {
                to.setX(dom.getX2() + 2);
            } else if (min == z1) {
                to.setZ(dom.getZ1() - 2);
            } else {
                to.setZ(dom.getZ2() + 2);
            }
            Teleport.doTeleportSafely(player, to).thenAccept((success) -> {
                if (!success) {
                    Notification.warn(player, "传送失败，你将被传送到复活点");
                    player.teleportAsync(player.getBedSpawnLocation() == null ?
                                    player.getWorld().getSpawnLocation() :
                                    player.getBedSpawnLocation()
                            , PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST) // place
    public void onPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (onPlace(player, event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // place - lava or water
    public void onPlaceLavaOrWater(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (onPlace(player, event.getBlock().getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // place - item frame
    public void placeItemFrame(HangingPlaceEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ITEM_FRAME && entity.getType() != EntityType.GLOW_ITEM_FRAME) {
            return;
        }
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (onPlace(player, entity.getLocation())) {
            return;
        }
        event.setCancelled(true);
    }

    public static boolean onPlace(Player player, Location location) {
        DominionDTO dom = Cache.instance.getDominion(location);
        return checkFlag(dom, Flag.PLACE, player, null);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // pressure
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
        DominionDTO dom = Cache.instance.getDominion(block.getLocation());
        checkFlag(dom, Flag.PRESSURE, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // riding
    public void onRiding(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        DominionDTO dom = Cache.instance.getDominion(event.getMount().getLocation());
        checkFlag(dom, Flag.RIDING, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // repeater
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
        DominionDTO dom = Cache.instance.getDominion(block.getLocation());
        checkFlag(dom, Flag.REPEATER, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // shear
    public void onShear(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getDominion(event.getEntity().getLocation());
        checkFlag(dom, Flag.SHEAR, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // shoot
    public void onShootArrowSnowball(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        if (event.getEntity().getType() != EntityType.ARROW &&
                event.getEntity().getType() != EntityType.SNOWBALL &&
                event.getEntity().getType() != EntityType.TRIDENT) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        checkFlag(dom, Flag.SHOOT, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // trade
    public void onTrade(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.MERCHANT) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getPlayer();
        DominionDTO dom = getInvDominion(bukkitPlayer, event.getInventory());
        checkFlag(dom, Flag.TRADE, bukkitPlayer, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // vehicle_destroy
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getAttacker();
        DominionDTO dom = Cache.instance.getDominion(event.getVehicle().getLocation());
        checkFlag(dom, Flag.VEHICLE_DESTROY, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // vehicle_spawn
    public void onVehicleSpawn(EntityPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof Vehicle)) {
            return;
        }
        DominionDTO dom = Cache.instance.getDominion(entity.getLocation());
        checkFlag(dom, Flag.VEHICLE_SPAWN, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)  // villager_killing
    public void onVillagerKilling(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Villager)) {
            return;
        }
        Player player = (Player) event.getDamager();
        DominionDTO dom = Cache.instance.getDominion(event.getEntity().getLocation());
        checkFlag(dom, Flag.VILLAGER_KILLING, player, event);
    }
}
