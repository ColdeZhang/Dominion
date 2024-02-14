package cn.lunadeer.dominion;

import cn.lunadeer.dominion.events.EnvironmentEvents;
import cn.lunadeer.dominion.events.PlayerEvents;
import cn.lunadeer.dominion.utils.ConfigManager;
import cn.lunadeer.dominion.utils.Database;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.*;

public final class Dominion extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        config = new ConfigManager(this);
        dbConnection = Database.createConnection();
        Database.migrate();
        Cache.instance = new Cache();

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
        Bukkit.getPluginManager().registerEvents(new EnvironmentEvents(), this);
        Objects.requireNonNull(Bukkit.getPluginCommand("dominion")).setExecutor(new Commands());

        XLogger.info("领地插件已启动");
        XLogger.info("版本：" + this.getPluginMeta().getVersion());
        // http://patorjk.com/software/taag/#p=display&f=Big&t=Dominion
        XLogger.info("  _____                  _       _");
        XLogger.info(" |  __ \\                (_)     (_)");
        XLogger.info(" | |  | | ___  _ __ ___  _ _ __  _  ___  _ __");
        XLogger.info(" | |  | |/ _ \\| '_ ` _ \\| | '_ \\| |/ _ \\| '_ \\");
        XLogger.info(" | |__| | (_) | | | | | | | | | | | (_) | | | |");
        XLogger.info(" |_____/ \\___/|_| |_| |_|_|_| |_|_|\\___/|_| |_|");
        XLogger.info(" ");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Dominion instance;
    public static ConfigManager config;
    public static Connection dbConnection;
    public static Map<UUID, List<Location>> pointsSelect = new HashMap<>();
}
