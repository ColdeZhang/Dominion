package cn.lunadeer.dominion.api.dtos;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface DominionDTO {
    /**
     * 获取领地 ID
     *
     * @return 领地 ID
     */
    @NotNull Integer getId();

    /**
     * 获取领地所有者 UUID
     *
     * @return 领地所有者 UUID
     */
    @NotNull UUID getOwner();

    /**
     * 设置领地所有者，设置成功后返回领地对象，设置失败返回null
     *
     * @param owner 领地所有者 UUID
     * @return 领地对象
     */
    @Nullable DominionDTO setOwner(UUID owner);

    /**
     * 设置领地所有者，设置成功后返回领地对象，设置失败返回null
     *
     * @param owner 领地所有者
     * @return 领地对象
     */
    @Nullable DominionDTO setOwner(Player owner);

    /**
     * 获取领地名称
     *
     * @return 领地名称
     */
    @NotNull String getName();

    /**
     * 设置领地名称，设置成功后返回领地对象，设置失败返回null
     *
     * @param name 领地名称
     * @return 领地对象
     */
    @Nullable DominionDTO setName(String name);

    /**
     * 获取领地所在世界，如果世界不存在，则返回null
     *
     * @return 领地所在世界
     */
    @Nullable World getWorld();

    /**
     * 获取领地所在世界 UUID，该接口返回的 UUID 一定不为 null，但是不保证世界一定存在。
     * 如果需要判断世界是否存在，请使用 {@link #getWorld()} 方法。
     *
     * @return 领地所在世界 UUID
     */
    @NotNull UUID getWorldUid();

    /**
     * 获取领地角点坐标1（小角点）的X坐标，X1 &lt; X2
     *
     * @return 领地角点坐标1（小角点）的X坐标
     */
    @NotNull Integer getX1();

    /**
     * 获取领地角点坐标1（小角点）的Y坐标，Y1 &lt; Y2
     *
     * @return 领地角点坐标1（小角点）的Y坐标
     */
    @NotNull Integer getY1();

    /**
     * 获取领地角点坐标1（小角点）的Z坐标，Z1 &lt; Z2
     *
     * @return 领地角点坐标1（小角点）的Z坐标
     */
    @NotNull Integer getZ1();

    /**
     * 获取领地角点坐标2（大角点）的X坐标，X2 &gt; X1
     *
     * @return 领地角点坐标2（大角点）的X坐标
     */
    @NotNull Integer getX2();

    /**
     * 获取领地角点坐标2（大角点）的Y坐标，Y2 &gt; Y1
     *
     * @return 领地角点坐标2（大角点）的Y坐标
     */
    @NotNull Integer getY2();

    /**
     * 获取领地角点坐标2（大角点）的Z坐标，Z2 &gt; Z1
     *
     * @return 领地角点坐标2（大角点）的Z坐标
     */
    @NotNull Integer getZ2();

    /**
     * 设置领地角点坐标，设置成功后返回领地对象，设置失败返回null
     *
     * @param x1 小角点X坐标
     * @param y1 小角点Y坐标
     * @param z1 小角点Z坐标
     * @param x2 大角点X坐标
     * @param y2 大角点Y坐标
     * @param z2 大角点Z坐标
     * @return 领地对象
     */
    @Nullable DominionDTO setXYZ(Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2);

    /**
     * 设置领地角点坐标，设置成功后返回领地对象，设置失败返回null
     *
     * @param cords 领地角点坐标数组，长度为6，依次为 x1, y1, z1, x2, y2, z2
     * @return 领地对象
     */
    @Nullable DominionDTO setXYZ(int[] cords);

    /**
     * 获取领地面积
     *
     * @return 领地面积
     */
    @NotNull Integer getSquare();

    /**
     * 获取领地体积
     *
     * @return 领地体积
     */
    @NotNull Integer getVolume();

    /**
     * 获取领地X轴向（东西向）宽度
     *
     * @return 领地X轴向（东西向）宽度
     */
    @NotNull Integer getWidthX();

    /**
     * 获取领地Y轴向（上下向）高度
     *
     * @return 领地Y轴向（上下向）高度
     */
    @NotNull Integer getHeight();

    /**
     * 获取领地Z轴向（南北向）宽度
     *
     * @return 领地Z轴向（南北向）宽度
     */
    @NotNull Integer getWidthZ();

    /**
     * 获取父领地 ID
     *
     * @return 父领地 ID  如果没有父领地，则返回 -1
     */
    @NotNull Integer getParentDomId();

    /**
     * 获取领地欢迎提示语
     *
     * @return 领地欢迎提示语
     */
    @NotNull String getJoinMessage();

    /**
     * 设置领地欢迎提示语，设置成功后返回领地对象，设置失败返回null
     *
     * @param joinMessage 领地欢迎提示语
     * @return 领地对象
     */
    @Nullable DominionDTO setJoinMessage(String joinMessage);

    /**
     * 获取领地离开提示语
     *
     * @return 领地离开提示语
     */
    @NotNull String getLeaveMessage();

    /**
     * 设置领地离开提示语，设置成功后返回领地对象，设置失败返回null
     *
     * @param leaveMessage 领地离开提示语
     * @return 领地对象
     */
    @Nullable DominionDTO setLeaveMessage(String leaveMessage);

    /**
     * 获取领地所有环境配置
     *
     * @return 领地环境权限配置
     */
    @NotNull Map<Flag, Boolean> getEnvironmentFlagValue();

    /**
     * 获取领地访客所有权限配置
     *
     * @return 领地访客权限配置
     */
    @NotNull Map<Flag, Boolean> getGuestPrivilegeFlagValue();

    /**
     * 设置领地某个环境配置或访客权限的值，设置成功后返回领地对象，设置失败返回null
     *
     * @param flag  权限
     * @param value 权限值
     * @return 领地对象
     */
    @Nullable DominionDTO setFlagValue(@NotNull Flag flag, @NotNull Boolean value);

    /**
     * 获取领地传送点坐标
     *
     * @return 领地传送点坐标  如果没有设置传送点，则返回null
     */
    @Nullable Location getTpLocation();

    /**
     * 领地角点坐标1，小角点（小角点的xyz小于大角点的xyz）
     *
     * @return 领地角点坐标1
     */
    @NotNull Location getLocation1();

    /**
     * 领地角点坐标2，大角点（大角点的xyz大于小角点的xyz）
     *
     * @return 领地角点坐标2
     */
    @NotNull Location getLocation2();

    int getColorR();

    int getColorG();

    int getColorB();

    @NotNull String getColor();

    int getColorHex();
}
