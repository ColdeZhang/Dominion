package cn.lunadeer.dominion;

import cn.lunadeer.dominion.handler.DominionEventHandler;
import cn.lunadeer.dominion.handler.GroupEventHandler;
import cn.lunadeer.dominion.handler.MemberEventHandler;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static cn.lunadeer.dominion.utils.Misc.isPaper;

public class EventsRegister {

    private JavaPlugin plugin;

    public EventsRegister(JavaPlugin plugin) {
        APIVersion version = GetAPIVersion(plugin);
        this.plugin = plugin;
        if (version == null) {
            return;
        }
        try {
            switch (version) {
                case v1_21:
                    XLogger.debug("Load API version: 1.21");
                    if (isPaper()) {
                        XLogger.debug("Load Paper special events");
                        registerEvents("cn.lunadeer.dominion.v1_21_paper.events.PlayerEvents");
                        registerEvents("cn.lunadeer.dominion.v1_21_paper.events.EnvironmentEvents");
                        registerEvents("cn.lunadeer.dominion.v1_21_paper.events.SelectPointEvents");
                        registerEvents("cn.lunadeer.dominion.v1_21_paper.scui.CuiEvents");
                    } else {
                        XLogger.debug("Load Spigot special events");
                        registerEvents("cn.lunadeer.dominion.v1_21_spigot.events.PlayerEvents");
                        registerEvents("cn.lunadeer.dominion.v1_21_spigot.events.EnvironmentEvents");
                        registerEvents("cn.lunadeer.dominion.v1_21_spigot.events.SelectPointEvents");
                    }
                    break;
                case v1_20_1:
                    XLogger.debug("Load API version: 1.20.1");
                    registerEvents("cn.lunadeer.dominion.v1_20_1.events.PlayerEvents");
                    registerEvents("cn.lunadeer.dominion.v1_20_1.events.EnvironmentEvents");
                    registerEvents("cn.lunadeer.dominion.v1_20_1.events.SelectPointEvents");
                    registerEvents("cn.lunadeer.dominion.v1_20_1.scui.CuiEvents");
                    if (isPaper()) {
                        registerEvents("cn.lunadeer.dominion.v1_20_1.events.special.Paper");
                    } else {
                        registerEvents("cn.lunadeer.dominion.v1_20_1.events.special.Spigot");
                    }
                    break;
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            XLogger.error("Failed to register events: %s", e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        new DominionEventHandler(plugin);
        new MemberEventHandler(plugin);
        new GroupEventHandler(plugin);
    }

    public enum APIVersion {
        v1_21,
        v1_20_1
    }

    public static APIVersion GetAPIVersion(JavaPlugin plugin) {
        String version = plugin.getServer().getBukkitVersion();
        XLogger.debug("API version: %s", version);
        if (version.contains("1.21")) {
            return APIVersion.v1_21;
        } else if (version.contains("1.20.1")
                || version.contains("1.20.4")
                || version.contains("1.20.6")) {
            return APIVersion.v1_20_1;
        }
        XLogger.error("Unsupported API version: %s", version);
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        return null;
    }

    public void registerEvents(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz = Class.forName(className);
        Listener listener = (Listener) clazz.newInstance();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

}
