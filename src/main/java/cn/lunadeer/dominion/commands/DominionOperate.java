package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.utils.Notification;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;
import static cn.lunadeer.dominion.commands.Apis.sizeInfo;

public class DominionOperate {
    /**
     * 创建领地
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void createDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2) {
            Notification.error(sender, "用法: /dominion create <领地名称>");
            return;
        }
        List<Location> points = Dominion.pointsSelect.get(player.getUniqueId());
        if (points == null || points.size() != 2) {
            Notification.error(sender, "请先使用木棍选择领地的对角线两点，或使用 /dominion auto_create <领地名称> 创建自动领地");
            return;
        }
        String name = args[1];
        DominionDTO dominionDTO = DominionDTO.select(name);
        if (dominionDTO != null) {
            Notification.error(sender, "已经存在名称为 " + name + " 的领地");
            return;
        }
        dominionDTO = DominionController.create(player, name, points.get(0), points.get(1));
        if (dominionDTO == null) {
            Notification.error(sender, "创建领地失败");
            return;
        }
        Notification.info(sender, "成功创建领地: " + name);
    }

    /**
     * 自动创建领地
     * 会在玩家当前位置的周围创建一个 20x20x20 的领地
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void autoCreateDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2) {
            Notification.error(sender, "用法: /dominion auto_create <领地名称>");
            return;
        }
        String name = args[1];
        DominionDTO dominionDTO = DominionDTO.select(name);
        if (dominionDTO != null) {
            Notification.error(sender, "已经存在名称为 " + name + " 的领地");
            return;
        }
        Location location = player.getLocation();
        Location location1 = new Location(location.getWorld(), location.getX() - 10, location.getY() - 10, location.getZ() - 10);
        Location location2 = new Location(location.getWorld(), location.getX() + 10, location.getY() + 10, location.getZ() + 10);
        dominionDTO = DominionController.create(player, name, location1, location2);
        if (dominionDTO == null) {
            Notification.error(sender, "创建领地失败");
            return;
        }
        Notification.info(sender, "成功创建领地: " + name);
    }

    public static void expandDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2 && args.length != 3) {
            Notification.error(sender, "用法: /dominion expand [大小] [领地名称]");
            return;
        }
        int size = 10;
        String name = "";
        if (args.length == 2) {
            try {
                size = Integer.parseInt(args[1]);
            } catch (Exception e) {
                Notification.error(sender, "大小格式错误");
                return;
            }
        }
        if (args.length == 3) {
            name = args[2];
        }
        DominionDTO dominionDTO;
        if (name.isEmpty()) {
            dominionDTO = DominionController.expand(player, size);
        } else {
            dominionDTO = DominionController.expand(player, size, name);
        }
        if (dominionDTO == null) {
            Notification.error(sender, "扩展领地失败");
        } else {
            Notification.info(sender, "成功扩展领地: " + dominionDTO.getName() + " " + size);
            sizeInfo(sender, dominionDTO);
        }
    }
}
