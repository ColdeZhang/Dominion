package cn.lunadeer.dominion.utils.scui;

import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.XVersionManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;

import static cn.lunadeer.dominion.utils.Misc.formatString;

public class OpenAnvilInventoryXVersion {

    public static AnvilInventory open(Player audience, String title) {
        try {
            return switch (XVersionManager.VERSION) {
                case v1_21 -> {
                    Class<?> clazz = Class.forName("cn.lunadeer.dominion.v1_21.scui.OpenAnvilInventory");
                    yield (AnvilInventory) clazz.getMethod("open", Player.class, String.class).invoke(null, audience, title);
                }
                case v1_20_1 -> {
                    Class<?> clazz = Class.forName("cn.lunadeer.dominion.v1_20_1.scui.OpenAnvilInventory");
                    yield (AnvilInventory) clazz.getMethod("open", Player.class, String.class).invoke(null, audience, title);
                }
                default -> {
                    String message = formatString("Unsupported API version: {0} for XVersionOpenAnvilInventory", XVersionManager.VERSION);
                    Notification.error(audience, message);
                    yield null;
                }
            };
        } catch (Exception e) {
            XLogger.error(e);
            return null;
        }
    }

}
