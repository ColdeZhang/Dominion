package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.api.dtos.flag.PreFlag;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.managers.Translation;

import static cn.lunadeer.dominion.utils.ControllerUtils.notAdminOrOwner;
import static cn.lunadeer.dominion.utils.ControllerUtils.notOwner;

public class GroupController {

    /**
     * 设置权限组权限
     *
     * @param operator  操作者
     * @param domName   领地名称
     * @param groupName 权限组名称
     * @param flag      权限名称
     * @param value     权限值
     */
    public static void setGroupFlag(AbstractOperator operator, String domName, String groupName, PreFlag flag, boolean value) {
        operator.addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_SetGroupFlagFailed, groupName, flag, value);
        operator.addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetGroupFlagSuccess, groupName, flag, value);
        DominionDTO dominion = DominionDTO.select(domName);
        if (dominion == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionNotExist, domName);
            return;
        }
        if (notAdminOrOwner(operator, dominion)) {
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), groupName);
        if (group == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_GroupNotExist, domName, groupName);
            return;
        }
        if ((flag.getFlagName().equals("admin") || group.getAdmin()) && notOwner(operator, dominion)) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwnerForGroup, domName);
            return;
        }
        if (flag.getFlagName().equals("admin")) {
            group = group.setAdmin(value);
        } else {
            group = group.setFlagValue(flag, value);
        }
        if (group == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            return;
        }
        operator.completeResult();
    }

}
