package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.dtos.*;

import static cn.lunadeer.dominion.controllers.Apis.noAuthToChangeFlags;
import static cn.lunadeer.dominion.controllers.Apis.notOwner;

public class MemberController {

    /**
     * 清空玩家成员权限
     *
     * @param operator     操作者
     * @param player_name  玩家
     * @param dominionName 领地名称
     */
    public static void memberRemove(AbstractOperator operator, String dominionName, String player_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "将玩家 %s 从领地 %s 移除失败", player_name, dominionName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "将玩家 %s 从领地 %s 移除成功", player_name, dominionName);
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在", dominionName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) return;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不存在或没有登录过", player_name));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不是领地 %s 的成员", player_name, dominionName));
            return;
        }
        if (privilege.getAdmin() && notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage("你不是领地 %s 的拥有者，无法移除一个领地管理员", dominionName));
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
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "设置玩家 %s 在领地 %s 的权限 %s 为 %s 失败", player_name, dominionName, flag, value);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "设置玩家 %s 在领地 %s 的权限 %s 为 %s 成功", player_name, dominionName, flag, value);
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在", dominionName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) return;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不存在或没有登录过", player_name));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不是领地 %s 的成员", player_name, dominionName));
            return;
        }
        if ((flag.equals("admin") || privilege.getAdmin()) && notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage("你不是领地 %s 的拥有者，无法修改其他玩家管理员的权限", dominionName));
            return;
        }
        if (flag.equals("admin")) {
            privilege.setAdmin(value);
        } else {
            Flag f = Flag.getFlag(flag);
            if (f == null) {
                operator.setResponse(FAIL.addMessage("未知的领地权限 %s", flag));
                return;
            }
            privilege.setFlagValue(f, value);
        }
        operator.setResponse(SUCCESS);
    }

    public static void memberAdd(AbstractOperator operator, String dominionName, String player_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "将玩家 %s 添加到领地成员 %s 失败", player_name, dominionName);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "将玩家 %s 添加到领地成员 %s 成功", player_name, dominionName);
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在", dominionName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) return;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不存在或没有登录过", player_name));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege != null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 已经是领地 %s 的成员", player_name, dominionName));
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
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "应用模板 %s 到玩家 %s 在领地 %s 的权限成功", templateName, playerName, dominionName);
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "应用模板 %s 到玩家 %s 在领地 %s 的权限失败", templateName, playerName, dominionName);
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage("领地 %s 不存在", dominionName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) return;
        PlayerDTO player = PlayerDTO.select(playerName);
        if (player == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不存在或没有登录过", playerName));
            return;
        }
        MemberDTO privilege = MemberDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不是领地 %s 的成员", playerName, dominionName));
            return;
        }
        PrivilegeTemplateDTO template = PrivilegeTemplateDTO.select(operator.getUniqueId(), templateName);
        if (template == null) {
            operator.setResponse(FAIL.addMessage("模板 %s 不存在", templateName));
            return;
        }
        if (notOwner(operator, dominion) && privilege.getAdmin()) {
            operator.setResponse(FAIL.addMessage("你不是领地 %s 的拥有者，无法修改其他管理员的权限", dominionName));
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
