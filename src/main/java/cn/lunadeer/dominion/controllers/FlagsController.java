package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.controllers.Apis.noAuthToChangeFlags;

public class FlagsController {

    /**
     * 设置领地权限
     *
     * @param operator 操作者
     * @param flag     权限名称
     * @param value    权限值
     * @return 设置后的领地信息
     */
    public static DominionDTO setFlag(Player operator, String flag, boolean value) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) return null;
        return setFlag(operator, flag, value, dominion.getName());
    }

    /**
     * 设置领地权限
     *
     * @param operator     操作者
     * @param flag         权限名称
     * @param value        权限值
     * @param dominionName 领地名称
     * @return 设置后的领地信息
     */
    public static DominionDTO setFlag(Player operator, String flag, boolean value, String dominionName) {
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            Dominion.notification.error(operator, "领地 %s 不存在", dominionName);
            return null;
        }
        if (noAuthToChangeFlags(operator, dominion)) return null;
        Flag f = Flag.getFlag(flag);
        if (f == null) {
            Dominion.notification.error(operator, "未知的领地权限 %s", flag);
            return null;
        }
        return dominion.setFlagValue(f, value);
    }
}
