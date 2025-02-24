package cn.lunadeer.dominion.utils.stui;

import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.Pagination;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ListView {

    private final Integer page_size;
    private final List<Line> lines = new ArrayList<>();
    private ListViewButton command = null;
    private final View view = View.create();

    private ListView(int page_size, ListViewButton command) {
        super();
        this.page_size = page_size;
        this.command = command;
    }

    public static ListView create(int page_size, ListViewButton command) {
        return new ListView(page_size, command);
    }

    public ListView title(String title) {
        view.title(title);
        return this;
    }

    public ListView title(TextComponent title) {
        view.title(title);
        return this;
    }

    public ListView title(String title, String subtitle) {
        view.title(title);
        view.subtitle(subtitle);
        return this;
    }

    public ListView subtitle(String subtitle) {
        view.subtitle(subtitle);
        return this;
    }

    public ListView subtitle(TextComponent subtitle) {
        view.subtitle(subtitle);
        return this;
    }

    public ListView subtitle(Line line) {
        view.subtitle(line);
        return this;
    }

    public ListView add(Line line) {
        lines.add(line);
        return this;
    }

    public ListView addLines(List<Line> lines) {
        this.lines.addAll(lines);
        return this;
    }

    public void showOn(CommandSender player, Integer page) {
        int offset = (page - 1) * page_size;
        if (lines.isEmpty()) {
            lines.add(Line.create().append("..."));
        }
        if (offset > lines.size() || offset < 0) {
            offset = 0;
            page = 1;
        }
        for (int i = offset; i < offset + page_size; i++) {
            if (i >= lines.size()) {
                for (int j = 0; j < page_size - lines.size() % page_size; j++) {
                    view.addLine(Line.create());
                }
                break;
            }
            view.addLine(lines.get(i));
        }
        view.actionBar(Pagination.create(page, lines.size(), page_size, this.command));
        view.showOn(player);
    }

    public ListView navigator(Line line) {
        Line nav = Line.create().setDivider("->").append(Component.text("\uD83E\uDDED", ViewStyles.main_color));
        for (Component component : line.getElements()) {
            nav.append(component);
        }
        view.subtitle(nav);
        return this;
    }
}
