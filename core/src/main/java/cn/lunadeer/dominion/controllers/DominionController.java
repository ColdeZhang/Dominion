package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.lunadeer.dominion.DominionNode.isInDominion;
import static cn.lunadeer.dominion.utils.ControllerUtils.getPlayerCurrentDominion;
import static cn.lunadeer.dominion.utils.ControllerUtils.notOwner;

public class DominionController {

    /**
     * 获取玩家拥有的领地
     *
     * @param owner 玩家
     * @return 领地列表
     */
    public static List<DominionDTO> all(Player owner) {
        return DominionDTO.selectByOwner(owner.getUniqueId());
    }

    public static List<DominionDTO> all() {
        return DominionDTO.selectAll();
    }


    /**
     * 设置领地的进入消息
     *
     * @param operator 操作者
     * @param message  消息
     */
    public static void setJoinMessage(AbstractOperator operator, String message) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            return;
        }
        setJoinMessage(operator, dominion.getName(), message);
    }

    /**
     * 设置进入领地的消息
     *
     * @param operator      操作者
     * @param dominion_name 领地名称
     * @param message       消息
     */
    public static void setJoinMessage(AbstractOperator operator, String message, String dominion_name) {
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return;
        }
        dominion.setJoinMessage(message);
        operator.addResult(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetEnterMessageSuccess, dominion_name);
    }

    /**
     * 设置领地的离开消息
     *
     * @param operator 操作者
     * @param message  消息
     */
    public static void setLeaveMessage(AbstractOperator operator, String message) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            return;
        }
        setLeaveMessage(operator, dominion.getName(), message);
    }

    /**
     * 设置离开领地的消息
     *
     * @param operator      操作者
     * @param dominion_name 领地名称
     * @param message       消息
     */
    public static void setLeaveMessage(AbstractOperator operator, String message, String dominion_name) {
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return;
        }
        dominion.setLeaveMessage(message);
        operator.addResult(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetLeaveMessageSuccess, dominion_name);
    }

    /**
     * 设置领地的传送点
     *
     * @param operator 操作者
     */
    public static void setTpLocation(AbstractOperator operator, int x, int y, int z) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            return;
        }
        setTpLocation(operator, x, y, z, dominion.getName());
    }

    /**
     * 设置领地的传送点
     *
     * @param operator      操作者
     * @param dominion_name 领地名称
     */
    public static void setTpLocation(AbstractOperator operator, int x, int y, int z, String dominion_name) {
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_SetTpLocationFailed, dominion_name)
                    .addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionNotExist, dominion_name);
            return;
        }
        World world = dominion.getWorld();
        if (world == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_SetTpLocationFailed, dominion_name)
                    .addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionWorldNotExist);
            return;
        }
        Location loc = new Location(world, x, y, z);
        // 检查是否在领地内
        if (isInDominion(dominion, loc)) {
            loc.setY(loc.getY() + 1.5);
            dominion.setTpLocation(loc);
            operator.addResult(AbstractOperator.ResultType.SUCCESS,
                    Translation.Messages_SetTpLocationSuccess, dominion_name
                    , loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        } else {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_SetTpLocationFailed, dominion_name)
                    .addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_TpLocationNotInDominion, dominion_name);
        }
    }

    /**
     * 重命名领地
     *
     * @param operator 操作者
     * @param old_name 旧名称
     * @param new_name 新名称
     */
    public static void rename(AbstractOperator operator, String old_name, String new_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_RenameDominionFailed);
        if (new_name.isEmpty()) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNameShouldNotEmpty));
            return;
        }
        if (new_name.contains(" ") || new_name.contains(".")) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNameInvalid));
            return;
        }
        if (Objects.equals(old_name, new_name)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_RenameDominionSameName));
            return;
        }
        DominionDTO dominion = getExistDomAndIsOwner(operator, old_name);
        if (dominion == null) {
            return;
        }
        if (DominionDTO.select(new_name) != null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNameExist, new_name));
            return;
        }
        dominion.setName(new_name);
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_RenameDominionSuccess, old_name, new_name));
    }

    /**
     * 转让领地
     *
     * @param operator    操作者
     * @param dom_name    领地名称
     * @param player_name 玩家名称
     * @param force       是否强制转让
     */
    public static void give(AbstractOperator operator, String dom_name, String player_name, boolean force) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_GiveDominionFailed);
        DominionDTO dominion = getExistDomAndIsOwner(operator, dom_name);
        if (dominion == null) {
            return;
        }
        PlayerDTO player = PlayerController.getPlayerDTO(player_name);
        if (player == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_PlayerNotExist, player_name));
            return;
        }
        if (Objects.equals(dominion.getOwner(), player.getUuid())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionAlreadyBelong, dom_name, player_name));
            return;
        }
        if (dominion.getParentDomId() != -1) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_SubDominionCannotGive, player_name, dom_name));
            return;
        }
        List<DominionDTO> sub_dominions = getSubDominionsRecursive(dominion);
        if (!force) {
            AbstractOperator.Result WARNING = new AbstractOperator.Result(AbstractOperator.Result.WARNING, Translation.Messages_GiveDominionConfirm, dom_name, player_name);
            showSubNamesWarning(sub_dominions, WARNING);
            if (operator instanceof BukkitPlayerOperator) {
                Notification.warn(operator.getPlayer(), Translation.Messages_GiveDominionForceConfirm, dom_name, player_name);
            }
            operator.setResponse(WARNING);
            return;
        }
        dominion.setOwner(player.getUuid());
        for (DominionDTO sub_dominion : sub_dominions) {
            sub_dominion.setOwner(player.getUuid());
        }
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_GiveDominionSuccess, dom_name, player_name));
    }

    /**
     * 设置领地的卫星地图地块颜色
     *
     * @param operator 操作者
     * @param color    16进制颜色 例如 #ff0000
     * @param dom_name 领地名称
     */
    public static void setMapColor(AbstractOperator operator, String color, String dom_name) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_SetMapColorFailed);
        DominionDTO dominion = getExistDomAndIsOwner(operator, dom_name);
        if (dominion == null) {
            return;
        }
        color = color.toUpperCase();    // 转换为大写
        if (!color.matches("^#[0-9a-fA-F]{6}$")) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_MapColorInvalid));
            return;
        }
        dominion.setColor(color);
        operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_SetMapColorSuccess, dom_name, color));
    }

    /**
     * 设置领地的卫星地图地块颜色
     *
     * @param operator 操作者
     * @param color    16进制颜色 例如 #ff0000
     */
    public static void setMapColor(AbstractOperator operator, String color) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_SetMapColorFailed);
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CannotGetDominionAuto));
            return;
        }
        setMapColor(operator, color, dominion.getName());
    }

    private static List<DominionDTO> getSubDominionsRecursive(DominionDTO dominion) {
        List<DominionDTO> sub_dominions = DominionDTO.selectByParentId(dominion.getWorldUid(), dominion.getId());
        List<DominionDTO> sub_sub_dominions = new ArrayList<>();
        for (DominionDTO sub_dominion : sub_dominions) {
            sub_sub_dominions.addAll(getSubDominionsRecursive(sub_dominion));
        }
        sub_dominions.addAll(sub_sub_dominions);
        return sub_dominions;
    }


    private static DominionDTO getExistDomAndIsOwner(AbstractOperator operator, String dominion_name) {
        DominionDTO dominion = DominionDTO.select(dominion_name);
        if (dominion == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_DominionNotExist, dominion_name);
            return null;
        }
        if (notOwner(operator, dominion)) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwner, dominion_name);
            return null;
        }
        return dominion;
    }
}
