package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.scheduler.CancellableTask;
import cn.lunadeer.dominion.utils.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.lunadeer.dominion.Dominion.adminPermission;
import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;
import static cn.lunadeer.dominion.utils.Misc.isPaper;
import static cn.lunadeer.dominion.utils.SafeLocationFinder.findNearestSafeLocation;

public class TeleportManager implements Listener {

    public static class TeleportManagerText extends ConfigurationPart {
        public String coolingDown = "Please wait for {0} seconds before teleporting again.";
        public String disabled = "Teleportation is disabled for your permission group.";
        public String delay = "Will teleport in {0} seconds, don't move...";
        public String unfinishedCancelled = "Cancelled previous unfinished teleportation.";
        public String cancelMove = "Cancelled teleportation due to movement.";
    }

    public TeleportManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static Map<UUID, Integer> teleportRequestOtherServer = new HashMap<>();
    public static Map<UUID, Integer> teleportCooldown = new HashMap<>();
    public static Map<UUID, CancellableTask> teleportDelayTasks = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!teleportRequestOtherServer.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        Integer dominionId = teleportRequestOtherServer.get(event.getPlayer().getUniqueId());
        teleportRequestOtherServer.remove(event.getPlayer().getUniqueId());
        DominionDTO dominion = Cache.instance.getDominion(dominionId);
        if (dominion == null) {
            return;
        }
        if (dominion.getServerId() != Configuration.multiServer.serverId) {
            return;
        }
        doTeleportSafely(event.getPlayer(), dominion.getTpLocation());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!teleportDelayTasks.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        teleportDelayTasks.get(event.getPlayer().getUniqueId()).cancel();
        teleportDelayTasks.remove(event.getPlayer().getUniqueId());
        Notification.warn(event.getPlayer(), Language.teleportManagerText.cancelMove);
    }

    /**
     * Teleports a player to a specified dominion.
     * <p>
     * This method checks if the player has the privilege to teleport to the dominion.
     * If the dominion is on the same server, it teleports the player safely to the dominion's location.
     * If the dominion is on a different server, it sends a teleport action message to the target server
     * and connects the player to that server.
     *
     * @param player   The player to be teleported.
     * @param dominion The dominion to which the player will be teleported.
     */
    public static void teleportToDominion(Player player, DominionDTO dominion) {
        // check privilege
        if (!Configuration.getPlayerLimitation(player).teleportation.enable) {
            Notification.warn(player, Language.teleportManagerText.disabled);
            return;
        }
        if (!checkPrivilegeFlag(dominion, Flags.TELEPORT, player, null)) {
            return;
        }
        // cooldown
        boolean needCooldown = Configuration.getPlayerLimitation(player).teleportation.cooldown > 0;
        if (player.hasPermission(adminPermission) && Configuration.adminBypass) {
            needCooldown = false;
        }
        if (needCooldown) {
            int currentTs = (int) (System.currentTimeMillis() / 1000);
            if (teleportCooldown.containsKey(player.getUniqueId())) {
                if (teleportCooldown.get(player.getUniqueId()) > currentTs) {
                    Notification.warn(player, Language.teleportManagerText.coolingDown, teleportCooldown.get(player.getUniqueId()) - currentTs);
                    return;
                }
            }
            teleportCooldown.put(player.getUniqueId(), currentTs + Configuration.getPlayerLimitation(player).teleportation.cooldown);
        }
        // delay
        if (teleportDelayTasks.containsKey(player.getUniqueId())) {
            teleportDelayTasks.get(player.getUniqueId()).cancel();
            teleportDelayTasks.remove(player.getUniqueId());
            Notification.warn(player, Language.teleportManagerText.unfinishedCancelled);
        }
        if (Configuration.getPlayerLimitation(player).teleportation.cooldown > 0) {
            Notification.info(player, Language.teleportManagerText.delay, Configuration.getPlayerLimitation(player).teleportation.delay);
        }
        // teleport
        CancellableTask task = Scheduler.runTaskLaterAsync(() -> {
            if (dominion.getServerId() == Configuration.multiServer.serverId) {
                doTeleportSafely(player, dominion.getTpLocation());
            } else {
                if (!Configuration.multiServer.enable) return;
                try {
                    MultiServerManager.instance.sendActionMessage(dominion.getServerId(), "teleport",
                            List.of(
                                    player.getUniqueId().toString(),
                                    dominion.getId().toString()
                            )
                    );
                    MultiServerManager.instance.connectToServer(player, MultiServerManager.instance.getServerName(dominion.getServerId()));
                } catch (Exception e) {
                    Notification.error(player, e.getMessage());
                }
            }
        }, Configuration.getPlayerLimitation(player).teleportation.delay * 20L);
        Scheduler.runTaskLaterAsync(() -> {
            teleportDelayTasks.remove(player.getUniqueId());    // remove task from map for cleanup
        }, Configuration.getPlayerLimitation(player).teleportation.delay * 20L + 1);
        teleportDelayTasks.put(player.getUniqueId(), task);
    }

    /**
     * Handles the teleport BungeeCord message.
     * <p>
     * This method processes a teleport BungeeCord message received from another server.
     * It stores the player UUID and dominion ID in the teleportingPlayers map.
     * The player will be teleported to the dominion when they join the server.
     *
     * @param playerUuid The UUID of the player as a string.
     * @param dominionId The ID of the dominion as a string.
     */
    public static void handleTeleportBcMsg(String playerUuid, String dominionId) {
        try {
            UUID uuid = UUID.fromString(playerUuid);
            Integer id = toIntegrity(dominionId);
            teleportRequestOtherServer.put(uuid, id);
        } catch (Exception e) {
            XLogger.error(e);
        }
    }

    /**
     * Teleports a player to a specified location safely.
     * <p>
     * This method ensures that the player is teleported to a safe location. If the player has passengers,
     * they are removed before teleportation. The method handles both synchronous and asynchronous teleportation
     * based on the server type (Paper or non-Paper).
     *
     * @param player   The player to be teleported.
     * @param location The target location to which the player will be teleported.
     */
    public static void doTeleportSafely(Player player, Location location) {
        if (!player.getPassengers().isEmpty()) {
            player.getPassengers().forEach(player::removePassenger);
        }
        if (!isPaper()) {
            Location loc = findNearestSafeLocation(location);
            Bukkit.getScheduler().runTask(Dominion.instance, () -> player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN));
        } else {
            location.getWorld().getChunkAtAsyncUrgently(location).thenAccept((chunk) -> {
                Location loc = findNearestSafeLocation(location);
                player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            });
        }
    }

}
