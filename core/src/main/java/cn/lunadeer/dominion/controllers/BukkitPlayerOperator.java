package cn.lunadeer.dominion.controllers;

import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BukkitPlayerOperator implements AbstractOperator {

    private final CommandSender player;
    private final CompletableFuture<Result> response = new CompletableFuture<>();

    public BukkitPlayerOperator(CommandSender player) {
        this.player = player;
    }

    public boolean isConsole() {
        return !(player instanceof Player);
    }

    @Override
    public UUID getUniqueId() {
        if (isConsole()) {
            return UUID.randomUUID();
        } else {
            return ((Player) player).getUniqueId();
        }
    }

    @Override
    public boolean isOp() {
        if (isConsole()) {
            return true;
        } else {
            return ((Player) player).isOp() || player.hasPermission("dominion.admin");
        }
    }

    @Override
    public void setResponse(Result result) {
        response.complete(result);
    }

    @Override
    public @Nullable Location getLocation() {
        if (isConsole()) {
            return null;
        } else {
            return ((Player) player).getLocation();
        }
    }

    @Override
    public @Nullable Player getPlayer() {
        if (isConsole()) {
            return null;
        } else {
            return (Player) player;
        }
    }

    @Override
    public @Nullable BlockFace getDirection() {
        if (isConsole() || getLocation() == null) {
            return null;
        }
        float yaw = getLocation().getYaw();
        float pitch = getLocation().getPitch();
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

    public static BukkitPlayerOperator create(CommandSender player) {
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
