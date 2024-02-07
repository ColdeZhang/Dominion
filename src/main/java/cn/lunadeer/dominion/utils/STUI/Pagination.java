package cn.lunadeer.dominion.utils.STUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.utils.STUI.ViewStyles.main_color;
import static cn.lunadeer.dominion.utils.STUI.ViewStyles.sub_color;


public class Pagination {
    public static TextComponent create(int page, int item_size, int page_size, String command) {
        // 第 x/y 页 [上一页] [下一页]
        int page_count = (int) Math.ceil((double) item_size / page_size);
        if (page_count == 0) {
            page_count = 1;
        }
        List<Component> componentList = new ArrayList<>();
        componentList.add(Component.text("第 ", main_color));
        componentList.add(Component.text(page, sub_color));
        componentList.add(Component.text("/", main_color));
        componentList.add(Component.text(page_count, sub_color));
        componentList.add(Component.text(" 页 ", main_color));
        if (page > 1) {
            componentList.add(Button.create("上一页", command + " " + (page - 1)));
        }
        if (page < page_count) {
            componentList.add(Button.create("下一页", command + " " + (page + 1)));
        }
        TextComponent.Builder builder = Component.text();
        for (Component component : componentList) {
            builder.append(component);
        }
        return builder.build();
    }
}
