package cn.lunadeer.dominion.api.dtos;

import org.jetbrains.annotations.NotNull;

public interface Flag {
    /**
     * 权限名称（英文）
     *
     * @return 权限名称
     */
    @NotNull String getFlagName();

    /**
     * 权限显示名称（中文）
     * 该名称从languages文件中加载
     *
     * @return 权限显示名称
     */
    @NotNull String getDisplayName();

    /**
     * 权限描述
     * 该描述从languages文件中加载
     *
     * @return 权限描述
     */
    @NotNull String getDescription();

    /**
     * 获取权限默认值
     *
     * @return 权限默认值
     */
    @NotNull Boolean getDefaultValue();

    /**
     * 获取权限是否启用
     *
     * @return 权限是否启用
     */
    @NotNull Boolean getEnable();

    /**
     * 获取权限是否为环境设置
     *
     * @return 权限是否为环境设置
     */
    @NotNull Boolean isEnvironmentFlag();
}
