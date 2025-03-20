package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.handler.DominionEventHandler;
import cn.lunadeer.dominion.handler.GroupEventHandler;
import cn.lunadeer.dominion.handler.MemberEventHandler;
import cn.lunadeer.dominion.handler.SelectPointEventsHandler;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.XVersionManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static cn.lunadeer.dominion.utils.Misc.isPaper;

public class EventsRegister {

    private JavaPlugin plugin;

    public EventsRegister(JavaPlugin plugin) {
        this.plugin = plugin;
        try {
            registerVersion(XVersionManager.VERSION, isPaper() ? Implementation.PAPER : Implementation.SPIGOT);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            XLogger.error("Failed to register events: {0}", e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        new DominionEventHandler(plugin);
        new MemberEventHandler(plugin);
        new GroupEventHandler(plugin);
        new SelectPointEventsHandler(plugin);
    }

    public void registerEvents(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz = Class.forName(className);
        Listener listener = (Listener) clazz.newInstance();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    private enum Implementation {
        SPIGOT,
        PAPER
    }

    private void registerVersion(XVersionManager.ImplementationVersion version, Implementation implementation) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        XLogger.debug("Load API version: {0}:{1}", implementation.name().toLowerCase(), version.name().toLowerCase());
        String packageName = "cn.lunadeer.dominion." + version.name().toLowerCase() + "_" + implementation.name().toLowerCase() + ".";
        if (implementation == Implementation.PAPER) {
            registerEvents(packageName + "scui.CuiEvents");
        }
        registerEvents(packageName + "events.PlayerEvents");
        registerEvents(packageName + "events.EnvironmentEvents");
        registerEvents(packageName + "events.PlayerPVPEvents");
    }

}
