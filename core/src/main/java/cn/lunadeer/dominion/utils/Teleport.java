package cn.lunadeer.dominion.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.CompletableFuture;

import static cn.lunadeer.dominion.utils.Misc.isPaper;


public class Teleport {

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
