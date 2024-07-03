package cn.lunadeer.dominion.controllers;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import org.bukkit.Location;

public class Apis {

    public static boolean notOwner(AbstractOperator player, DominionDTO dominion) {
        if (player.isOp()) return false;
        return !dominion.getOwner().equals(player.getUniqueId());
    }

    public static boolean noAuthToChangeFlags(AbstractOperator player, DominionDTO dominion) {
        if (player.isOp()) return false;
        if (!dominion.getOwner().equals(player.getUniqueId())) {
            MemberDTO privileges = MemberDTO.select(player.getUniqueId(), dominion.getId());
            if (privileges == null || !privileges.getAdmin()) {
                player.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "你不是领地 %s 的拥有者或管理员，无权修改权限", dominion.getName()));
                return true;
            }
        }
        return false;
    }

    /**
     * 获取玩家当前所在的领地
     * 如果玩家不在一个领地内或者在子领地内，会提示玩家手动指定要操作的领地名称
     *
     * @param player 玩家
     * @return 当前所在的领地
     */
    public static DominionDTO getPlayerCurrentDominion(AbstractOperator player) {
        Location location = player.getLocation();
        if (location == null) {
            player.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "无法获取你的位置信息"));
            return null;
        }
        DominionDTO dominion = Cache.instance.getDominion(location);
        if (dominion == null) {
            return null;
        }
        if (dominion.getParentDomId() == -1) {
            return dominion;
        } else {
            player.setResponse(new AbstractOperator.Result(AbstractOperator.Result.FAILURE, "你当前在子领地内，请指定要操作的领地名称"));
            return null;
        }
    }

}
