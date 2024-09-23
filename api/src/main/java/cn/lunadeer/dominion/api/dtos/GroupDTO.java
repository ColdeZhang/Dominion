package cn.lunadeer.dominion.api.dtos;

import net.kyori.adventure.text.Component;

public interface GroupDTO {
    /**
     * 获取权限组 ID
     *
     * @return 权限组 ID
     */
    Integer getId();

    /**
     * 获取权限组所属领地 ID
     *
     * @return 权限组所属领地 ID
     */
    Integer getDomID();

    /**
     * 获取权限组名称
     *
     * @return 权限组名称
     */
    String getName();

    /**
     * 获取权限组名称（带颜色） kyori.adventure.text.Component
     *
     * @return 权限组名称（带颜色）
     */
    Component getNameColoredComponent();

    /**
     * 获取权限组名称（带颜色） Bukkit
     *
     * @return 权限组名称（带颜色）
     */
    String getNameColoredBukkit();

    /**
     * 获取权限组是否为管理员组
     *
     * @return 是否为管理员组
     */
    Boolean getAdmin();

    /**
     * 获取权限组某个权限配置
     *
     * @param flag 权限
     * @return 权限配置值
     */
    Boolean getFlagValue(Flag flag);
}
