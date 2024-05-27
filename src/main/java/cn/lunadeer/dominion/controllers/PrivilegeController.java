package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
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
            Notification.error(operator, "领地 %s 不存在", dominionName);
            return false;
        }
        if (noAuthToChangeFlags(operator, dominion)) return false;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            Notification.error(operator, "玩家 %s 不存在或没有登录过", player_name);
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
            Notification.error(operator, "领地 %s 不存在，无法设置特权", dominionName);
            return false;
        }
        if (noAuthToChangeFlags(operator, dominion)) return false;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            Notification.error(operator, "玩家 %s 不存在或没有登录过", player_name);
            return false;
        }
        PlayerPrivilegeDTO privilege = PlayerPrivilegeDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            privilege = createPlayerPrivilege(operator, player.getUuid(), dominion);
            if (privilege == null) return false;
        }
        if (flag.equals("admin")) {
            if (notOwner(operator, dominion)) {
                Notification.error(operator, "你不是领地 %s 的拥有者，无法设置其他玩家为管理员", dominionName);
                return false;
            }
            privilege.setAdmin(value);
        } else {
            Flag f = Flag.getFlag(flag);
            if (f == null) {
                Notification.error(operator, "未知的领地权限 %s", flag);
                return false;
            }
            privilege.setFlagValue(f, value);
        }
        Notification.info(operator, "设置玩家在领地 %s 的权限 %s 为 %s", dominionName, flag, value);
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
            Notification.error(operator, "领地 %s 不存在，无法创建特权", dominionName);
            return false;
        }
        if (noAuthToChangeFlags(operator, dominion)) return false;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            Notification.error(operator, "玩家 %s 不存在或没有登录过", player_name);
            return false;
        }
        return createPlayerPrivilege(operator, player.getUuid(), dominion) != null;
    }

    private static PlayerPrivilegeDTO createPlayerPrivilege(Player operator, UUID player, DominionDTO dom) {
        XLogger.debug("operator: " + operator.getUniqueId() + " player: " + player);
        if (operator.getUniqueId().equals(player)) {
            Notification.error(operator, "你不能给自己设置特权");
            return null;
        }
        PlayerPrivilegeDTO privilege = new PlayerPrivilegeDTO(player, dom);
        privilege = PlayerPrivilegeDTO.insert(privilege);
        if (privilege == null) {
            Notification.error(operator, "创建玩家特权失败，可能是此玩家已存在特权");
            return null;
        }
        return privilege;
    }

}
