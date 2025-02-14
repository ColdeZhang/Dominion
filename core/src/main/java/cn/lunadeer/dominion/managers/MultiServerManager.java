package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.utils.Notification;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static cn.lunadeer.dominion.utils.Misc.isPaper;

// https://docs.papermc.io/paper/dev/plugin-messaging#forward
public class MultiServerManager implements PluginMessageListener, Listener {

    public static MultiServerManager instance;

    private final JavaPlugin plugin;
    private final ServerInfoDTO thisServerInfo;
    private final Map<Integer, String> allServerInfo;

    public MultiServerManager(JavaPlugin plugin) {
        this.plugin = plugin;
        File infoFile = new File(plugin.getDataFolder(), "server_info.json");
        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, "BungeeCord");
        this.plugin.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, "BungeeCord", this);
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        instance = this;

        if (!infoFile.exists()) {
            thisServerInfo = ServerInfoDTO.initServerInfo(plugin, infoFile);
        } else {
            thisServerInfo = ServerInfoDTO.updateServerInfo(plugin, infoFile);
        }
        allServerInfo = ServerInfoDTO.getAllServerInfo();
    }

    public String getThisServerName() {
        return thisServerInfo.getName();
    }

    public int getThisServerId() {
        return thisServerInfo.getId();
    }

    public String getServerName(int id) {
        return allServerInfo.get(id);
    }

    public Map<Integer, String> getAllServerInfo() {
        return allServerInfo;
    }

    /**
     * A method that will be thrown when a PluginMessageSource sends a plugin
     * message on a registered channel.
     *
     * @param channel Channel that the message was sent through.
     * @param player  Source of the message.
     * @param message The raw message that was sent.
     */
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
    }

    private void teleportToServer(Player player, DominionDTO dominionDTO) {
        Field player_uuid = new Field("player_uuid", player.getUniqueId().toString());
        Field dom_id = new Field("dom_id", dominionDTO.getId());
        InsertRow addCache = new InsertRow();
        addCache.field(player_uuid)
                .field(dom_id)
                .onConflictOverwrite(player_uuid)
                .table("bc_tp_cache");
        addCache.execute();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(dominionDTO.getServerName());
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String sql = "SELECT dom_id FROM bc_tp_cache WHERE player_uuid = ?";
        int dominionId;
        try (ResultSet res = DatabaseManager.instance.query(sql, player.getUniqueId().toString())) {
            if (res.next()) {
                dominionId = res.getInt("dom_id");
            } else {
                XLogger.debug("玩家 %s 没有传送缓存", player.getName());
                return;
            }
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("获取玩家的传送缓存失败", e, sql);
            return;
        }
        DominionDTO dominionDTO = DominionDTO.select(dominionId);
        if (dominionDTO == null) {
            Notification.error(player, "无法获取目标领地信息");
        } else {
            if (dominionDTO.getServerId() == getThisServerId()) {
                doTp(player, dominionDTO);
            }
        }
        sql = "DELETE FROM bc_tp_cache WHERE player_uuid = ?";
        DatabaseManager.instance.query(sql, player.getUniqueId().toString());
    }

    /**
     * 安全传送玩家到指定位置
     *
     * @param player   玩家
     * @param location 位置
     * @return 是否成功 (true: 成功, false: 失败)
     * <p>
     * 如果需要处理传送失败的情况，可以使用 CompletableFuture 的 thenAccept 方法
     * 例如:
     * Teleport.doTeleportSafely(player, location).thenAccept((success) -> {
     * if (!success) {
     * // 传送失败的处理
     * }
     * });
     */
    public static CompletableFuture<Boolean> doTeleportSafely(Player player, Location location) {
        if (!player.getPassengers().isEmpty()) {
            player.getPassengers().forEach(player::removePassenger);
        }
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (!isPaper()) {
            Location loc = getSafeTeleportLocation(location);
            if (loc == null) {
                Notification.error(player, Localization.Utils_TeleportUnsafe);
                future.complete(false);
                return future;
            }
            player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        } else {
            location.getWorld().getChunkAtAsyncUrgently(location).thenAccept((chunk) -> {
                Location loc = getSafeTeleportLocation(location);
                if (loc == null) {
                    Notification.error(player, Localization.Utils_TeleportUnsafe);
                    future.complete(false);
                    return;
                }
                player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                future.complete(true);
            });
        }
        return future;
    }

    public static Location getSafeTeleportLocation(Location location) {
        int max_attempts = 512;
        while (location.getBlock().isPassable()) {
            location.setY(location.getY() - 1);
            max_attempts--;
            if (max_attempts <= 0) {
                return null;
            }
        }
        Block up1 = location.getBlock().getRelative(BlockFace.UP);
        Block up2 = up1.getRelative(BlockFace.UP);
        max_attempts = 512;
        while (!(up1.isPassable() && !up1.isLiquid()) || !(up2.isPassable() && !up2.isLiquid())) {
            location.setY(location.getY() + 1);
            up1 = location.getBlock().getRelative(BlockFace.UP);
            up2 = up1.getRelative(BlockFace.UP);
            max_attempts--;
            if (max_attempts <= 0) {
                return null;
            }
        }
        location.setY(location.getY() + 1);
        if (location.getBlock().getRelative(BlockFace.DOWN).getType() == Material.LAVA) {
            return null;
        }
        return location;
    }
}
