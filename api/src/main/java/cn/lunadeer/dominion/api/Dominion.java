package cn.lunadeer.dominion.api;

import org.bukkit.Bukkit;

public class Dominion {

    public static DominionAPI getInstance() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // 通过反射获取 Cache 类中的 instance 字段
        var instanceField = Class.forName("cn.lunadeer.dominion.Cache").getDeclaredField("instance");
        // 设置可访问
        instanceField.setAccessible(true);
        // 返回 Cache 的实例
        return (DominionAPI) instanceField.get(null);
    }

    public static boolean isDominionEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("Dominion");
    }
}
