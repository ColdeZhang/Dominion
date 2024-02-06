package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.entity.Player;

import java.util.UUID;

import static cn.lunadeer.dominion.controllers.Apis.noAuthToChangeFlags;

public class GroupController {

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
            Notification.error(operator, "未知的领地权限 " + flag);
            return false;
        }
        return true;
    }

    /**
     * 添加玩家到权限组
     *
     * @param operator  操作者
     * @param player    玩家
     * @param groupName 权限组名称
     * @return 是否添加成功
     */
    public static boolean addPlayer(Player operator, UUID player, String groupName) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) return false;
        return addPlayer(operator, player, groupName, dominion.getName());
    }

    /**
     * 添加玩家到权限组
     *
     * @param operator     操作者
     * @param player       玩家
     * @param groupName    权限组名称
     * @param dominionName 领地名称
     * @return 是否添加成功
     */
    public static boolean addPlayer(Player operator, UUID player, String groupName, String dominionName) {
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), groupName);
        if (template == null) {
            Notification.error(operator, "没有找到权限组 " + groupName + " 或者需要先创建权限组");
            return false;
        }
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            Notification.error(operator, "领地 " + dominionName + " 不存在");
            return false;
        }
        if (noAuthToChangeFlags(operator, dominion)) return false;
        PlayerPrivilegeDTO privilege = new PlayerPrivilegeDTO(player, false, dominion.getId(), template.getId());
        privilege = PlayerPrivilegeDTO.insert(privilege);
        if (privilege == null) {
            Notification.error(operator, "添加玩家 " + player + " 到权限组 " + groupName + " 失败");
            return false;
        }
        return true;
    }

    /**
     * 从权限组中移除玩家
     *
     * @param operator  操作者
     * @param player    玩家
     * @param groupName 权限组名称
     * @return 是否移除成功
     */
    public static boolean removePlayer(Player operator, UUID player, String groupName) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) return false;
        return removePlayer(operator, player, groupName, dominion.getName());
    }

    /**
     * 从权限组中移除玩家
     *
     * @param operator     操作者
     * @param player       玩家
     * @param groupName    权限组名称
     * @param dominionName 领地名称
     * @return 是否移除成功
     */
    public static boolean removePlayer(Player operator, UUID player, String groupName, String dominionName) {
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
        PlayerPrivilegeDTO.delete(player, dominion.getId(), template.getId());
        return true;
    }
}
