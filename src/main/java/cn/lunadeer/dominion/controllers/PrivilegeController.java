package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.dtos.PrivilegeTemplateDTO;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static cn.lunadeer.dominion.controllers.Apis.noAuthToChangeFlags;

public class PrivilegeController {

    /**
     * 清空玩家特权
     *
     * @param operator 操作者
     * @param player   玩家
     * @return 是否清空成功
     */
    public static boolean clearPrivilege(Player operator, UUID player) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) return false;
        return clearPrivilege(operator, player, dominion.getName());
    }

    /**
     * 清空玩家特权
     *
     * @param operator     操作者
     * @param player       玩家
     * @param dominionName 领地名称
     * @return 是否清空成功
     */
    public static boolean clearPrivilege(Player operator, UUID player, String dominionName) {
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            Notification.error(operator, "领地 " + dominionName + " 不存在");
            return false;
        }
        if (noAuthToChangeFlags(operator, dominion)) return false;
        List<PlayerPrivilegeDTO> privileges = PlayerPrivilegeDTO.select(player, dominion.getId());
        List<PrivilegeTemplateDTO> templates = getPlayerPrivilegeTemplates(player, dominion.getId());
        if (templates.size() < 1) {
            return true;
        }
        for (PrivilegeTemplateDTO template : templates) {
            PlayerPrivilegeDTO.delete(player, dominion.getId(), template.getId());
        }
        return true;
    }

    /**
     * 设置玩家特权
     *
     * @param operator 操作者
     * @param player   玩家
     * @param flag     权限名称
     * @param value    权限值
     * @return 是否设置成功
     */
    public static boolean setPrivilege(Player operator, UUID player, String flag, boolean value) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) return false;
        return setPrivilege(operator, player, flag, value, dominion.getName());
    }

    /**
     * 设置玩家特权
     *
     * @param operator     操作者
     * @param player       玩家
     * @param flag         权限名称
     * @param value        权限值
     * @param dominionName 领地名称
     * @return 是否设置成功
     */
    public static boolean setPrivilege(Player operator, UUID player, String flag, boolean value, String dominionName) {
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            Notification.error(operator, "领地 " + dominionName + " 不存在");
            return false;
        }
        if (noAuthToChangeFlags(operator, dominion)) return false;
        List<PrivilegeTemplateDTO> templates = getPlayerPrivilegeTemplates(player, dominion.getId());
        if (templates.size() < 1) {
            PrivilegeTemplateDTO template = createPlayerPrivilege(operator, player, dominion.getId());
            if (template == null) return false;
            templates.add(template);
        }
        if (templates.size() > 1) {
            Notification.error(operator, "玩家特权拥有多个权限模板，建议使用 clear_privilege 清空此玩家特权后重新设置");
            XLogger.warn("玩家特权拥有多个权限模板，使用搜索到的第一个 id: " + templates.get(0).getId() + " 进行操作，请检查数据库");
            XLogger.warn("其他权限模板如下：");
            for (int i = 1; i < templates.size(); i++) {
                XLogger.warn("    id: " + templates.get(i).getId());
            }
        }
        PrivilegeTemplateDTO privilege = templates.get(0);
        if (Objects.equals(flag, "admin")) {
            List<PlayerPrivilegeDTO> privileges = PlayerPrivilegeDTO.select(player, dominion.getId());
            for (PlayerPrivilegeDTO p : privileges) {
                if (p.getPrivilegeTemplateID().equals(privilege.getId())) {
                    p = p.setAdmin(value);
                    if (p == null) {
                        Notification.error(operator, "更新玩家特权失败");
                        return false;
                    }
                    return true;
                }
            }
            Notification.error(operator, "没有找到玩家权限关联数据");
            return false;
        }
        if (!Apis.updateTemplateFlag(privilege, flag, value)){
            Notification.error(operator, "未知的领地权限 " + flag);
            return false;
        }
        return true;
    }

    private static PrivilegeTemplateDTO createPlayerPrivilege(Player operator, UUID player, Integer domID) {
        PrivilegeTemplateDTO template = new PrivilegeTemplateDTO(player.toString(), operator.getUniqueId(), false);
        template = PrivilegeTemplateDTO.insert(template);
        if (template == null) {
            Notification.error(operator, "创建玩家特权失败");
            return null;
        }
        PlayerPrivilegeDTO privilege = new PlayerPrivilegeDTO(player, false, domID, template.getId());
        privilege = PlayerPrivilegeDTO.insert(privilege);
        if (privilege == null) {
            Notification.error(operator, "创建玩家特权关联玩家时失败");
            PrivilegeTemplateDTO.delete(template);
            return null;
        }
        return template;
    }

    private static List<PrivilegeTemplateDTO> getPlayerPrivilegeTemplates(UUID player, Integer domID) {
        List<PlayerPrivilegeDTO> privileges = PlayerPrivilegeDTO.select(player, domID);
        List<PrivilegeTemplateDTO> templates = new ArrayList<>();
        for (PlayerPrivilegeDTO privilege : privileges) {
            PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(privilege.getPrivilegeTemplateID());
            if (template == null) continue;
            if (template.getGroup()) continue;  // 跳过组权限
            templates.add(template);
        }
        return templates;
    }
}
