package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.api.dtos.flag.PreFlag;
import cn.lunadeer.dominion.dtos.*;
import cn.lunadeer.dominion.managers.Translation;

import static cn.lunadeer.dominion.utils.ControllerUtils.*;

public class MemberController {

    /**
     * 设置玩家成员权限
     *
     * @param operator     操作者
     * @param player_name  玩家
     * @param flag         权限名称
     * @param value        权限值
     * @param dominionName 领地名称
     */
    public static void setMemberFlag(AbstractOperator operator, String dominionName, String player_name, PreFlag flag, boolean value) {
        operator.addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_SetMemberFlagFailed, player_name, dominionName, flag, value);
        operator.addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetMemberFlagSuccess, player_name, dominionName, flag, value);
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionNotExist, dominionName);
            return;
        }
        if (notAdminOrOwner(operator, dominion)) return;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_PlayerNotExist, player_name);
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_PlayerNotMember, player_name, dominionName);
            return;
        }
        GroupDTO group = GroupDTO.select(privilege.getGroupId());
        if (group != null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_PlayerBelongToGroup, player_name, group.getNamePlain());
            return;
        }
        if ((flag.getFlagName().equals("admin") || isAdmin(privilege)) && notOwner(operator, dominion)) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwnerForSetAdmin, dominionName);
            return;
        }
        if (flag.getFlagName().equals("admin")) {
            privilege.setAdmin(value);
        } else {
            privilege.setFlagValue(flag, value);
        }
        operator.completeResult();
    }

    public static void applyTemplate(AbstractOperator operator, String dominionName, String playerName, String templateName) {
        operator.addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_ApplyTemplateFailed, templateName, playerName, dominionName);
        operator.addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_ApplyTemplateSuccess, templateName, playerName, dominionName);
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionNotExist, dominionName);
            return;
        }
        if (notAdminOrOwner(operator, dominion)) return;
        PlayerDTO player = PlayerDTO.select(playerName);
        if (player == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_PlayerNotExist, playerName);
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_PlayerNotMember, playerName, dominionName);
            return;
        }
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_TemplateNotExist, templateName);
            return;
        }
        if (notOwner(operator, dominion) && (isAdmin(privilege) || template.getAdmin())) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwnerForSetAdmin, dominionName);
            return;
        }
        privilege = privilege.applyTemplate(template);
        operator.completeResult();
    }

}
