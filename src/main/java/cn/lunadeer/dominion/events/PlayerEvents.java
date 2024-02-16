package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.utils.Notification;
import io.papermc.paper.event.entity.EntityDyeEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player bukkitPlayer = event.getPlayer();
        PlayerDTO player = PlayerDTO.get(bukkitPlayer);
        player.onJoin(); // update name
    }

    @EventHandler(priority = EventPriority.HIGHEST) // anchor
    public void onRespawnAnchor(PlayerRespawnEvent event) {
        Player bukkitPlayer = event.getPlayer();
        if (!event.isAnchorSpawn()) {
            return;
        }
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(bukkitPlayer, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(bukkitPlayer, dom);
        if (privilege != null) {
            if (!privilege.getAnchor()) {
                Notification.error(bukkitPlayer, "你没有锚点重生权限");
                return;
            } else {
                if (bukkitPlayer.getBedSpawnLocation() != null) {
                    event.setRespawnLocation(bukkitPlayer.getBedSpawnLocation());
                } else {
                    event.setRespawnLocation(bukkitPlayer.getWorld().getSpawnLocation());
                }
            }
        } else {
            if (dom.getAnchor()) {
                Notification.error(bukkitPlayer, "你没有锚点重生权限");
                return;
            } else {
                if (bukkitPlayer.getBedSpawnLocation() != null) {
                    event.setRespawnLocation(bukkitPlayer.getBedSpawnLocation());
                } else {
                    event.setRespawnLocation(bukkitPlayer.getWorld().getSpawnLocation());
                }
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
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(bukkitPlayer, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(bukkitPlayer, dom);
        if (privilege != null) {
            if (privilege.getAnimalKilling()) {
                return;
            }
        } else {
            if (dom.getAnimalKilling()) {
                return;
            }
        }
        Notification.error(bukkitPlayer, "你没有动物击杀权限");
        event.setCancelled(true);
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
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(bukkitPlayer, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(bukkitPlayer, dom);
        if (privilege != null) {
            if (privilege.getAnvil()) {
                return;
            }
        } else {
            if (dom.getAnvil()) {
                return;
            }
        }
        Notification.error(bukkitPlayer, "你没有使用铁砧的权限");
        event.setCancelled(true);
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
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(bukkitPlayer, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(bukkitPlayer, dom);
        if (privilege != null) {
            if (privilege.getBeacon()) {
                return;
            }
        } else {
            if (dom.getBeacon()) {
                return;
            }
        }
        Notification.error(bukkitPlayer, "你没有使用信标的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // bed
    public void onBedUse(PlayerBedEnterEvent event) {
        Player bukkitPlayer = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(bukkitPlayer, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(bukkitPlayer, dom);
        if (privilege != null) {
            if (privilege.getBed()) {
                return;
            }
        } else {
            if (dom.getBed()) {
                return;
            }
        }
        Notification.error(bukkitPlayer, "你没有使用床的权限");
        event.setCancelled(true);
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
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(bukkitPlayer, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(bukkitPlayer, dom);
        if (privilege != null) {
            if (privilege.getBrew()) {
                return;
            }
        } else {
            if (dom.getBrew()) {
                return;
            }
        }
        Notification.error(bukkitPlayer, "你没有使用酿造台的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // break
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getBreak()) {
                return;
            }
        } else {
            if (dom.getBreak()) {
                return;
            }
        }
        Notification.error(player, "你没有破坏方块的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // button
    public void onButton(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) {
            return;
        }
        Material clicked = event.getClickedBlock().getType();
        if (clicked != Material.STONE_BUTTON &&
                clicked != Material.BAMBOO_BUTTON &&
                clicked != Material.OAK_BUTTON &&
                clicked != Material.SPRUCE_BUTTON &&
                clicked != Material.BIRCH_BUTTON &&
                clicked != Material.JUNGLE_BUTTON &&
                clicked != Material.ACACIA_BUTTON &&
                clicked != Material.DARK_OAK_BUTTON &&
                clicked != Material.CRIMSON_BUTTON &&
                clicked != Material.WARPED_BUTTON &&
                clicked != Material.POLISHED_BLACKSTONE_BUTTON &&
                clicked != Material.MANGROVE_BUTTON &&
                clicked != Material.CHERRY_BUTTON) {
            return;
        }
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getButton()) {
                return;
            }
        } else {
            if (dom.getButton()) {
                return;
            }
        }
        Notification.error(player, "你没有使用按钮的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // cake
    public void eatCake(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Material clicked = event.getClickedBlock().getType();
        if (clicked != Material.CAKE) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getCake()) {
                return;
            }
        } else {
            if (dom.getCake()) {
                return;
            }
        }
        Notification.error(player, "你没有吃蛋糕权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // container
    public void openContainer(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST &&
                event.getInventory().getType() != InventoryType.BARREL &&
                event.getInventory().getType() != InventoryType.HOPPER &&
                event.getInventory().getType() != InventoryType.DISPENSER &&
                event.getInventory().getType() != InventoryType.DROPPER &&
                event.getInventory().getType() != InventoryType.SHULKER_BOX) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(bukkitPlayer, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(bukkitPlayer, dom);
        if (privilege != null) {
            if (privilege.getContainer()) {
                return;
            }
        } else {
            if (dom.getContainer()) {
                return;
            }
        }
        Notification.error(bukkitPlayer, "你没有使用容器的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // craft
    public void onCraft(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.WORKBENCH) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(bukkitPlayer, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(bukkitPlayer, dom);
        if (privilege != null) {
            if (privilege.getCraft()) {
                return;
            }
        } else {
            if (dom.getCraft()) {
                return;
            }
        }
        Notification.error(bukkitPlayer, "你没有使用工作台的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // comparer
    public void comparerChange(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Material clicked = event.getClickedBlock().getType();
        if (clicked != Material.COMPARATOR) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getComparer()) {
                return;
            }
        } else {
            if (dom.getComparer()) {
                return;
            }
        }
        Notification.error(player, "你没有使用红石比较器的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // door
    public void doorUse(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Material clicked = event.getClickedBlock().getType();
        if (clicked != Material.IRON_DOOR &&
                clicked != Material.OAK_DOOR &&
                clicked != Material.SPRUCE_DOOR &&
                clicked != Material.BIRCH_DOOR &&
                clicked != Material.JUNGLE_DOOR &&
                clicked != Material.ACACIA_DOOR &&
                clicked != Material.CHERRY_DOOR &&
                clicked != Material.DARK_OAK_DOOR &&
                clicked != Material.MANGROVE_DOOR &&
                clicked != Material.BAMBOO_DOOR &&
                clicked != Material.CRIMSON_DOOR &&
                clicked != Material.WARPED_DOOR &&
                clicked != Material.IRON_TRAPDOOR &&
                clicked != Material.OAK_TRAPDOOR &&
                clicked != Material.SPRUCE_TRAPDOOR &&
                clicked != Material.BIRCH_TRAPDOOR &&
                clicked != Material.JUNGLE_TRAPDOOR &&
                clicked != Material.ACACIA_TRAPDOOR &&
                clicked != Material.CHERRY_TRAPDOOR &&
                clicked != Material.DARK_OAK_TRAPDOOR &&
                clicked != Material.MANGROVE_TRAPDOOR &&
                clicked != Material.BAMBOO_TRAPDOOR &&
                clicked != Material.CRIMSON_TRAPDOOR &&
                clicked != Material.WARPED_TRAPDOOR &&
                clicked != Material.OAK_FENCE_GATE &&
                clicked != Material.SPRUCE_FENCE_GATE &&
                clicked != Material.BIRCH_FENCE_GATE &&
                clicked != Material.JUNGLE_FENCE_GATE &&
                clicked != Material.ACACIA_FENCE_GATE &&
                clicked != Material.CHERRY_FENCE_GATE &&
                clicked != Material.DARK_OAK_FENCE_GATE &&
                clicked != Material.MANGROVE_FENCE_GATE &&
                clicked != Material.BAMBOO_FENCE_GATE &&
                clicked != Material.CRIMSON_FENCE_GATE &&
                clicked != Material.WARPED_FENCE_GATE) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getDoor()) {
                return;
            }
        } else {
            if (dom.getDoor()) {
                return;
            }
        }
        Notification.error(player, "你没有使用门的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // dye
    public void dyeEvent(EntityDyeEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getDye()) {
                return;
            }
        } else {
            if (dom.getDye()) {
                return;
            }
        }
        Notification.error(player, "你没有染色的权限");
        event.setCancelled(true);
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
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getEgg()) {
                return;
            }
        } else {
            if (dom.getEgg()) {
                return;
            }
        }
        Notification.error(player, "你没有扔鸡蛋的权限");
        event.setCancelled(true);
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
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(bukkitPlayer, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(bukkitPlayer, dom);
        if (privilege != null) {
            if (privilege.getEnchant()) {
                return;
            }
        } else {
            if (dom.getEnchant()) {
                return;
            }
        }
        Notification.error(bukkitPlayer, "你没有使用附魔台的权限");
        event.setCancelled(true);
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
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getEnderPearl()) {
                return;
            }
        } else {
            if (dom.getEnderPearl()) {
                return;
            }
        }
        Notification.error(player, "你没有使用末影珍珠的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // feed
    public void onFeedAnimal(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Animals)) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getFeed()) {
                return;
            }
        } else {
            if (dom.getFeed()) {
                return;
            }
        }
        Notification.error(player, "你没有喂养动物的权限");
        event.setCancelled(true);
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
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getHarvest()) {
                return;
            }
        } else {
            if (dom.getHarvest()) {
                return;
            }
        }
        Notification.error(player, "你没有收获的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // honey
    public void honeyInteractive(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Material clicked = event.getClickedBlock().getType();
        if (clicked != Material.BEEHIVE && clicked != Material.BEE_NEST) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getHoney()) {
                return;
            }
        } else {
            if (dom.getHoney()) {
                return;
            }
        }
        Notification.error(player, "你没有与蜜蜂交互的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // hook
    public void onHook(PlayerFishEvent event) {
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getHook()) {
                return;
            }
        } else {
            if (dom.getHook()) {
                return;
            }
        }
        Notification.error(player, "你没有使用钓钩的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // ignite
    public void onPlayerIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getIgnite()) {
                return;
            }
        } else {
            if (dom.getIgnite()) {
                return;
            }
        }
        Notification.error(player, "你没有点火的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // lever
    public void onLever(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Material clicked = event.getClickedBlock().getType();
        if (clicked != Material.LEVER) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getLever()) {
                return;
            }
        } else {
            if (dom.getLever()) {
                return;
            }
        }
        Notification.error(player, "你没有使用拉杆的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // monster_killing
    public void onMonsterKilling(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        // 如果不是动物 则不处理
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }
        Player bukkitPlayer = (Player) event.getDamager();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(bukkitPlayer, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(bukkitPlayer, dom);
        if (privilege != null) {
            if (privilege.getMonsterKilling()) {
                return;
            }
        } else {
            if (dom.getMonsterKilling()) {
                return;
            }
        }
        Notification.error(bukkitPlayer, "你没有击杀怪物的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // move
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getMove()) {
                return;
            }
        } else {
            if (dom.getMove()) {
                return;
            }
        }
        Notification.error(player, "你没有移动的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // place
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getPlace()) {
                return;
            }
        } else {
            if (dom.getPlace()) {
                return;
            }
        }
        Notification.error(player, "你没有放置方块的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // pressure
    public void onPressure(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Material clicked = event.getClickedBlock().getType();
        if (clicked != Material.STONE_PRESSURE_PLATE &&
                clicked != Material.LIGHT_WEIGHTED_PRESSURE_PLATE &&
                clicked != Material.HEAVY_WEIGHTED_PRESSURE_PLATE &&
                clicked != Material.OAK_PRESSURE_PLATE &&
                clicked != Material.SPRUCE_PRESSURE_PLATE &&
                clicked != Material.BIRCH_PRESSURE_PLATE &&
                clicked != Material.JUNGLE_PRESSURE_PLATE &&
                clicked != Material.ACACIA_PRESSURE_PLATE &&
                clicked != Material.DARK_OAK_PRESSURE_PLATE &&
                clicked != Material.CRIMSON_PRESSURE_PLATE &&
                clicked != Material.WARPED_PRESSURE_PLATE &&
                clicked != Material.POLISHED_BLACKSTONE_PRESSURE_PLATE &&
                clicked != Material.MANGROVE_PRESSURE_PLATE &&
                clicked != Material.CHERRY_PRESSURE_PLATE &&
                clicked != Material.BAMBOO_PRESSURE_PLATE) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getPressure()) {
                return;
            }
        } else {
            if (dom.getPressure()) {
                return;
            }
        }
        Notification.error(player, "你没有使用压力板的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // riding
    public void onRiding(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getRiding()) {
                return;
            }
        } else {
            if (dom.getRiding()) {
                return;
            }
        }
        Notification.error(player, "你没有骑乘交通工具的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // repeater
    public void onRepeaterChange(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Material clicked = event.getClickedBlock().getType();
        if (clicked != Material.REPEATER) {
            return;
        }
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getRepeater()) {
                return;
            }
        } else {
            if (dom.getRepeater()) {
                return;
            }
        }
        Notification.error(player, "你没有使用红石中继器的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // shear
    public void onShear(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getShear()) {
                return;
            }
        } else {
            if (dom.getShear()) {
                return;
            }
        }
        Notification.error(player, "你没有剪羊毛的权限");
        event.setCancelled(true);
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
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getShoot()) {
                return;
            }
        } else {
            if (dom.getShoot()) {
                return;
            }
        }
        Notification.error(player, "你没有发射弓箭、三叉戟或雪球的权限");
        event.setCancelled(true);
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
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(bukkitPlayer);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(bukkitPlayer, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(bukkitPlayer, dom);
        if (privilege != null) {
            if (privilege.getTrade()) {
                return;
            }
        } else {
            if (dom.getTrade()) {
                return;
            }
        }
        Notification.error(bukkitPlayer, "你没有交易的权限");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // vehicle_destroy
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getAttacker();
        DominionDTO dom = Cache.instance.getPlayerCurrentDominion(player);
        if (dom == null) {
            return;
        }
        if (Apis.hasPermission(player, dom)) {
            return;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            if (privilege.getVehicleDestroy()) {
                return;
            }
        } else {
            if (dom.getVehicleDestroy()) {
                return;
            }
        }
        Notification.error(player, "你没有破坏交通工具的权限");
        event.setCancelled(true);
    }
}
