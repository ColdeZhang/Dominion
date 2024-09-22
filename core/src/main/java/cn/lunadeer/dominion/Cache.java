package cn.lunadeer.dominion;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Cache {
    /**
     * 获取玩家当前所在领地
     * 此方法会先判断缓存中是否有玩家当前所在领地，如果没有则遍历所有领地判断玩家所在位置
     * 如果玩家不在任何领地内，则返回null
     * 如果玩家在领地内，则返回领地信息
     *
     * @param player 玩家
     * @return 玩家当前所在领地
     */
    DominionDTO getPlayerCurrentDominion(Player player);

    /**
     * 获取指定位置的领地信息
     * 如果位置不在任何领地内，则返回null
     *
     * @param loc 位置
     * @return 领地信息    如果位置不在任何领地内，则返回null
     */
    DominionDTO getDominionByLoc(Location loc);

    GroupDTO getGroup(Integer id);

    /**
     * 获取玩家在指定领地的特权
     * 如果玩家不存在特权，则返回null
     *
     * @param player   玩家
     * @param dominion 领地
     * @return 特权表
     */
    MemberDTO getMember(Player player, DominionDTO dominion);

    MemberDTO getMember(UUID player_uuid, DominionDTO dominion);

    DominionDTO getDominion(Integer id);

    @Nullable GroupDTO getPlayerUsingGroupTitle(UUID uuid);
}
