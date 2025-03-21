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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.utils.Misc.isPaper;

public class EventsRegister {

    private final JavaPlugin plugin;
    public XVersionManager.ImplementationVersion version;
    public Implementation implementation;

    public enum Implementation {
        SPIGOT,
        PAPER
    }

    public EventsRegister(JavaPlugin plugin) {
        this.plugin = plugin;
        this.version = XVersionManager.VERSION;
        this.implementation = isPaper() ? Implementation.PAPER : Implementation.SPIGOT;
        try {
            XLogger.debug("Load API version: {0}:{1}", implementation.name().toLowerCase(), version.name());
            registerVersion();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            XLogger.error("Failed to register events: {0}", e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }

        new DominionEventHandler(plugin);
        new MemberEventHandler(plugin);
        new GroupEventHandler(plugin);
        new SelectPointEventsHandler(plugin);
    }

    public void registerEvents(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = Class.forName(className);
        if (!Listener.class.isAssignableFrom(clazz)) {
            return;
        }
        if (implementation == Implementation.PAPER && clazz.isAnnotationPresent(SpigotOnly.class)) {
            XLogger.debug("Skipping {0} because it is Spigot only", className);
            return;
        }
        if (implementation == Implementation.SPIGOT && clazz.isAnnotationPresent(PaperOnly.class)) {
            XLogger.debug("Skipping {0} because it is Paper only", className);
            return;
        }
        if (clazz.isAnnotationPresent(HighestVersion.class)) {
            HighestVersion highestVersion = clazz.getAnnotation(HighestVersion.class);
            if (highestVersion.value().compareWith(version) < 0) {
                XLogger.debug("Skipping {0} because it is for a lower version", className);
                return;
            }
        }
        if (clazz.isAnnotationPresent(LowestVersion.class)) {
            LowestVersion lowestVersion = clazz.getAnnotation(LowestVersion.class);
            if (lowestVersion.value().compareWith(version) > 0) {
                XLogger.debug("Skipping {0} because it is for a higher version", className);
                return;
            }
        }
        Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public void registerPackageEvents(String packageName) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        List<String> classesInPackage = new ArrayList<>();
        // list all classes in the packageName package
        String path = packageName.replace('.', '/');
        URL packageDir = plugin.getClass().getClassLoader().getResource(path);
        if (packageDir == null) {
            return;
        }
        String packageDirPath = packageDir.getPath();
        // if the package is in a jar file, unpack it and list the classes
        packageDirPath = packageDirPath.substring(0, packageDirPath.indexOf("jar!") + 4);
        packageDirPath = packageDirPath.replace("file:", "");
        packageDirPath = packageDirPath.replace("!", "");
        // unpack the jar file
        XLogger.debug("Registering events in jar: {0}", packageDirPath);
        File jarFile = new File(packageDirPath);
        if (!jarFile.exists() || !jarFile.isFile()) {
            XLogger.debug("Skipping {0} because it is not a jar file", packageDirPath);
            return;
        }
        // list the classes in the jar file
        try (java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile)) {
            jar.stream().filter(entry -> entry.getName().endsWith(".class") && entry.getName().startsWith(path))
                    .forEach(entry -> classesInPackage.add(entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6)));
        } catch (Exception e) {
            XLogger.debug("Failed to list classes in jar: {0}", e.getMessage());
            return;
        }

        for (String className : classesInPackage) {
            XLogger.debug("Registering event: {0}", className);
            registerEvents(className);
        }
    }

    private void registerVersion() throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        for (XVersionManager.ImplementationVersion v : XVersionManager.ImplementationVersion.values()) {
            String packageName = "cn.lunadeer.dominion." + v.name() + ".";
            registerPackageEvents(packageName + "events.player");
            registerPackageEvents(packageName + "events.environment");

            if (implementation == Implementation.PAPER) registerEvents(packageName + "scui.CuiEvents");
        }
    }

}
