package cn.lunadeer.dominion.api;

import org.bukkit.Bukkit;

public class Dominion {

    private final static int[] requiredDominionVersion = new int[]{2, 12, 0};

    public static DominionAPI getInstance() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        if (!isDominionEnabled()) {
            throw new IllegalStateException("Dominion is not installed.");
        }
        if (!isVersionCompatible(requiredDominionVersion)) {
            throw new IllegalStateException("DominionAPI is not compatible with the current version of Dominion."
                    + " Required Dominion version: " + requiredDominionVersion[0] + "." + requiredDominionVersion[1] + "." + requiredDominionVersion[2]);
        }
        // 通过反射获取 Cache 类中的 instance 字段
        var instanceField = Class.forName("cn.lunadeer.dominion.DominionInterface").getDeclaredField("instance");
        // 设置可访问
        instanceField.setAccessible(true);
        // 返回 Cache 的实例
        return (DominionAPI) instanceField.get(null);
    }

    public static boolean isDominionEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("Dominion");
    }

    private static int[] getDominionVersion() {
        var plugin = Bukkit.getPluginManager().getPlugin("Dominion");
        if (plugin == null) {
            return new int[]{0, 0, 0};
        }
        var version = plugin.getDescription().getVersion().replaceAll("[^0-9.]", "");
        var versionSplit = version.split("\\.");
        var versionInt = new int[versionSplit.length];
        for (int i = 0; i < versionSplit.length; i++) {
            versionInt[i] = Integer.parseInt(versionSplit[i]);
        }
        return versionInt;
    }

    private static boolean isVersionCompatible(int[] requiredVersion) {
        var version = getDominionVersion();
        for (int i = 0; i < requiredVersion.length; i++) {
            if (version[i] < requiredVersion[i]) {
                return false;
            }
        }
        return true;
    }
}
