package cn.lunadeer.dominion.utils.stui;

import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.stui.components.Line;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.utils.stui.ViewStyles.main_color;

public class View {
    protected TextComponent space = Component.text(" ");
    protected TextComponent sub_title_decorate = Component.text("- ", main_color);
    protected TextComponent line_decorate = Component.text("⌗ ", main_color);
    protected TextComponent action_decorate = Component.text("▸ ", main_color);
    protected TextComponent title = Component.text("       ");
    protected TextComponent subtitle = null;
    protected List<TextComponent> content_lines = new ArrayList<>();
    protected TextComponent actionbar = null;
    protected TextComponent edge = Component.text("                                                               ", Style.style(main_color, TextDecoration.STRIKETHROUGH));
    protected TextComponent divide_line = Component.text("                                                               ", Style.style(main_color, TextDecoration.STRIKETHROUGH));

    public void showOn(CommandSender player) {
        // player.sendMessage(edge);
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("__/", main_color));
        builder.append(space).append(title).append(space);
        builder.append(Component.text("\\__", main_color));
        Notification.instance.sender.sendMessage(player, builder.build());
        if (subtitle != null) {
            Notification.instance.sender.sendMessage(player, divide_line);
            Notification.instance.sender.sendMessage(player, Component.text().append(sub_title_decorate).append(subtitle).build());
        }
        Notification.instance.sender.sendMessage(player, divide_line);
        for (TextComponent content_line : content_lines) {
            Notification.instance.sender.sendMessage(player, Component.text().append(line_decorate).append(content_line).build());
        }
        if (actionbar != null) {
            Notification.instance.sender.sendMessage(player, divide_line);
            Notification.instance.sender.sendMessage(player, Component.text().append(action_decorate).append(actionbar).build());
        }
        Notification.instance.sender.sendMessage(player, edge);
        Notification.instance.sender.sendMessage(player, Component.text("     "));
    }

    public static View create() {
        return new View();
    }

    public View title(String title) {
        this.title = Component.text(title);
        return this;
    }

    public View title(TextComponent title) {
        this.title = title;
        return this;
    }


    public View subtitle(String subtitle) {
        this.subtitle = Component.text(subtitle);
        return this;
    }


    public View subtitle(Line line) {
        this.subtitle = line.build();
        return this;
    }

    public View navigator(Line line) {
        Line nav = Line.create().setDivider("->").append(Component.text("\uD83E\uDDED", main_color));
        for (Component component : line.getElements()) {
            nav.append(component);
        }
        return this.subtitle(nav);
    }

    public View subtitle(TextComponent subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public View actionBar(TextComponent actionbar) {
        this.actionbar = actionbar;
        return this;
    }

    public View actionBar(String actionbar) {
        this.actionbar = Component.text(actionbar);
        return this;
    }

    public View actionBar(Line actionbar) {
        this.actionbar = actionbar.build();
        return this;
    }

    public View addLine(TextComponent component) {
        this.content_lines.add(component);
        return this;
    }

    public View addLine(String component) {
        this.content_lines.add(Component.text(component));
        return this;
    }

    public View addLine(Line component) {
        this.content_lines.add(component.build());
        return this;
    }
}
