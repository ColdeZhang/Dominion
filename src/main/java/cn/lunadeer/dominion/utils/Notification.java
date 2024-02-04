package cn.lunadeer.dominion.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Notification {
    private static final Style i_style = Style.style(TextColor.color(139, 255, 123));
    private static final Style w_style = Style.style(TextColor.color(255, 185, 69));
    private static final Style e_style = Style.style(TextColor.color(255, 96, 72));

    private static final String prefix = "[Dominion] ";

    public static void info(Player player, String msg) {
        player.sendMessage(Component.text(prefix + msg, i_style));
    }

    public static void warn(Player player, String msg) {
        player.sendMessage(Component.text(prefix + msg, w_style));
    }

    public static void error(Player player, String msg) {
        player.sendMessage(Component.text(prefix + msg, e_style));
    }

    public static void info(CommandSender sender, String msg) {
        sender.sendMessage(Component.text(prefix + msg, i_style));
    }

    public static void warn(CommandSender sender, String msg) {
        sender.sendMessage(Component.text(prefix + msg, w_style));
    }

    public static void error(CommandSender sender, String msg) {
        sender.sendMessage(Component.text(prefix + msg, e_style));
    }

    public static void info(Player player, Component msg) {
        player.sendMessage(Component.text(prefix, i_style).append(msg));
    }

    public static void warn(Player player, Component msg) {
        player.sendMessage(Component.text(prefix, w_style).append(msg));
    }

    public static void error(Player player, Component msg) {
        player.sendMessage(Component.text(prefix, e_style).append(msg));
    }

    public static void info(CommandSender player, Component msg) {
        player.sendMessage(Component.text(prefix, i_style).append(msg));
    }

    public static void warn(CommandSender player, Component msg) {
        player.sendMessage(Component.text(prefix, w_style).append(msg));
    }

    public static void error(CommandSender player, Component msg) {
        player.sendMessage(Component.text(prefix, e_style).append(msg));
    }
}
