package cn.lunadeer.dominion.events_v1_20_1;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.utils.Particle;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.VaultConnect.VaultConnect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SelectPointEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void selectPoint(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Dominion.config.getTool()) {
            return;
        }
        Block block = event.getClickedBlock();
        Action action = event.getAction();
        if (block == null) {
            return;
        }

        Map<Integer, Location> points = Dominion.pointsSelect.get(player.getUniqueId());
        if (points == null) {
            points = new HashMap<>();
        }

        if (action == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            Notification.info(player, "已选择第一个点: %d %d %d", block.getX(), block.getY(), block.getZ());
            Location loc = block.getLocation();
            if (Dominion.config.getLimitVert(player)) {
                loc.setY(Dominion.config.getLimitMinY(player));
            }
            points.put(0, loc);
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            Notification.info(player, "已选择第二个点: %d %d %d", block.getX(), block.getY(), block.getZ());
            Location loc = block.getLocation();
            if (Dominion.config.getLimitVert(player)) {
                loc.setY(Dominion.config.getLimitMaxY(player) - 1);
            }
            points.put(1, loc);
        } else {
            return;
        }
        Dominion.pointsSelect.put(player.getUniqueId(), points);

        if (points.size() == 2) {
            World world = points.get(0).getWorld();
            if (world == null) {
                return;
            }
            if (!points.get(0).getWorld().equals(points.get(1).getWorld())) {
                Notification.warn(player, "两个点不在同一个世界");
                return;
            }
            Notification.info(player, "已选择两个点，可以使用 /dominion create <领地名称> 创建领地");
            Location loc1 = points.get(0);
            Location loc2 = points.get(1);
            int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
            int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
            int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
            int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX()) + 1;
            int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY()) + 1;
            int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ()) + 1;
            DominionDTO dominion = new DominionDTO(player.getUniqueId(), "", loc1.getWorld(),
                    minX, minY, minZ, maxX, maxY, maxZ);
            if (Dominion.config.getEconomyEnable()) {
                if (!VaultConnect.instance.economyAvailable()) {
                    Notification.error(player, "计算价格失败，没有可用的经济插件系统，请联系服主。");
                    return;
                }
                int count;
                if (Dominion.config.getEconomyOnlyXZ(player)) {
                    count = dominion.getSquare();
                } else {
                    count = dominion.getVolume();
                }
                float price = count * Dominion.config.getEconomyPrice(player);
                Notification.info(player, "预计领地创建价格为 %.2f %s", price, VaultConnect.instance.currencyNamePlural());
            }
            Particle.showBorder(player, dominion);
            Notification.info(player, "尺寸： %d x %d x %d", dominion.getWidthX(), dominion.getHeight(), dominion.getWidthZ());
            Notification.info(player, "面积： %d", dominion.getSquare());
            Notification.info(player, "高度： %d", dominion.getHeight());
            Notification.info(player, "体积： %d", dominion.getVolume());
        }
    }
}
