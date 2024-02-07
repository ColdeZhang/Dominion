package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
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
            List<PlayerPrivilegeDTO> privileges = PlayerPrivilegeDTO.select(player.getUniqueId(), dominion.getId());
            for (PlayerPrivilegeDTO privilege : privileges) {
                if (privilege.getAdmin()) return false;
            }
            Notification.error(player, "你不是领地 " + dominion.getName() + " 的拥有者或管理员，无法执行此操作");
            return true;
        } else {
            return false;
        }
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


    public static boolean updateTemplateFlag(PrivilegeTemplateDTO privilege, String flag, boolean value) {
        switch (flag) {
            case "anchor":
                privilege.setAnchor(value);
                break;
            case "animal_killing":
                privilege.setAnimalKilling(value);
                break;
            case "anvil":
                privilege.setAnvil(value);
                break;
            case "beacon":
                privilege.setBeacon(value);
                break;
            case "bed":
                privilege.setBed(value);
                break;
            case "brew":
                privilege.setBrew(value);
                break;
            case "button":
                privilege.setButton(value);
                break;
            case "cake":
                privilege.setCake(value);
                break;
            case "container":
                privilege.setContainer(value);
                break;
            case "craft":
                privilege.setCraft(value);
                break;
            case "diode":
                privilege.setDiode(value);
                break;
            case "door":
                privilege.setDoor(value);
                break;
            case "dye":
                privilege.setDye(value);
                break;
            case "egg":
                privilege.setEgg(value);
                break;
            case "enchant":
                privilege.setEnchant(value);
                break;
            case "ender_pearl":
                privilege.setEnderPearl(value);
                break;
            case "feed":
                privilege.setFeed(value);
                break;
            case "glow":
                privilege.setGlow(value);
                break;
            case "honey":
                privilege.setHoney(value);
                break;
            case "hook":
                privilege.setHook(value);
                break;
            case "ignite":
                privilege.setIgnite(value);
                break;
            case "mob_killing":
                privilege.setMobKilling(value);
                break;
            case "move":
                privilege.setMove(value);
                break;
            case "place":
                privilege.setPlace(value);
                break;
            case "pressure":
                privilege.setPressure(value);
                break;
            case "riding":
                privilege.setRiding(value);
                break;
            case "shear":
                privilege.setShear(value);
                break;
            case "shoot":
                privilege.setShoot(value);
                break;
            case "trade":
                privilege.setTrade(value);
                break;
            case "vehicle_destroy":
                privilege.setVehicleDestroy(value);
                break;
            case "harvest":
                privilege.setHarvest(value);
                break;
            default:
                return false;
        }
        return true;
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
