package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.managers.Translation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
     * 设置领地的卫星地图地块颜色
     *
     * @param operator 操作者
     * @param color    16进制颜色 例如 #ff0000
     * @param dom_name 领地名称
     */
    public static void setMapColor(AbstractOperator operator, String color, String dom_name) {
        DominionDTO dominion = getExistDomAndIsOwner(operator, dom_name);
        if (dominion == null) {
            return;
        }
        color = color.toUpperCase();    // 转换为大写
        if (!color.matches("^#[0-9a-fA-F]{6}$")) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_MapColorInvalid);
            return;
        }
        dominion.setColor(color);
        operator.addResult(AbstractOperator.ResultType.SUCCESS, Translation.Messages_SetMapColorSuccess, dom_name, color);
    }

    /**
     * 设置领地的卫星地图地块颜色
     *
     * @param operator 操作者
     * @param color    16进制颜色 例如 #ff0000
     */
    public static void setMapColor(AbstractOperator operator, String color) {
        DominionDTO dominion = getPlayerCurrentDominion(operator);
        if (dominion == null) {
            operator.addResult(AbstractOperator.ResultType.FAILURE, Translation.Messages_CannotGetDominionAuto);
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
