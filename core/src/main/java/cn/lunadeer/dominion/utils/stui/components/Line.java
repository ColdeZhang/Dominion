package cn.lunadeer.dominion.utils.stui.components;

import cn.lunadeer.dominion.utils.stui.ViewStyles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;


public class Line {
    private String d = " - ";
    private final List<Component> elements = new ArrayList<>();

    public Line() {
    }

    public TextComponent build() {
        TextComponent divider = Component.text(d, ViewStyles.sub_color);
        TextComponent.Builder builder = Component.text();
        for (int i = 0; i < elements.size(); i++) {
            builder.append(elements.get(i));
            if (i != elements.size() - 1) {
                builder.append(divider);
            }
        }
        return builder.build();
    }

    public static Line create() {
        return new Line();
    }

    public List<Component> getElements() {
        return elements;
    }

    public Line append(TextComponent component) {
        elements.add(component);
        return this;
    }

    public Line setDivider(String d) {
        this.d = d;
        return this;
    }

    public Line append(Component component) {
        elements.add(component);
        return this;
    }

    public Line append(String component) {
        elements.add(Component.text(component));
        return this;
    }

}
