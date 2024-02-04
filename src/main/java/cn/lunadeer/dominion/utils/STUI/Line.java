package cn.lunadeer.dominion.utils.STUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;


public class Line {
    private final List<Component> elements = new ArrayList<>();

    private final TextComponent divider = Component.text(" - ", ViewStyles.sub_color);

    public Line() {
    }

    public TextComponent build() {
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

    public Line append(TextComponent component) {
        elements.add(component);
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
