package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

public class Apis {

    public static boolean notOwner(Player player, DominionDTO dominion) {
        if (player.isOp()) return false;
        if (dominion.getOwner().equals(player.getUniqueId())) return false;
        Notification.error(player, "你不是领地 " + dominion.getName() + " 的拥有者，无法执行此操作");
        return true;
    }

    public static boolean noAuthToChangeFlags(Player player, DominionDTO dominion) {
        if (player.isOp()) return false;
        if (!dominion.getOwner().equals(player.getUniqueId())) {
            PlayerPrivilegeDTO privileges = PlayerPrivilegeDTO.select(player.getUniqueId(), dominion.getId());
            if (privileges == null || !privileges.getAdmin()) {
                Notification.error(player, "你不是领地 " + dominion.getName() + " 的拥有者或管理员，无权修改权限");
                return true;
            }
        }
        return false;
    }

    /**
     * 获取玩家当前所在的领地
     * 如果玩家不在一个领地内或者在子领地内，会提示玩家手动指定要操作的领地名称
     *
     * @param player 玩家
     * @return 当前所在的领地
     */
    public static DominionDTO getPlayerCurrentDominion(Player player, boolean show_warning) {
        Location location = player.getLocation();
        List<DominionDTO> dominions = DominionDTO.selectByLocation(location.getWorld().getName(),
                (int) location.getX(), (int) location.getY(), (int) location.getZ());
        if (dominions.size() != 1) {
            if (show_warning) {
                Notification.error(player, "你不在一个领地内或在子领地内，无法确定你要操作的领地，请手动指定要操作的领地名称");
            }
            return null;
        }
        return dominions.get(0);
    }

    public static DominionDTO getPlayerCurrentDominion(Player player) {
        return getPlayerCurrentDominion(player, true);
    }

    public static BlockFace getFace(Player player) {
        float yaw = player.getYaw();
        float pitch = player.getPitch();
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
            return BlockFace.UP;
        } else {
            return BlockFace.DOWN;
        }
    }
}
