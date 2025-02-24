package cn.lunadeer.dominion.utils;

import org.bukkit.plugin.java.JavaPlugin;

public class XVersionManager {

    public static ImplementationVersion VERSION;

    public static ImplementationVersion GetVersion(JavaPlugin plugin) {
        String version = plugin.getServer().getBukkitVersion();
        XLogger.debug("API version: {0}", version);
        if (version.contains("1.21")) {
            return ImplementationVersion.v1_21;
        } else if (version.contains("1.20.1")
                || version.contains("1.20.4")
                || version.contains("1.20.6")) {
            return ImplementationVersion.v1_20_1;
        }
        XLogger.error("Unsupported API version: {0}", version);
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        return null;
    }

    public enum ImplementationVersion {
        v1_21,
        v1_20_1
    }

}
