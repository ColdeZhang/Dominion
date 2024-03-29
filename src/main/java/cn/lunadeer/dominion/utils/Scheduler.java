package cn.lunadeer.dominion.utils;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public class Scheduler {
    public Scheduler(JavaPlugin plugin) {
        region = plugin.getServer().getGlobalRegionScheduler();
        async = plugin.getServer().getAsyncScheduler();
    }

    public GlobalRegionScheduler region;
    public AsyncScheduler async;
}
