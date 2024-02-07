package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static cn.lunadeer.dominion.controllers.Apis.noAuthToChangeFlags;

public class GroupController {

    public static List<PrivilegeTemplateDTO> all(Player owner) {
        return PrivilegeTemplateDTO.selectGroup(owner.getUniqueId());
    }

    /**
     * 创建权限组
     *
     * @param operator 操作者
     * @param name     权限组名称
     * @return 是否创建成功
     */
    public static PrivilegeTemplateDTO create(Player operator, String name) {
        PrivilegeTemplateDTO template = new PrivilegeTemplateDTO(name, operator.getUniqueId(), true);
        template = PrivilegeTemplateDTO.insert(template);
        if (template == null) {
            Notification.error(operator, "创建权限组失败");
            return null;
        }
        return template;
    }

    /**
     * 删除权限组
     *
     * @param operator 操作者
     * @param name     权限组名称
     * @return 是否删除成功
     */
    public static boolean delete(Player operator, String name) {
        PrivilegeTemplateDTO.delete(operator.getUniqueId(), name);
        return true;
    }

    /**
     * 设置权限组的权限
     *
     * @param operator 操作者
     * @param name     权限组名称
     * @param flag     权限名称
     * @param value    权限值
     * @return 是否设置成功
     */
    public static boolean setFlag(Player operator, String name, String flag, boolean value) {
        PrivilegeTemplateDTO privilege = PrivilegeTemplateDTO.select(operator.getUniqueId(), name);
        if (privilege == null) {
            Notification.error(operator, "没有找到权限组 " + name);
            return false;
        }
        if (!Apis.updateTemplateFlag(privilege, flag, value)) {
            Notification.error(operator, "未知的权限组权限 " + flag);
            return false;
        }
        Notification.info(operator, "设置权限组 " + name + " 权限 " + flag + " 为 " + value);
        return true;
    }

    /**
     * 添加玩家到权限组
     *
     * @param operator    操作者
     * @param player_name 玩家
     * @param groupName   权限组名称
     * @return 是否添加成功
     */
    public static boolean addPlayer(Player operator, String player_name, String groupName) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) return false;
        return addPlayer(operator, player_name, groupName, dominion.getName());
    }

    /**
     * 添加玩家到权限组
     *
     * @param operator     操作者
     * @param player_name  玩家
     * @param groupName    权限组名称
     * @param dominionName 领地名称
     * @return 是否添加成功
     */
    public static boolean addPlayer(Player operator, String player_name, String groupName, String dominionName) {
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), groupName);
        if (template == null) {
            Notification.error(operator, "没有找到权限组 " + groupName + " 或者需要先创建权限组");
            return false;
        }
        PlayerDTO player = PlayerDTO.select(player_name);
        if (player == null) {
            Notification.error(operator, "玩家 " + player_name + " 不存在或没有登录过");
            return false;
        }
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            Notification.error(operator, "领地 " + dominionName + " 不存在");
            return false;
        }
        if (noAuthToChangeFlags(operator, dominion)) return false;
        PlayerPrivilegeDTO privilege = new PlayerPrivilegeDTO(player.getUuid(), false, dominion.getId(), template.getId());
        privilege = PlayerPrivilegeDTO.insert(privilege);
        if (privilege == null) {
            Notification.error(operator, "设置玩家" + player.getLastKnownName() + "在领地 " + dominionName + " 归属权限组 " + groupName + " 失败");
            return false;
        }
        Notification.info(operator, "设置玩家" + player.getLastKnownName() + "在领地 " + dominionName + " 归属权限组 " + groupName);
        return true;
    }

    /**
     * 从权限组中移除玩家
     *
     * @param operator    操作者
     * @param player_name 玩家
     * @param groupName   权限组名称
     * @return 是否移除成功
     */
    public static boolean removePlayer(Player operator, String player_name, String groupName) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) return false;
        return removePlayer(operator, player_name, groupName, dominion.getName());
    }

    /**
     * 从权限组中移除玩家
     *
     * @param operator     操作者
     * @param player_name  玩家
     * @param groupName    权限组名称
     * @param dominionName 领地名称
     * @return 是否移除成功
     */
    public static boolean removePlayer(Player operator, String player_name, String groupName, String dominionName) {
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), groupName);
        if (template == null) {
            Notification.error(operator, "没有找到权限组 " + groupName);
            return false;
        }
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            Notification.error(operator, "领地 " + dominionName + " 不存在");
            return false;
        }
        if (noAuthToChangeFlags(operator, dominion)) return false;
        PlayerDTO player = PlayerDTO.select(player_name);
        if (player == null) {
            Notification.error(operator, "玩家 " + player_name + " 不存在或没有登录过");
            return false;
        }
        PlayerPrivilegeDTO.delete(player.getUuid(), dominion.getId(), template.getId());
        return true;
    }
}
