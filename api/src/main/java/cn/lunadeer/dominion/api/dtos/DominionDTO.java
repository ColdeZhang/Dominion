package cn.lunadeer.dominion.api.dtos;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface DominionDTO {
    /**
     * 获取领地 ID
     *
     * @return 领地 ID
     */
    Integer getId();

    /**
     * 获取领地所有者 UUID
     *
     * @return 领地所有者 UUID
     */
    UUID getOwner();

    /**
     * 获取领地名称
     *
     * @return 领地名称
     */
    String getName();

    /**
     * 获取领地所在世界
     *
     * @return 领地所在世界  如果世界不存在，则返回null
     */
    @Nullable World getWorld();

    /**
     * 获取领地所在世界 UUID
     *
     * @return 领地所在世界 UUID
     */
    UUID getWorldUid();

    Integer getX1();

    Integer getY1();

    Integer getZ1();

    Integer getX2();

    Integer getY2();

    Integer getZ2();

    /**
     * 获取领地面积
     *
     * @return 领地面积
     */
    Integer getSquare();

    /**
     * 获取领地体积
     *
     * @return 领地体积
     */
    Integer getVolume();

    /**
     * 获取领地X轴向（东西向）宽度
     *
     * @return 领地X轴向（东西向）宽度
     */
    Integer getWidthX();

    /**
     * 获取领地Y轴向（上下向）高度
     *
     * @return 领地Y轴向（上下向）高度
     */
    Integer getHeight();

    /**
     * 获取领地Z轴向（南北向）宽度
     *
     * @return 领地Z轴向（南北向）宽度
     */
    Integer getWidthZ();

    /**
     * 获取父领地 ID
     *
     * @return 父领地 ID  如果没有父领地，则返回 -1
     */
    Integer getParentDomId();

    String getJoinMessage();

    String getLeaveMessage();

    /**
     * 获取领地某个权限的值
     *
     * @param flag 权限
     * @return 权限值
     */
    Boolean getFlagValue(Flag flag);

    /**
     * 获取领地传送点坐标
     *
     * @return 领地传送点坐标  如果没有设置传送点，则返回null
     */
    @Nullable Location getTpLocation();

    Location getLocation1();

    Location getLocation2();

    int getColorR();

    int getColorG();

    int getColorB();

    String getColor();

    int getColorHex();
}
