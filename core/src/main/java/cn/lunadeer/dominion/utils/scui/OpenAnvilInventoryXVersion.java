package cn.lunadeer.dominion.utils.scui;

import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.XVersionManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;

public class OpenAnvilInventoryXVersion {

    public static AnvilInventory open(Player audience, String title) {
        try {
            return switch (XVersionManager.VERSION) {
                case v1_21 -> {
                    Class<?> clazz = Class.forName("cn.lunadeer.dominion.v1_21_paper.scui.OpenAnvilInventory");
                    yield (AnvilInventory) clazz.getMethod("open", Player.class, String.class).invoke(null, audience, title);
                }
                case v1_20_1 -> {
                    Class<?> clazz = Class.forName("cn.lunadeer.dominion.v1_20_1.scui.OpenAnvilInventory");
                    yield (AnvilInventory) clazz.getMethod("open", Player.class, String.class).invoke(null, audience, title);
                }
                default -> {
                    XLogger.error("Unsupported API version: {0} for XVersionOpenAnvilInventory", XVersionManager.VERSION);
                    yield null;
                }
            };
        } catch (Exception e) {
            XLogger.error(e);
            return null;
        }
    }

}
