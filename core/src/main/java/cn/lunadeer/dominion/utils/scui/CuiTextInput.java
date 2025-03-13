package cn.lunadeer.dominion.utils.scui;

import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.AnvilInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static cn.lunadeer.dominion.utils.Misc.isPaper;


public class CuiTextInput implements CuiView {

    public static String CUI_NOT_AVAILABLE = "CUI is not available on no-paper (fork) core server.";
    public static String CUI_SUGGEST_COMMAND = "You can use command {0} instead.";
    public static String CUI_BUTTON = "Left Click: Confirm | Right Click: Cancel";
    public static String CUI_INPUT_INVALID = "Input can not contain space.";

    public interface InputCallback {
        public void handleData(String input);
    }

    private String text = "";
    private final UUID token = UUID.randomUUID();
    private TextComponent title = Component.text("Title");
    private final Map<Integer, ItemStackButton> buttons = new HashMap<>();
    private InputCallback callback_handle_input;
    private AnvilInventory inv;
    private String suggest_command = null;

    public static CuiTextInput create(InputCallback callback_handle_input) {
        CuiTextInput cuiTextInput = new CuiTextInput();
        cuiTextInput.callback_handle_input = callback_handle_input;
        return cuiTextInput;
    }

    @Override
    public void open(Player audience) {
        if (!isPaper()) {
            Notification.error(audience, CUI_NOT_AVAILABLE);
            if (suggest_command != null) {
                Notification.info(audience, CUI_SUGGEST_COMMAND, suggest_command);
            }
            return;
        }
        // open anvil inventory
        inv = OpenAnvilInventoryXVersion.open(audience, title.content());

        ItemStackButton btn_1 = ItemStackButton.create(this, Material.NAME_TAG)
                .title(text);
        inv.setFirstItem(btn_1.build());

        ItemStackButton btn_2 = ItemStackButton.create(this, Material.GREEN_CONCRETE)
                .title(CUI_BUTTON);
        btn_2.addLeftClickTask(0, (view, event) -> {
            try {
                String input = inv.getRenameText();
                if (input != null && input.contains(" ")) {
                    Notification.error(audience, CUI_INPUT_INVALID);
                    return;
                }
                callback_handle_input.handleData(input);
            } catch (Exception e) {
                XLogger.error(e);
            }
            view.close(null);
        });
        btn_2.addRightClickTask(0, (view, event) -> {
            view.close(null);
        });
        inv.setSecondItem(btn_2.build());

        register();
    }

    public CuiTextInput setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public UUID getToken() {
        return token;
    }

    @Override
    public void close(InventoryCloseEvent event) {
        if (event == null) {
            inv.close();
        }
        unregister();
    }

    public String getTitlePlainText() {
        return title.content();
    }

    public TextComponent getTitle() {
        return title;
    }

    public CuiTextInput title(String title) {
        this.title = Component.text(title);
        return this;
    }

    public CuiTextInput title(TextComponent title) {
        this.title = title;
        return this;
    }

    @Override
    public void register() {
        CuiManager.instance.register(this);
    }

    @Override
    public void unregister() {
        for (ItemStackButton button : buttons.values()) {
            button.unregister();
        }
        CuiManager.instance.unregister(this);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void setButton(Integer slot, ItemStackButton button) {

    }

    public CuiTextInput setSuggestCommand(String command) {
        this.suggest_command = command;
        return this;
    }

}
