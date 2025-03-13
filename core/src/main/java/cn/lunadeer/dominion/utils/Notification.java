package cn.lunadeer.dominion.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static cn.lunadeer.dominion.utils.Misc.formatString;
import static cn.lunadeer.dominion.utils.XLogger.isDebug;

public class Notification {
    public static Notification instance;

    public final SendMessageAbstract sender;

    public Notification(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.prefix = "[" + plugin.getName() + "]";
        this.sender = new SendMessageAbstract(plugin);
    }

    private static final Style i_style = Style.style(TextColor.color(139, 255, 123));
    private static final Style w_style = Style.style(TextColor.color(255, 185, 69));
    private static final Style e_style = Style.style(TextColor.color(255, 96, 72));
    private static final Style d_style = Style.style(TextColor.color(0, 255, 255));

    private String prefix;
    private JavaPlugin plugin;

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public static void info(Player player, String msg) {
        instance.sender.sendMessage(player, Component.text(instance.prefix + " " + msg, i_style));
    }

    public static void info(Player player, String msg, Object... args) {
        instance.sender.sendMessage(player, Component.text(instance.prefix + " " + formatString(msg, args), i_style));
    }

    public static void warn(Player player, String msg) {
        instance.sender.sendMessage(player, Component.text(instance.prefix + " " + msg, w_style));
    }

    public static void warn(Player player, String msg, Object... args) {
        instance.sender.sendMessage(player, Component.text(instance.prefix + " " + formatString(msg, args), w_style));
    }


    public static void error(Player player, String msg) {
        instance.sender.sendMessage(player, Component.text(instance.prefix + " " + msg, e_style));
    }

    public static void error(Player player, String msg, Object... args) {
        instance.sender.sendMessage(player, Component.text(instance.prefix + " " + formatString(msg, args), e_style));
    }

    public static void info(CommandSender sender, String msg) {
        instance.sender.sendMessage(sender, Component.text(instance.prefix + " " + msg, i_style));
    }

    public static void info(CommandSender sender, String msg, Object... args) {
        instance.sender.sendMessage(sender, Component.text(instance.prefix + " " + formatString(msg, args), i_style));
    }

    public static void warn(CommandSender sender, String msg) {
        instance.sender.sendMessage(sender, Component.text(instance.prefix + " " + msg, w_style));
    }

    public static void warn(CommandSender sender, String msg, Object... args) {
        instance.sender.sendMessage(sender, Component.text(instance.prefix + " " + formatString(msg, args), w_style));
    }

    public static void error(CommandSender sender, String msg) {
        instance.sender.sendMessage(sender, Component.text(instance.prefix + " " + msg, e_style));
    }

    public static void error(CommandSender sender, String msg, Object... args) {
        instance.sender.sendMessage(sender, Component.text(instance.prefix + " " + formatString(msg, args), e_style));
    }

    public static void info(Player player, Component msg) {
        instance.sender.sendMessage(player, Component.text(instance.prefix, i_style).append(Component.text(" ")).append(msg));
    }

    public static void warn(Player player, Component msg) {
        instance.sender.sendMessage(player, Component.text(instance.prefix, w_style).append(Component.text(" ")).append(msg));
    }

    public static void error(Player player, Component msg) {
        instance.sender.sendMessage(player, Component.text(instance.prefix, e_style).append(Component.text(" ")).append(msg));
    }

    public static void info(CommandSender player, Component msg) {
        instance.sender.sendMessage(player, Component.text(instance.prefix, i_style).append(Component.text(" ")).append(msg));
    }

    public static void warn(CommandSender player, Component msg) {
        instance.sender.sendMessage(player, Component.text(instance.prefix, w_style).append(Component.text(" ")).append(msg));
    }

    public static void error(CommandSender player, Component msg) {
        instance.sender.sendMessage(player, Component.text(instance.prefix, e_style).append(Component.text(" ")).append(msg));
    }

    public static void error(CommandSender player, Throwable e) {
        Notification.error(player, e.getMessage());
        if (isDebug()) {
            XLogger.error(e);
        }
    }

    public static void all(String msg) {
        instance.sender.broadcast(Component.text(instance.prefix + " " + msg, i_style));
    }

    public static void all(Component msg) {
        instance.sender.broadcast(Component.text(instance.prefix, i_style).append(Component.text(" ")).append(msg));
    }

    public static void all(String msg, Object... args) {
        instance.sender.broadcast(Component.text(instance.prefix + " " + formatString(msg, args), i_style));
    }

    public static void actionBar(Player player, String msg) {
        instance.sender.sendActionBar(player, Component.text(msg));
    }

    public static void actionBar(Player player, String msg, Object... args) {
        instance.sender.sendActionBar(player, Component.text(formatString(msg, args)));
    }

    public static void actionBar(Player player, Component msg) {
        instance.sender.sendActionBar(player, msg);
    }

    public static void title(Player player, String title) {
        instance.sender.sendTitle(player, Component.text(title), Component.empty());
    }

    public static void title(Player player, String title, String subtitle) {
        instance.sender.sendTitle(player, Component.text(title), Component.text(subtitle));
    }

    public static void title(Player player, Component title) {
        instance.sender.sendTitle(player, title, Component.empty());
    }

    public static void subTitle(Player player, Component subtitle) {
        instance.sender.sendTitle(player, Component.empty(), subtitle);
    }

    public static void subTitle(Player player, String subtitle) {
        instance.sender.sendTitle(player, Component.empty(), Component.text(subtitle));
    }

    public static void bossBar(Player player, String message) {
        instance.sender.sendBossBar(player, Component.text(message));
    }

    public static void bossBar(Player player, Component message) {
        instance.sender.sendBossBar(player, message);
    }

}
