package cn.lunadeer.dominion.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Time {

    public static String nowStr() {
        // yyyy-MM-dd HH:mm:ss
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * 尝试获取folia的调度器
     *
     * @return 是否成功
     */
    private static boolean tryFolia() {
        try {
            Bukkit.getAsyncScheduler();
            return true;
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static Boolean IS_FOLIA = null;

    /**
     * 判断是否是folia核心
     *
     * @return 是否是folia核心
     */
    public static Boolean isFolia() {
        if (IS_FOLIA == null) IS_FOLIA = tryFolia();
        return IS_FOLIA;
    }

    /**
     * 定时异步任务
     *
     * @param plugin   插件
     * @param runnable 任务
     * @param ticks    间隔
     */
    public static void runAtFixedRateAsync(Plugin plugin, Runnable runnable, int ticks) {
        if (isFolia())
            Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (task) -> runnable.run(), ticks / 20, ticks / 20, TimeUnit.SECONDS);
        else Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, ticks, ticks);
    }

    public static void runLater(Plugin plugin, Runnable runnable, int ticks) {
        if (isFolia())
            Bukkit.getAsyncScheduler().runDelayed(plugin, (task) -> runnable.run(), ticks / 20, TimeUnit.SECONDS);
        else Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks);
    }
}
