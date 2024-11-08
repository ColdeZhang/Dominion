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

    private void show(ResultType type) {
        if (getHeader().containsKey(type)) {
            if (getPlayer() != null) {
                switch (type) {
                    case FAILURE:
                        Notification.error(getPlayer(), getHeader().get(type));
                        break;
                    case WARNING:
                        Notification.warn(getPlayer(), getHeader().get(type));
                        break;
                    default:
                        Notification.info(getPlayer(), getHeader().get(type));
                }
            } else {
                switch (type) {
                    case FAILURE:
                        XLogger.err(getHeader().get(type));
                        break;
                    case WARNING:
                        XLogger.warn(getHeader().get(type));
                        break;
                    default:
                        XLogger.info(getHeader().get(type));
                }
            }
        }
        for (String message : getResults().get(type)) {
            if (getPlayer() != null) {
                switch (type) {
                    case FAILURE:
                        Notification.error(getPlayer(), message);
                        break;
                    case WARNING:
                        Notification.warn(getPlayer(), message);
                        break;
                    default:
                        Notification.info(getPlayer(), message);
                }
            } else {
                switch (type) {
                    case FAILURE:
                        XLogger.err(message);
                        break;
                    case WARNING:
                        XLogger.warn(message);
                        break;
                    default:
                        XLogger.info(message);
                }
            }
        }
    }

    @Override
    public void completeResult() {
        if (!getResults().get(ResultType.WARNING).isEmpty()) {
            show(ResultType.WARNING);
        }
        if (!getResults().get(ResultType.FAILURE).isEmpty()) {
            show(ResultType.FAILURE);
        } else {
            show(ResultType.SUCCESS);
        }
    }
}
