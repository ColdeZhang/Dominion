package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.PreFlag;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.utils.ControllerUtils;

import static cn.lunadeer.dominion.utils.ControllerUtils.notAdminOrOwner;

public class FlagsController {

    /**
     * 设置领地权限
     *
     * @param operator 操作者
     * @param flag     权限名称
     * @param value    权限值
     */
    public static void setFlag(AbstractOperator operator, Flag flag, boolean value) {
        DominionDTO dominion = ControllerUtils.getPlayerCurrentDominion(operator);
        if (dominion == null) return;
        setFlag(operator, flag, value, dominion.getName());
        operator.addResult(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetDominionFlagSuccess, flag, value);
    }

    /**
     * 设置领地权限
     *
     * @param operator     操作者
     * @param flag         权限名称
     * @param value        权限值
     * @param dominionName 领地名称
     */
    public static void setFlag(AbstractOperator operator, Flag flag, boolean value, String dominionName) {
        DominionDTO dominion = cn.lunadeer.dominion.dtos.DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionNotExist, dominionName);
            return;
        }
        if (notAdminOrOwner(operator, dominion)) return;
        if (flag instanceof EnvFlag) {
            dominion.setEnvFlagValue((EnvFlag) flag, value);
        } else {
            dominion.setGuestFlagValue((PreFlag) flag, value);
        }
        operator.addResult(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetDominionFlagSuccess, flag, value);
    }
}
