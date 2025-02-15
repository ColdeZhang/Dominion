package cn.lunadeer.dominion.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

import static cn.lunadeer.dominion.utils.Misc.isPaper;

public class Scheduler {
    public static Scheduler instance;
    private final JavaPlugin plugin;
    private boolean isPaper = false;

    public Scheduler(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.isPaper = isPaper();
    }

    public static void cancelAll() {
        if (instance.isPaper) {
            instance.plugin.getServer().getGlobalRegionScheduler().cancelTasks(instance.plugin);
            instance.plugin.getServer().getGlobalRegionScheduler().cancelTasks(instance.plugin);
        } else {
            instance.plugin.getServer().getScheduler().cancelTasks(instance.plugin);
        }
    }

    /**
     * Run a task later
     *
     * @param task  The task to run
     * @param delay The delay in ticks (20 ticks = 1 second)
     */
    public static void runTaskLater(Runnable task, long delay) {
        if (delay <= 0) {
            runTask(task);
            return;
        }
        if (instance.isPaper) {
            instance.plugin.getServer().getGlobalRegionScheduler().runDelayed(instance.plugin, (plugin) -> task.run(), delay);
        } else {
            instance.plugin.getServer().getScheduler().runTaskLater(instance.plugin, task, delay);
        }
    }

    /**
     * Run a task
     *
     * @param task The task to run
     */
    public static void runTask(Runnable task) {
        if (instance.isPaper) {
            instance.plugin.getServer().getGlobalRegionScheduler().run(instance.plugin, (plugin) -> task.run());
        } else {
            instance.plugin.getServer().getScheduler().runTask(instance.plugin, task);
        }
    }

    /**
     * Run a task repeatedly
     *
     * @param task   The task to run
     * @param delay  The delay in ticks (20 ticks = 1 second)
     * @param period The period in ticks (20 ticks = 1 second)
     */
    public static void runTaskRepeat(Runnable task, long delay, long period) {
        if (instance.isPaper) {
            instance.plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(instance.plugin, (plugin) -> task.run(), delay, period);
        } else {
            instance.plugin.getServer().getScheduler().runTaskTimer(instance.plugin, task, delay, period);
        }
    }

    /**
     * Run a task later asynchronously
     *
     * @param task  The task to run
     * @param delay The delay in ticks (20 ticks = 1 second)
     */
    public static void runTaskLaterAsync(Runnable task, long delay) {
        if (delay <= 0) {
            runTaskAsync(task);
            return;
        }
        if (instance.isPaper) {
            instance.plugin.getServer().getAsyncScheduler().runDelayed(instance.plugin, (plugin) -> task.run(), delay * 50, TimeUnit.MILLISECONDS);
        } else {
            instance.plugin.getServer().getScheduler().runTaskLaterAsynchronously(instance.plugin, task, delay);
        }
    }

    /**
     * Run a task asynchronously
     *
     * @param task The task to run
     */
    public static void runTaskAsync(Runnable task) {
        if (instance.isPaper) {
            instance.plugin.getServer().getAsyncScheduler().runNow(instance.plugin, (plugin) -> task.run());
        } else {
            instance.plugin.getServer().getScheduler().runTaskAsynchronously(instance.plugin, task);
        }
    }

    /**
     * Run a task repeatedly asynchronously
     *
     * @param task   The task to run
     * @param delay  The delay in ticks (20 ticks = 1 second)
     * @param period The period in ticks (20 ticks = 1 second)
     */
    public static void runTaskRepeatAsync(Runnable task, long delay, long period) {
        if (instance.isPaper) {
            instance.plugin.getServer().getAsyncScheduler().runAtFixedRate(instance.plugin, (plugin) -> task.run(), delay * 50, period * 50, TimeUnit.MILLISECONDS);
        } else {
            instance.plugin.getServer().getScheduler().runTaskTimerAsynchronously(instance.plugin, task, delay, period);
        }
    }
}
