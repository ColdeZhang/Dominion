package cn.lunadeer.dominion.utils.scui;

import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.scui.task.ClickTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static cn.lunadeer.dominion.utils.scui.CuiManager.createHiddenComponentWithToken;

public class ItemStackButton {
    public static final String btnPrefix = "cui:btn:";

    private final UUID token = UUID.randomUUID();
    private TextComponent title = null;
    private List<Component> lores = new ArrayList<>();
    private Material material;
    private CuiView view;
    private final SortedMap<Integer, ClickTask> left_c_tasks = new TreeMap<>();
    private final SortedMap<Integer, ClickTask> right_c_tasks = new TreeMap<>();

    public static ItemStackButton create(CuiView view, Material material) {
        ItemStackButton button = new ItemStackButton();
        button.view = view;
        button.material = material;
        return button;
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (title != null) {
            meta.displayName(createHiddenComponentWithToken(title, token, btnPrefix));
        } else {
            TextComponent raw_name = (TextComponent) meta.displayName();
            if (raw_name == null) {
                raw_name = Component.text(material.toString());
            }
            meta.displayName(createHiddenComponentWithToken(raw_name, token, btnPrefix));
        }
        if (lores.size() > 0) {
            meta.lore(lores);
        }
        itemStack.setItemMeta(meta);
        register();
        return itemStack;
    }

    public void click(InventoryClickEvent event) {
        XLogger.debug("Button clicked");
        event.setCancelled(true);
        if (event.getClick().isLeftClick()) {
            left_c_tasks.forEach((priority, task) -> {
                task.run(view, event);
            });
        } else if (event.getClick().isRightClick()) {
            right_c_tasks.forEach((priority, task) -> {
                task.run(view, event);
            });
        }
    }

    public ItemStackButton addLeftClickTask(Integer priority, ClickTask task) {
        left_c_tasks.put(priority, task);
        return this;
    }

    public ItemStackButton addRightClickTask(Integer priority, ClickTask task) {
        right_c_tasks.put(priority, task);
        return this;
    }

    public UUID getToken() {
        return token;
    }

    public void register() {
        CuiManager.instance.register(this);
    }

    public void unregister() {
        CuiManager.instance.unregister(this);
    }

    public String getTitlePlainText() {
        return title.content();
    }

    public TextComponent getTitle() {
        return title;
    }

    public ItemStackButton title(String title) {
        this.title = Component.text(title);
        return this;
    }

    public ItemStackButton title(TextComponent title) {
        this.title = title;
        return this;
    }

    public CuiView getView() {
        return view;
    }

    public ItemStackButton lores(List<Component> lores) {
        this.lores = lores;
        return this;
    }

}
