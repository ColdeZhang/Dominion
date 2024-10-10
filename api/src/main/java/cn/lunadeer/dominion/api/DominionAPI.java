package cn.lunadeer.dominion.api;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.Flag;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface DominionAPI {

    /**
     * 从缓存获取所有领地信息
     *
     * @return 所有领地信息
     */
    @NotNull List<DominionDTO> getAllDominions();

    /**
     * 从缓存获取玩家当前所在领地
     *
     * @param player 玩家
     * @return 玩家当前所在领地   如果玩家不在任何领地内，则返回null
     */
    @Nullable DominionDTO getPlayerCurrentDominion(@NotNull Player player);

    /**
     * 从缓存获取指定位置的领地信息
     *
     * @param loc 位置
     * @return 领地信息    如果位置不在任何领地内，则返回null
     */
    @Nullable DominionDTO getDominionByLoc(@NotNull Location loc);

    /**
     * 从缓存根据 ID 获取权限组对象
     *
     * @param id 权限组 ID
     * @return 权限组对象    如果权限组不存在，则返回null
     */
    @Nullable GroupDTO getGroup(@NotNull Integer id);

    /**
     * 从缓存获取玩家在指定领地的成员信息
     *
     * @param player   玩家
     * @param dominion 领地
     * @return 玩家在指定领地的成员信息   如果玩家不属于领地成员，则返回null
     */
    @Nullable MemberDTO getMember(@NotNull Player player, @NotNull DominionDTO dominion);

    /**
     * 从缓存获取玩家在指定领地的成员信息
     *
     * @param player_uuid 玩家 UUID
     * @param dominion    领地
     * @return 玩家在指定领地的成员信息  如果玩家不属于领地成员，则返回null
     */
    @Nullable MemberDTO getMember(@NotNull UUID player_uuid, @NotNull DominionDTO dominion);

    /**
     * 从缓存获取指定 ID 的领地信息
     *
     * @param id 领地 ID
     * @return 领地信息   如果领地不存在，则返回null
     */
    @Nullable DominionDTO getDominion(@NotNull Integer id);

    /**
     * 从缓存获取玩家当前正在使用的权限组称号
     *
     * @param uuid 玩家 UUID
     * @return 权限组对象    如果玩家没有使用任何权限组，则返回null
     */
    @Nullable GroupDTO getPlayerUsingGroupTitle(@NotNull UUID uuid);

    /**
     * 获取 flags.yml 中启用的所有环境权限对象（environment部分）
     *
     * @return 环境权限列表
     */
    @NotNull List<Flag> getEnvironmentFlagsEnabled();

    /**
     * 获取 flags.yml 中启用的所有玩家权限对象（privilege部分）
     *
     * @return 玩家权限列表
     */
    @NotNull List<Flag> getPrivilegeFlagsEnabled();

    /**
     * 通过权限名称获取权限对象，即使权限没有启用此方法也会返回权限对象
     *
     * @param flagName 权限名称 （非 displayName）
     * @return 权限对象   如果权限不存在，则返回null
     */
    @Nullable Flag getFlagByName(@NotNull String flagName);
}
