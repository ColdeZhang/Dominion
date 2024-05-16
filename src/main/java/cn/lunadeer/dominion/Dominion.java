package cn.lunadeer.dominion;

import cn.lunadeer.dominion.events.EnvironmentEvents;
import cn.lunadeer.dominion.events.PlayerEvents;
import cn.lunadeer.dominion.events.SelectPointEvents;
import cn.lunadeer.dominion.managers.ConfigManager;
import cn.lunadeer.dominion.managers.DatabaseTables;
import cn.lunadeer.dominion.utils.Scheduler;
import cn.lunadeer.minecraftpluginutils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class Dominion extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        notification = new Notification(this);
        logger = new XLogger(this);
        config = new ConfigManager(this);
        database = new DatabaseManager(this,
                config.getDbType().equals("pgsql") ? DatabaseManager.TYPE.POSTGRESQL : DatabaseManager.TYPE.SQLITE,
                config.getDbHost(),
                config.getDbPort(),
                config.getDbName(),
                config.getDbUser(),
                config.getDbPass());
        DatabaseTables.migrate();
        scheduler = new Scheduler(this);
        AutoClean.run();
        Cache.instance = new Cache();

        if (config.getEconomyEnable()) {
            vault = new VaultConnect(this);
            if (vault.getEconomy() == null) {
                logger.err("你没有安装 Vault 前置插件，无法使用经济功能。");
                config.setEconomyEnable(false);
            }
        }

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
        Bukkit.getPluginManager().registerEvents(new EnvironmentEvents(), this);
        Bukkit.getPluginManager().registerEvents(new SelectPointEvents(), this);
        Objects.requireNonNull(Bukkit.getPluginCommand("dominion")).setExecutor(new Commands());

        bStatsMetrics metrics = new bStatsMetrics(this, 21445);
        if (config.getCheckUpdate()) {
            giteaReleaseCheck = new GiteaReleaseCheck(this,
                    "https://ssl.lunadeer.cn:14446",
                    "zhangyuheng",
                    "Dominion");
        }

        logger.info("领地插件已启动");
        logger.info("版本：" + this.getPluginMeta().getVersion());
        // http://patorjk.com/software/taag/#p=display&f=Big&t=Dominion
        logger.info("  _____                  _       _");
        logger.info(" |  __ \\                (_)     (_)");
        logger.info(" | |  | | ___  _ __ ___  _ _ __  _  ___  _ __");
        logger.info(" | |  | |/ _ \\| '_ ` _ \\| | '_ \\| |/ _ \\| '_ \\");
        logger.info(" | |__| | (_) | | | | | | | | | | | (_) | | | |");
        logger.info(" |_____/ \\___/|_| |_| |_|_|_| |_|_|\\___/|_| |_|");
        logger.info(" ");

        scheduler.async.runDelayed(this, scheduledTask -> {
            BlueMapConnect.render();
        }, 40, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Dominion instance;
    public static ConfigManager config;
    public static XLogger logger;
    public static Notification notification;
    public static DatabaseManager database;
    public static Map<UUID, Map<Integer, Location>> pointsSelect = new HashMap<>();
    public static Scheduler scheduler;
    private GiteaReleaseCheck giteaReleaseCheck;
    public static VaultConnect vault;
}
