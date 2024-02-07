package cn.lunadeer.dominion.utils.STUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.utils.STUI.ViewStyles.main_color;

public class View {
    protected TextComponent title_decorate = Component.text("━", main_color);
    protected TextComponent space = Component.text(" ");
    protected TextComponent sub_title_decorate = Component.text("-   ", main_color);
    protected TextComponent line_decorate = Component.text("⌗ ", main_color);
    protected TextComponent action_decorate = Component.text("▸ ", main_color);
    protected TextComponent title = Component.text("       ");
    protected TextComponent subtitle = null;
    protected List<TextComponent> content_lines = new ArrayList<>();
    protected TextComponent actionbar = null;
    protected TextComponent edge = Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", main_color);
    protected TextComponent divide_line = Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", main_color);

    public void showOn(Player player) {
        player.sendMessage(edge);
        TextComponent.Builder builder = Component.text();
        int title_length = title.content().length();
        int title_width = title_length * 2 + 2;
        int decorate_count = divide_line.content().length() - title_width;
        for (int i = 0; i < decorate_count / 2; i++) {
            builder.append(title_decorate);
        }
        builder.append(space).append(title).append(space);
        for (int i = 0; i < decorate_count / 2; i++) {
            builder.append(title_decorate);
        }
        player.sendMessage(builder.build());
        if (subtitle != null) {
            player.sendMessage(divide_line);
            player.sendMessage(Component.text().append(sub_title_decorate).append(subtitle).build());
        }
        player.sendMessage(divide_line);
        for (TextComponent content_line : content_lines) {
            player.sendMessage(Component.text().append(line_decorate).append(content_line).build());
        }
        if (actionbar != null) {
            player.sendMessage(divide_line);
            player.sendMessage(Component.text().append(action_decorate).append(actionbar).build());
        }
        player.sendMessage(edge);
        player.sendMessage(Component.text("     "));
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
        Line nav = Line.create().setDivider("->").append(Component.text("导航", ViewStyles.main_color));
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
