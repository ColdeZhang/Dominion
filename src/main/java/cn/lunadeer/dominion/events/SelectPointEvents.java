package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.Location;
import org.bukkit.Material;
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

        if (item.getType() != Material.ARROW) {
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
            Notification.info(player, "已选择第一个点: " + block.getX() + " " + block.getY() + " " + block.getZ());
            points.put(0, block.getLocation());
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            Notification.info(player, "已选择第二个点: " + block.getX() + " " + block.getY() + " " + block.getZ());
            points.put(1, block.getLocation());
        } else {
            return;
        }

        if (points.size() == 2) {
            Notification.info(player, "已选择两个点，可以使用 /dominion create <领地名称> 创建领地");
            Notification.info(player, "尺寸为 " +
                    Math.abs(points.get(1).getX() - points.get(0).getX()) + " x " +
                    Math.abs(points.get(1).getY() - points.get(0).getY()) + " x " +
                    Math.abs(points.get(1).getZ() - points.get(0).getZ()));
            Notification.info(player, "面积为 " +
                    Math.abs(points.get(1).getX() - points.get(0).getX()) *
                            Math.abs(points.get(1).getZ() - points.get(0).getZ()));
            Notification.info(player, "高度为 " +
                    Math.abs(points.get(1).getY() - points.get(0).getY()));
            Notification.info(player, "体积为 " +
                    Math.abs(points.get(1).getX() - points.get(0).getX()) *
                            Math.abs(points.get(1).getY() - points.get(0).getY()) *
                            Math.abs(points.get(1).getZ() - points.get(0).getZ()));
        }
        Dominion.pointsSelect.put(player.getUniqueId(), points);
    }
}
