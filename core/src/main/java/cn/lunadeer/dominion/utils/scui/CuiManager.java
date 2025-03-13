package cn.lunadeer.dominion.utils.scui;

import cn.lunadeer.dominion.utils.XLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static cn.lunadeer.dominion.utils.Misc.isPaper;

public class CuiManager implements Listener {

    public static CuiManager instance;
    private final JavaPlugin plugin;
    private final Map<UUID, CuiView> cuis = new HashMap<>();

    private final Map<UUID, ItemStackButton> btns = new HashMap<>();

    public CuiManager(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;
    }

    public CuiView get(UUID token) {
        return cuis.get(token);
    }

    public void register(CuiView cuiView) {
        cuis.put(cuiView.getToken(), cuiView);
    }

    public void unregister(CuiView cuiView) {
        cuis.remove(cuiView.getToken());
    }

    public void register(ItemStackButton btn) {
        btns.put(btn.getToken(), btn);
    }

    public void unregister(ItemStackButton btn) {
        btns.remove(btn.getToken());
    }

    public void unregisterAll() {
        cuis.clear();
        btns.clear();
    }

    public CuiView getCUI(UUID token) {
        return cuis.get(token);
    }

    public ItemStackButton getBtn(UUID token) {
        return btns.get(token);
    }

    public Server getServer() {
        return plugin.getServer();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClickExecute(InventoryClickEvent event) {
        if (!isPaper()) {
            return;
        }
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) {
            return;
        }
        UUID token = getTokenFromHiddenComponent(currentItem.displayName(), ItemStackButton.btnPrefix);
        if (token == null) {
            return;
        }
        ItemStackButton btn = getBtn(token);
        if (btn == null) {
            return;
        }
        btn.click(event);
    }

    /**
     * 尝试从 Inventory#title 中获取 Token
     *
     * @param title InventoryView
     * @return Token
     */
    public static UUID getTokenFromHiddenComponent(Component title, String prefix) {
        String component_str = title.toString();
        // look for prefix
        int prefixIndex = component_str.indexOf(prefix);
        if (prefixIndex == -1) {
            return null;
        }
        String uuidStr = component_str.substring(prefixIndex + prefix.length(), prefixIndex + prefix.length() + 36);
        if (uuidStr.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            XLogger.debug(e.getMessage());
            return null;
        }
    }

    public static TextComponent createHiddenComponentWithToken(String name, UUID token, String prefix) {
        return createHiddenComponentWithToken(Component.text(name), token, prefix);
    }

    public static TextComponent createHiddenComponentWithToken(TextComponent name, UUID token, String prefix) {
        return name.hoverEvent(Component.text(prefix + token.toString()));
    }
}
