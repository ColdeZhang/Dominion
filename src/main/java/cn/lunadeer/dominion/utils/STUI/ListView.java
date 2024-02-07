package cn.lunadeer.dominion.utils.STUI;

import cn.lunadeer.dominion.utils.Notification;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListView {

    private final Integer page_size;
    private final List<Line> lines = new ArrayList<>();
    private String command = "";
    private final View view = View.create();

    private ListView(int page_size, String command) {
        super();
        this.page_size = page_size;
        this.command = command;
    }

    public static ListView create(int page_size, String command) {
        return new ListView(page_size, command);
    }

    public ListView title(String title) {
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

    public void showOn(Player player, Integer page) {
        int offset = (page - 1) * page_size;
        if (lines.isEmpty()) {
            Notification.warn(player, "没有数据");
            return;
        }
        if (offset >= lines.size() || offset < 0) {
            Notification.error(player, "页数超出范围");
            return;
        }
        for (int i = offset; i < offset + page_size; i++) {
            if (i >= lines.size()) {
                break;
            }
            view.addLine(lines.get(i));
        }
        view.actionBar(Pagination.create(page, lines.size(), this.command));
        view.showOn(player);
    }
}
