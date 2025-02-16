package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.lunadeer.dominion.misc.Converts.toIntegrity;
import static cn.lunadeer.dominion.misc.Others.checkPrivilegeFlag;
import static cn.lunadeer.dominion.utils.Misc.isPaper;
import static cn.lunadeer.dominion.utils.SafeLocationFinder.findNearestSafeLocation;

public class TeleportManager implements Listener {

    public TeleportManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static Map<UUID, Integer> teleportingPlayers = new HashMap<>();


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!teleportingPlayers.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        Integer dominionId = teleportingPlayers.get(event.getPlayer().getUniqueId());
        teleportingPlayers.remove(event.getPlayer().getUniqueId());
        DominionDTO dominion = Cache.instance.getDominion(dominionId);
        teleportToDominion(event.getPlayer(), dominion);
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
        if (!checkPrivilegeFlag(dominion, Flags.TELEPORT, player, null)) {
            return;
        }
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
    }

    public static void handleTeleport(String playerUuid, String dominionId) {
        try {
            UUID uuid = UUID.fromString(playerUuid);
            Integer id = toIntegrity(dominionId);
            teleportingPlayers.put(uuid, id);
        } catch (Exception e) {
            XLogger.error(e.getMessage());
        }
    }

    public static void doTeleportSafely(Player player, Location location) {
        if (!player.getPassengers().isEmpty()) {
            player.getPassengers().forEach(player::removePassenger);
        }
        if (!isPaper()) {
            Location loc = findNearestSafeLocation(location);
            player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        } else {
            location.getWorld().getChunkAtAsyncUrgently(location).thenAccept((chunk) -> {
                Location loc = findNearestSafeLocation(location);
                player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            });
        }
    }

}
