package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.*;
import cn.lunadeer.dominion.managers.Translation;

import static cn.lunadeer.dominion.utils.ControllerUtils.*;

public class MemberController {

    /**
     * 清空玩家成员权限
     *
     * @param operator     操作者
     * @param player_name  玩家
     * @param dominionName 领地名称
     */
    public static void memberRemove(AbstractOperator operator, String dominionName, String player_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_RemoveMemberFailed, player_name, dominionName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_RemoveMemberSuccess, player_name, dominionName);
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, dominionName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) return;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotExist, player_name));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotMember, player_name, dominionName));
            return;
        }
        if (isAdmin(privilege) && notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotDominionOwnerForRemoveAdmin, dominionName));
            return;
        }
        MemberDTO.delete(player.getUuid(), dominion.getId());
        operator.setResponse(SUCCESS);
    }

    /**
     * 设置玩家成员权限
     *
     * @param operator     操作者
     * @param player_name  玩家
     * @param flag         权限名称
     * @param value        权限值
     * @param dominionName 领地名称
     */
    public static void setMemberFlag(AbstractOperator operator, String dominionName, String player_name, String flag, boolean value) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_SetMemberFlagFailed, player_name, dominionName, flag, value);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_SetMemberFlagSuccess, player_name, dominionName, flag, value);
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, dominionName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) return;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotExist, player_name));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotMember, player_name, dominionName));
            return;
        }
        GroupDTO group = GroupDTO.select(privilege.getGroupId());
        if (group != null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerBelongToGroup, player_name, group.getNamePlain()));
            return;
        }
        if ((flag.equals("admin") || isAdmin(privilege)) && notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotDominionOwnerForSetAdmin, dominionName));
            return;
        }
        if (flag.equals("admin")) {
            privilege.setAdmin(value);
        } else {
            Flag f = Flag.getFlag(flag);
            if (f == null) {
                operator.setResponse(FAIL.addMessage(Translation.Messages_UnknownFlag, flag));
                return;
            }
            privilege.setFlagValue(f, value);
        }
        operator.setResponse(SUCCESS);
    }

    public static void memberAdd(AbstractOperator operator, String dominionName, String player_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_AddMemberFailed, player_name, dominionName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_AddMemberSuccess, player_name, dominionName);
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, dominionName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) return;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotExist, player_name));
            return;
        }
        if (dominion.getOwner().equals(player.getUuid())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_OwnerCannotBeMember, player_name, dominionName));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege != null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerAlreadyMember, player_name, dominionName));
            return;
        }
        privilege = MemberDTO.insert(new MemberDTO(player.getUuid(), dominion));
        if (privilege == null) {
            operator.setResponse(FAIL);
        } else {
            operator.setResponse(SUCCESS);
        }
    }

    public static void applyTemplate(AbstractOperator operator, String dominionName, String playerName, String templateName) {
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_ApplyTemplateSuccess, templateName, playerName, dominionName);
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_ApplyTemplateFailed, templateName, playerName, dominionName);
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, dominionName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) return;
        PlayerDTO player = PlayerDTO.select(playerName);
        if (player == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotExist, playerName));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotMember, playerName, dominionName));
            return;
        }
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_TemplateNotExist, templateName));
            return;
        }
        if (notOwner(operator, dominion) && (isAdmin(privilege) || template.getAdmin())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotDominionOwnerForSetAdmin, dominionName));
            return;
        }
        privilege = privilege.applyTemplate(template);
        if (privilege == null) {
            operator.setResponse(FAIL);
        } else {
            operator.setResponse(SUCCESS);
        }
    }

}
