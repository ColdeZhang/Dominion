package cn.lunadeer.dominion.utils;

import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}
