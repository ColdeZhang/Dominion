package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class BukkitPlayerOperator extends AbstractOperator {

    private final CommandSender player;


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

    public static BukkitPlayerOperator create(CommandSender player) {
        return new BukkitPlayerOperator(player);
    }

    @Override
    public void completeResult() {
        for (String message : getResults().get(ResultType.SUCCESS)) {
            if (getPlayer() != null) {
                Notification.info(getPlayer(), message);
            } else {
                XLogger.info(message);
            }
        }
        for (String message : getResults().get(ResultType.WARNING)) {
            if (getPlayer() != null) {
                Notification.warn(getPlayer(), message);
            } else {
                XLogger.warn(message);
            }
        }
        for (String message : getResults().get(ResultType.FAILURE)) {
            if (getPlayer() != null) {
                Notification.error(getPlayer(), message);
            } else {
                XLogger.err(message);
            }
        }
    }
}
