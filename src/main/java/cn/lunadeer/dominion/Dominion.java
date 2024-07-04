package cn.lunadeer.dominion;

import cn.lunadeer.dominion.events.EnvironmentEvents;
import cn.lunadeer.dominion.events.PlayerEvents;
import cn.lunadeer.dominion.events.SelectPointEvents;
import cn.lunadeer.dominion.managers.ConfigManager;
import cn.lunadeer.dominion.managers.DatabaseTables;
import cn.lunadeer.minecraftpluginutils.*;
import cn.lunadeer.minecraftpluginutils.databse.DatabaseManager;
import cn.lunadeer.minecraftpluginutils.databse.DatabaseType;
import cn.lunadeer.minecraftpluginutils.scui.CuiManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class Dominion extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        new Notification(this);
        new XLogger(this);
        config = new ConfigManager(this);
        new DatabaseManager(this,
                DatabaseType.valueOf(config.getDbType().toUpperCase()),
                config.getDbHost(),
                config.getDbPort(),
                config.getDbName(),
                config.getDbUser(),
                config.getDbPass());
        DatabaseTables.migrate();
        new Scheduler(this);
        AutoClean.run();
        Cache.instance = new Cache();

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
        Bukkit.getPluginManager().registerEvents(new EnvironmentEvents(), this);
        Bukkit.getPluginManager().registerEvents(new SelectPointEvents(), this);
        Objects.requireNonNull(Bukkit.getPluginCommand("dominion")).setExecutor(new Commands());

        bStatsMetrics metrics = new bStatsMetrics(this, 21445);
        metrics.addCustomChart(new bStatsMetrics.SimplePie("database", () -> config.getDbType()));

        if (config.getCheckUpdate()) {
            giteaReleaseCheck = new GiteaReleaseCheck(this,
                    "https://ssl.lunadeer.cn:14446",
                    "zhangyuheng",
                    "Dominion");
        }

        // SCUI 初始化
        Bukkit.getPluginManager().registerEvents(new CuiManager(this), this);

        XLogger.info("领地插件已启动");
        XLogger.info("版本：" + this.getDescription().getVersion());
        // http://patorjk.com/software/taag/#p=display&f=Big&t=Dominion
        XLogger.info("  _____                  _       _");
        XLogger.info(" |  __ \\                (_)     (_)");
        XLogger.info(" | |  | | ___  _ __ ___  _ _ __  _  ___  _ __");
        XLogger.info(" | |  | |/ _ \\| '_ ` _ \\| | '_ \\| |/ _ \\| '_ \\");
        XLogger.info(" | |__| | (_) | | | | | | | | | | | (_) | | | |");
        XLogger.info(" |_____/ \\___/|_| |_| |_|_|_| |_|_|\\___/|_| |_|");
        XLogger.info(" ");

        Scheduler.runTaskLaterAsync(BlueMapConnect::render, 40 * 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        DatabaseManager.instance.close();
    }

    public static Dominion instance;
    public static ConfigManager config;
    public static Map<UUID, Map<Integer, Location>> pointsSelect = new HashMap<>();
    private GiteaReleaseCheck giteaReleaseCheck;
}
