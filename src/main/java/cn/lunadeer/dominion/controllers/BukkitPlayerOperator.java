package cn.lunadeer.dominion.controllers;

import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BukkitPlayerOperator implements AbstractOperator {

    private final org.bukkit.entity.Player player;
    private final CompletableFuture<Result> response = new CompletableFuture<>();

    public BukkitPlayerOperator(org.bukkit.entity.Player player) {
        this.player = player;
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public boolean isOp() {
        return player.isOp();
    }

    @Override
    public void setResponse(Result result) {
        response.complete(result);
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public BlockFace getDirection() {
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        if (pitch > -45 && pitch < 45) {
            if (yaw > -45 && yaw < 45) {
                return BlockFace.SOUTH;
            } else if (yaw > 135 || yaw < -135) {
                return BlockFace.NORTH;
            } else if (yaw > 45 && yaw < 135) {
                return BlockFace.WEST;
            } else {
                return BlockFace.EAST;
            }
        } else if (pitch > 45) {
            return BlockFace.DOWN;
        } else {
            return BlockFace.UP;
        }
    }

    @Override
    public CompletableFuture<Result> getResponse() {
        return response;
    }

    public static BukkitPlayerOperator create(org.bukkit.entity.Player player) {
        BukkitPlayerOperator operator = new BukkitPlayerOperator(player);
        operator.getResponse().thenAccept(result -> {
            if (Objects.equals(result.getStatus(), BukkitPlayerOperator.Result.SUCCESS)) {
                for (String msg : result.getMessages()) {
                    Notification.info(player, msg);
                }
            } else if (Objects.equals(result.getStatus(), BukkitPlayerOperator.Result.WARNING)) {
                for (String msg : result.getMessages()) {
                    Notification.warn(player, msg);
                }
            } else {
                for (String msg : result.getMessages()) {
                    Notification.error(player, msg);
                }
            }
        });
        return operator;
    }
}
