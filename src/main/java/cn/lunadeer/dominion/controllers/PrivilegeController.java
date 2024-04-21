package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.entity.Player;

import java.util.UUID;

import static cn.lunadeer.dominion.controllers.Apis.noAuthToChangeFlags;
import static cn.lunadeer.dominion.controllers.Apis.notOwner;

public class PrivilegeController {

    /**
     * 清空玩家特权
     *
     * @param operator    操作者
     * @param player_name 玩家
     * @return 是否清空成功
     */
    public static boolean clearPrivilege(Player operator, String player_name) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) {
            Notification.error(operator, "你不在任何领地内，请指定领地名称 /dominion clear_privilege <玩家名称> <领地名称>");
            return false;
        }
        return clearPrivilege(operator, player_name, dominion.getName());
    }

    /**
     * 清空玩家特权
     *
     * @param operator     操作者
     * @param player_name  玩家
     * @param dominionName 领地名称
     * @return 是否清空成功
     */
    public static boolean clearPrivilege(Player operator, String player_name, String dominionName) {
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            Notification.error(operator, "领地 " + dominionName + " 不存在");
            return false;
        }
        if (noAuthToChangeFlags(operator, dominion)) return false;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            Notification.error(operator, "玩家 " + player_name + " 不存在或没有登录过");
            return false;
        }
        PlayerPrivilegeDTO.delete(player.getUuid(), dominion.getId());
        return true;
    }

    /**
     * 设置玩家特权
     *
     * @param operator    操作者
     * @param player_name 玩家
     * @param flag        权限名称
     * @param value       权限值
     * @return 是否设置成功
     */
    public static boolean setPrivilege(Player operator, String player_name, String flag, boolean value) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) {
            Notification.error(operator, "你不在任何领地内，请指定领地名称 /dominion set_privilege <玩家名称> <权限名称> <true/false> [领地名称]");
            return false;
        }
        return setPrivilege(operator, player_name, flag, value, dominion.getName());
    }

    /**
     * 设置玩家特权
     *
     * @param operator     操作者
     * @param player_name  玩家
     * @param flag         权限名称
     * @param value        权限值
     * @param dominionName 领地名称
     * @return 是否设置成功
     */
    public static boolean setPrivilege(Player operator, String player_name, String flag, boolean value, String dominionName) {
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            Notification.error(operator, "领地 " + dominionName + " 不存在，无法设置特权");
            return false;
        }
        if (noAuthToChangeFlags(operator, dominion)) return false;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            Notification.error(operator, "玩家 " + player_name + " 不存在或没有登录过");
            return false;
        }
        PlayerPrivilegeDTO privilege = PlayerPrivilegeDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            privilege = createPlayerPrivilege(operator, player.getUuid(), dominion);
            if (privilege == null) return false;
        }
        switch (flag) {
            case "admin":
                if (notOwner(operator, dominion)) {
                    Notification.error(operator, "你不是领地 " + dominionName + " 的拥有者，无法设置其他玩家为管理员");
                    return false;
                }
                privilege.setAdmin(value);
                break;
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
            case "break":
                privilege.setBreak(value);
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
            case "comparer":
                privilege.setComparer(value);
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
            case "harvest":
                privilege.setHarvest(value);
                break;
            case "honey":
                privilege.setHoney(value);
                break;
            case "hook":
                privilege.setHook(value);
                break;
            case "hopper":
                privilege.setHopper(value);
                break;
            case "ignite":
                privilege.setIgnite(value);
                break;
            case "lever":
                privilege.setLever(value);
                break;
            case "monster_killing":
                privilege.setMonsterKilling(value);
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
            case "repeater":
                privilege.setRepeater(value);
                break;
            case "shear":
                privilege.setShear(value);
                break;
            case "shoot":
                privilege.setShoot(value);
                break;
            case "teleport":
                privilege.setTeleport(value);
                break;
            case "trade":
                privilege.setTrade(value);
                break;
            case "vehicle_destroy":
                privilege.setVehicleDestroy(value);
                break;
            case "vehicle_spawn":
                privilege.setVehicleSpawn(value);
                break;
            default:
                Notification.error(operator, "未知的领地权限 " + flag);
                return false;
        }
        Notification.info(operator, "设置玩家在领地 " + dominionName + " 的权限 " + flag + " 为 " + value);
        return true;
    }

    public static boolean createPrivilege(Player operator, String player_name) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) {
            Notification.error(operator, "你不在任何领地内，请指定领地名称 /dominion create_privilege <玩家名称> <领地名称>");
            return false;
        }
        return createPrivilege(operator, player_name, dominion.getName());
    }

    public static boolean createPrivilege(Player operator, String player_name, String dominionName) {
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            Notification.error(operator, "领地 " + dominionName + " 不存在，无法创建特权");
            return false;
        }
        if (noAuthToChangeFlags(operator, dominion)) return false;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            Notification.error(operator, "玩家 " + player_name + " 不存在或没有登录过");
            return false;
        }
        return createPlayerPrivilege(operator, player.getUuid(), dominion) != null;
    }

    private static PlayerPrivilegeDTO createPlayerPrivilege(Player operator, UUID player, DominionDTO dom) {
        if (operator.getUniqueId() == player) {
            Notification.error(operator, "你不能给自己设置特权");
            return null;
        }
        PlayerPrivilegeDTO privilege = new PlayerPrivilegeDTO(player, dom.getId(),
                dom.getAnchor(), dom.getAnimalKilling(), dom.getAnvil(),
                dom.getBeacon(), dom.getBed(), dom.getBrew(), dom.getBreak(), dom.getButton(),
                dom.getCake(), dom.getContainer(), dom.getCraft(), dom.getComparer(),
                dom.getDoor(), dom.getDye(),
                dom.getEgg(), dom.getEnchant(), dom.getEnderPearl(),
                dom.getFeed(),
                dom.getGlow(),
                dom.getHarvest(), dom.getHoney(), dom.getHook(), dom.getHopper(),
                dom.getIgnite(),
                dom.getLever(),
                dom.getMonsterKilling(), dom.getMove(),
                dom.getPlace(), dom.getPressure(),
                dom.getRiding(), dom.getRepeater(),
                dom.getShear(), dom.getShoot(),
                dom.getTeleport(), dom.getTrade(),
                dom.getVehicleDestroy(),
                dom.getVehicleSpawn());
        privilege = PlayerPrivilegeDTO.insert(privilege);
        if (privilege == null) {
            Notification.error(operator, "创建玩家特权失败，可能是此玩家已存在特权");
            return null;
        }
        return privilege;
    }

}
