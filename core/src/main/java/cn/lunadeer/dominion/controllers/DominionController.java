package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
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
     * 删除领地 会同时删除其所有子领地
     *
     * @param operator      操作者
     * @param dominion_name 领地名称
     * @param force         是否强制删除
     */
    public static void delete(AbstractOperator operator, String dominion_name, boolean force) {
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_DeleteDominionFailed);
        AbstractOperator.Result SUCCESS = new AbstractOperator.Result(AbstractOperator.Result.SUCCESS, Translation.Messages_DeleteDominionSuccess, dominion_name);
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            return;
        }
        List<DominionDTO> sub_dominions = getSubDominionsRecursive(dominion);
        if (!force) {
            AbstractOperator.Result WARNING = new AbstractOperator.Result(AbstractOperator.Result.WARNING, Translation.Messages_DeleteDominionConfirm, dominion_name);
            showSubNamesWarning(sub_dominions, WARNING);
            if (operator instanceof BukkitPlayerOperator) {
                Notification.warn(operator.getPlayer(), Translation.Messages_DeleteDominionForceConfirm, dominion_name);
            }
            operator.setResponse(WARNING);
            return;
        }
        DominionDTO.delete(dominion);
        // 退还经济
        int count;
        if (Dominion.config.getEconomyOnlyXZ(operator.getPlayer())) {
            count = dominion.getSquare();
            for (DominionDTO sub_dominion : sub_dominions) {
                count += sub_dominion.getSquare();
            }
        } else {
            count = dominion.getVolume();
            for (DominionDTO sub_dominion : sub_dominions) {
                count += sub_dominion.getVolume();
            }
        }
        if (handleEconomyFailed(operator, count, false, FAIL, SUCCESS)) return;
        operator.setResponse(SUCCESS);
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
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_SetTpLocationFailed);
        DominionDTO dominion = getExistDomAndIsOwner(operator, dominion_name);
        if (dominion == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_DominionNotExist, dominion_name));
            return;
        }
        World world = dominion.getWorld();
        if (world == null) {
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, Translation.Messages_DominionWorldNotExist));
            return;
        }
        Location loc = new Location(world, x, y, z);
        // 检查是否在领地内
        if (isInDominion(dominion, loc)) {
            loc.setY(loc.getY() + 1.5);
            dominion.setTpLocation(loc);
            operator.setResponse(new AbstractOperator.Result(AbstractOperator.Result.SUCCESS,
                    Translation.Messages_SetTpLocationSuccess, dominion_name
                    , loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        } else {
            operator.setResponse(FAIL.addMessage(Translation.Messages_TpLocationNotInDominion, dominion_name));
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


    private static boolean isIntersect(DominionDTO a, int[] cord) {
        return isIntersect(a, cord[0], cord[1], cord[2], cord[3], cord[4], cord[5]);
    }

    private static boolean isIntersect(int[] cord, Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        return cord[0] < x2 && cord[3] > x1 &&
                cord[1] < y2 && cord[4] > y1 &&
                cord[2] < z2 && cord[5] > z1;
    }

    /**
     * 判断 sub 是否完全被 parent 包裹
     */
    private static boolean isContained(DominionDTO sub, DominionDTO parent) {
        if (parent.getId() == -1) {
            return true;
        }
        return isContained(sub.getX1(), sub.getY1(), sub.getZ1(), sub.getX2(), sub.getY2(), sub.getZ2(), parent.getX1(), parent.getY1(), parent.getZ1(), parent.getX2(), parent.getY2(), parent.getZ2());
    }

    private static boolean isContained(int[] cords, DominionDTO parent) {
        return isContained(cords[0], cords[1], cords[2], cords[3], cords[4], cords[5], parent);
    }

    private static boolean isContained(DominionDTO sub, int[] cords) {
        return isContained(sub, cords[0], cords[1], cords[2], cords[3], cords[4], cords[5]);
    }

    private static boolean isContained(Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2, DominionDTO parent) {
        if (parent.getId() == -1) {
            return true;
        }
        return isContained(x1, y1, z1, x2, y2, z2, parent.getX1(), parent.getY1(), parent.getZ1(), parent.getX2(), parent.getY2(), parent.getZ2());
    }

    private static boolean isContained(DominionDTO sub, Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        return isContained(sub.getX1(), sub.getY1(), sub.getZ1(), sub.getX2(), sub.getY2(), sub.getZ2(), x1, y1, z1, x2, y2, z2);
    }

    private static boolean isContained(int sub_x1, int sub_y1, int sub_z1, int sub_x2, int sub_y2, int sub_z2, int parent_x1, int parent_y1, int parent_z1, int parent_x2, int parent_y2, int parent_z2) {
        return sub_x1 >= parent_x1 && sub_x2 <= parent_x2 &&
                sub_y1 >= parent_y1 && sub_y2 <= parent_y2 &&
                sub_z1 >= parent_z1 && sub_z2 <= parent_z2;
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
        AbstractOperator.Result FAIL = new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "");
        DominionDTO dominion = DominionDTO.select(dominion_name);
        if (dominion == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_DominionNotExist, dominion_name));
            return null;
        }
        if (notOwner(operator, dominion)) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotDominionOwner, dominion_name));
            return null;
        }
        return dominion;
    }


    private static @Nullable DominionDTO expandContractPreCheck(AbstractOperator operator, @Nullable DominionDTO dominion, AbstractOperator.Result FAIL) {
        if (dominion == null) {
            return null;
        }
        if (operator.getLocation() == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CannotGetLocation));
            return null;
        }
        if (!operator.getLocation().getWorld().getUID().equals(dominion.getWorldUid())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CrossWorldOperationDisallowed));
            return null;
        }
        if (!isInDominion(dominion, operator.getLocation())) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_NotInDominion, dominion.getName()));
            return null;
        }
        return dominion;
    }

    private static int[] expandContractSizeChange(AbstractOperator operator, @NotNull DominionDTO dominion, boolean expand, int size, AbstractOperator.Result FAIL) {
        BlockFace face = operator.getDirection();
        if (face == null) {
            operator.setResponse(FAIL.addMessage(Translation.Messages_CannotGetDirection));
            return null;
        }
        int[] result = new int[6];
        result[0] = dominion.getX1();
        result[1] = dominion.getY1();
        result[2] = dominion.getZ1();
        result[3] = dominion.getX2();
        result[4] = dominion.getY2();
        result[5] = dominion.getZ2();
        if (!expand) {
            size = size * -1;
        }
        switch (face) {
            case NORTH:
                result[2] -= size;
                break;
            case SOUTH:
                result[5] += size;
                break;
            case WEST:
                result[0] -= size;
                break;
            case EAST:
                result[3] += size;
                break;
            case UP:
                result[4] += size;
                break;
            case DOWN:
                result[1] -= size;
                break;
            default:
                operator.setResponse(FAIL.addMessage(Translation.Messages_InvalidDirection, face));
                return null;
        }
        if (!expand) {
            // 校验第二组坐标是否小于第一组坐标
            if (result[0] > result[3] || result[1] > result[4] || result[2] > result[5]) {
                operator.setResponse(FAIL.addMessage(Translation.Messages_ContractSizeInvalid));
                return null;
            }
        }
        if (sizeNotValid(operator, result)) {
            return null;
        }
        return result;
    }


    /**
     * 以警告形式打印所有子领地名称
     *
     * @param sub_dominions 子领地列表
     * @param WARNING       警告消息
     */
    public static void showSubNamesWarning(List<DominionDTO> sub_dominions, AbstractOperator.Result WARNING) {
        String sub_names = "";
        for (DominionDTO sub_dominion : sub_dominions) {
            sub_names = sub_dominion.getName() + ", ";
        }
        if (!sub_dominions.isEmpty()) {
            sub_names = sub_names.substring(0, sub_names.length() - 2);
            WARNING.addMessage(Translation.Messages_SubDominionList, sub_names);
        }
    }

}
