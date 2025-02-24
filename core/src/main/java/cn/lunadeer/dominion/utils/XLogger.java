package cn.lunadeer.dominion.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

import static cn.lunadeer.dominion.utils.Misc.formatString;

public class XLogger {
    public static XLogger instance;

    public XLogger() {
        instance = this;
        this._logger = Logger.getLogger("Lunadeer");
    }

    public XLogger(@Nullable JavaPlugin plugin) {
        instance = this;
        this._logger = plugin != null ? plugin.getLogger() : Logger.getLogger("Lunadeer");
    }

    public static XLogger setDebug(boolean debug) {
        instance._debug = debug;
        return instance;
    }

    public static boolean isDebug() {
        return instance._debug;
    }

    private final Logger _logger;
    private boolean _debug = false;

    public static void info(String message) {
        instance._logger.info(" I | " + message);
    }

    public static void info(String message, Object... args) {
        instance._logger.info(" I | " + formatString(message, args));
    }

    public static void warn(String message) {
        instance._logger.warning(" W | " + message);
    }

    public static void warn(String message, Object... args) {
        instance._logger.warning(" W | " + formatString(message, args));
    }

    public static void error(String message) {
        instance._logger.severe(" E | " + message);
    }

    public static void error(String message, Object... args) {
        instance._logger.severe(" E | " + formatString(message, args));
    }

    public static void debug(String message) {
        if (!instance._debug) return;
        instance._logger.info(" D | " + message);
    }

    public static void debug(String message, Object... args) {
        if (!instance._debug) return;
        instance._logger.info(" D | " + formatString(message, args));
    }
}
