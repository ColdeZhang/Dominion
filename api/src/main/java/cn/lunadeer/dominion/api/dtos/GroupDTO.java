package cn.lunadeer.dominion.api.dtos;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface GroupDTO {
    /**
     * 获取权限组 ID
     *
     * @return 权限组 ID
     */
    @NotNull Integer getId();

    /**
     * 获取权限组所属领地 ID
     *
     * @return 权限组所属领地 ID
     */
    @NotNull Integer getDomID();

    /**
     * 设置权限组名称，可以包含颜色代码，设置成功后返回权限组对象，设置失败返回null
     *
     * @param name 权限组名称
     * @return 权限组对象
     */
    @Nullable GroupDTO setName(@NotNull String name);

    /**
     * 获取权限组名称（普通字符，不含颜色代码）
     * 绝大多数情况下应该使用该方法获取权限组名称
     *
     * @return 权限组名称
     */
    @NotNull String getNamePlain();

    /**
     * 获取权限组名称（原始字符，包含颜色代码）
     *
     * @return 权限组名称
     */
    @NotNull String getNameRaw();

    /**
     * 获取权限组名称（带颜色） kyori.adventure.text.Component 类型
     *
     * @return 权限组名称（带颜色）
     */
    @NotNull Component getNameColoredComponent();

    /**
     * 获取权限组名称（带颜色） Bukkit类型
     *
     * @return 权限组名称（带颜色）
     */
    @NotNull String getNameColoredBukkit();

    /**
     * 设置权限组是否为管理员组，设置成功后返回权限组对象，设置失败返回null
     *
     * @param admin 是否为管理员组
     * @return 权限组对象
     */
    @Nullable GroupDTO setAdmin(@NotNull Boolean admin);

    /**
     * 获取权限组是否为管理员组
     *
     * @return 是否为管理员组
     */
    @NotNull Boolean getAdmin();

    /**
     * 获取权限组某个权限配置
     *
     * @param flag 权限
     * @return 权限配置值，如果权限不存在则返回默认值
     */
    @NotNull Boolean getFlagValue(@NotNull Flag flag);

    /**
     * 获取权限组所有权限配置
     *
     * @return 权限配置
     */
    @NotNull Map<Flag, Boolean> getFlagsValue();

    /**
     * 设置权限组某个权限配置，设置成功后返回权限组对象，设置失败返回null
     *
     * @param flag  权限
     * @param value 权限值
     * @return 权限组对象
     */
    @Nullable GroupDTO setFlagValue(@NotNull Flag flag, @NotNull Boolean value);
}
