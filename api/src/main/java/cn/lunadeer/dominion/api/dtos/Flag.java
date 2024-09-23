package cn.lunadeer.dominion.api.dtos;

public interface Flag {
    /**
     * 权限名称（英文）
     *
     * @return 权限名称
     */
    String getFlagName();

    /**
     * 权限显示名称（中文）
     * 该名称从languages文件中加载
     *
     * @return 权限显示名称
     */
    String getDisplayName();

    /**
     * 权限描述
     * 该描述从languages文件中加载
     *
     * @return 权限描述
     */
    String getDescription();

    /**
     * 获取权限默认值
     *
     * @return 权限默认值
     */
    Boolean getDefaultValue();

    /**
     * 获取权限是否启用
     *
     * @return 权限是否启用
     */
    Boolean getEnable();
}
