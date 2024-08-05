package cn.lunadeer.dominion.events;

import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterEvents {

    private JavaPlugin plugin;

    public RegisterEvents(JavaPlugin plugin) {
        APIVersion version = GetAPIVersion(plugin);
        this.plugin = plugin;
        if (version == null) {
            return;
        }
        try {
            switch (version) {
                case v1_21:
                    XLogger.debug("Load API version: 1.21");
                    registerEvents("cn.lunadeer.dominion.events_v1_21.PlayerEvents");
                    registerEvents("cn.lunadeer.dominion.events_v1_21.EnvironmentEvents");
                    registerEvents("cn.lunadeer.dominion.events_v1_21.SelectPointEvents");
                    break;
                case v1_20_1:
                    XLogger.debug("Load API version: 1.20.1");
                    registerEvents("cn.lunadeer.dominion.events_v1_20_1.PlayerEvents");
                    registerEvents("cn.lunadeer.dominion.events_v1_20_1.EnvironmentEvents");
                    registerEvents("cn.lunadeer.dominion.events_v1_20_1.SelectPointEvents");
                    break;
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            XLogger.err("Failed to register events: %s", e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    enum APIVersion {
        v1_21,
        v1_20_1
    }

    private static APIVersion GetAPIVersion(JavaPlugin plugin) {
        String version = plugin.getServer().getBukkitVersion();
        if (version.contains("1.21")) {
            return APIVersion.v1_21;
        } else if (version.contains("1.20.1")
                || version.contains("1.20.4")
                || version.contains("1.20.6")) {
            return APIVersion.v1_20_1;
        }
        XLogger.err("Unsupported API version: %s", version);
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        return null;
    }

    private void registerEvents(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz = Class.forName(className);
        Listener listener = (Listener) clazz.newInstance();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

}
