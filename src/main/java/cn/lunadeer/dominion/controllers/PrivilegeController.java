package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.*;
import cn.lunadeer.minecraftpluginutils.XLogger;

import java.util.UUID;

import static cn.lunadeer.dominion.controllers.Apis.noAuthToChangeFlags;
import static cn.lunadeer.dominion.controllers.Apis.notOwner;

public class PrivilegeController {

    /**
     * 清空玩家成员权限
     *
     * @param operator    操作者
     * @param player_name 玩家
     */
    public static void clearPrivilege(AbstractOperator operator, String player_name) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "你不在任何领地内，请指定领地名称 /dominion clear_privilege <玩家名称> <领地名称>"));
            return;
        }
        clearPrivilege(operator, player_name, dominion.getName());
    }

    /**
     * 清空玩家成员权限
     *
     * @param operator     操作者
     * @param player_name  玩家
     * @param dominionName 领地名称
     */
    public static void clearPrivilege(AbstractOperator operator, String player_name, String dominionName) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "清空玩家 %s 在领地 %s 的权限失败", player_name, dominionName);
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
        PlayerPrivilegeDTO privilege = PlayerPrivilegeDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            operator.setResponse(FAIL.addMessage("玩家 %s 不是领地 %s 的成员", player_name, dominionName));
            return;
        }
        if (privilege.getAdmin() && notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage("你不是领地 %s 的拥有者，无法移除一个领地管理员", dominionName));
            return;
        }
        PlayerPrivilegeDTO.delete(player.getUuid(), dominion.getId());
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "清空玩家 %s 在领地 %s 的权限成功", player_name, dominionName));
    }

    /**
     * 设置玩家成员权限
     *
     * @param operator    操作者
     * @param player_name 玩家
     * @param flag        权限名称
     * @param value       权限值
     */
    public static void setPrivilege(AbstractOperator operator, String player_name, String flag, boolean value) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "你不在任何领地内，请指定领地名称 /dominion set_privilege <玩家名称> <权限名称> <true/false> [领地名称]"));
            return;
        }
        setPrivilege(operator, player_name, flag, value, dominion.getName());
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
    public static void setPrivilege(AbstractOperator operator, String player_name, String flag, boolean value, String dominionName) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "设置玩家 %s 在领地 %s 的权限 %s 为 %s 失败", player_name, dominionName, flag, value);
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
        PlayerPrivilegeDTO privilege = PlayerPrivilegeDTO.select(player.getUuid(), dominion.getId());
        if (privilege == null) {
            privilege = createPlayerPrivilege(operator, player.getUuid(), dominion);
            if (privilege == null) return;
        }
        if (privilege.getAdmin()) {
            if (notOwner(operator, dominion)) {
                operator.setResponse(FAIL.addMessage("你不是领地 %s 的拥有者，无法修改其他管理员的权限", dominionName));
                return;
            }
            if (flag.equals("admin")) {
                privilege.setAdmin(value);
            } else {
                operator.setResponse(FAIL.addMessage("管理员拥有所有权限，无需单独设置权限"));
                return;
            }
        } else {
            Flag f = Flag.getFlag(flag);
            if (f == null) {
                operator.setResponse(FAIL.addMessage("未知的领地权限 %s", flag));
                return;
            }
            privilege.setFlagValue(f, value);
        }
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "设置玩家 %s 在领地 %s 的权限 %s 为 %s 成功", player_name, dominionName, flag, value));
    }

    public static void createPrivilege(AbstractOperator operator, String player_name) {
        DominionDTO dominion = Apis.getPlayerCurrentDominion(operator);
        if (dominion == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "你不在任何领地内，请指定领地名称 /dominion create_privilege <玩家名称> <领地名称>"));
            return;
        }
        createPrivilege(operator, player_name, dominion.getName());
    }

    public static void createPrivilege(AbstractOperator operator, String player_name, String dominionName) {
        DominionDTO dominion = DominionDTO.select(dominionName);
        if (dominion == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "领地 %s 不存在，无法创建成员权限", dominionName));
            return;
        }
        if (noAuthToChangeFlags(operator, dominion)) return;
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "玩家 %s 不存在或没有登录过", player_name));
            return;
        }
        if (createPlayerPrivilege(operator, player.getUuid(), dominion) != null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, "创建玩家 %s 在领地 %s 的成员权限成功", player_name, dominionName));
        }
    }

    private static PlayerPrivilegeDTO createPlayerPrivilege(AbstractOperator operator, UUID player, DominionDTO dom) {
        XLogger.debug("operator: " + operator.getUniqueId() + " player: " + player);
        if (operator.getUniqueId().equals(player)) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "你不能给自己设置成员权限"));
            return null;
        }
        PlayerDTO playerDTO = PlayerDTO.select(player);
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "创建玩家成员权限失败，玩家 %s 已经是领地 %s 的成员", playerDTO.getLastKnownName(), dom.getName()));
            return null;
        }
        privilege = PlayerPrivilegeDTO.insert(new PlayerPrivilegeDTO(player, dom));
        if (privilege == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "创建玩家成员权限失败，请联系管理员"));
            return null;
        }
        return privilege;
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
        PlayerPrivilegeDTO privilege = PlayerPrivilegeDTO.select(player.getUuid(), dominion.getId());
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
