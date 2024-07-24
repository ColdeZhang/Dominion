package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;

import static cn.lunadeer.dominion.controllers.Apis.noAuthToChangeFlags;

public class FlagsController {

    /**
     * 设置领地权限
     *
     * @param operator 操作者
     * @param flag     权限名称
     * @param value    权限值
     */
    public static void setFlag(AbstractOperator operator, String flag, boolean value) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) return;
        setFlag(operator, flag, value, dominion.getName());
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "设置领地权限 %s 为 %s", flag, value));
    }

    /**
     * 设置领地权限
     *
     * @param operator     操作者
     * @param flag         权限名称
     * @param value        权限值
     * @param dominionName 领地名称
     */
    public static void setFlag(AbstractOperator operator, String flag, boolean value, String dominionName) {
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "领地 %s 不存在", dominionName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) return;
        Flag f = Flag.getFlag(flag);
        if (f == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "未知的领地权限 %s", flag));
            return;
        }
        dominion.setFlagValue(f, value);
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "设置领地权限 %s 为 %s", flag, value));
    }
}
