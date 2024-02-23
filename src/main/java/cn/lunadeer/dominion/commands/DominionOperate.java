package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.utils.Notification;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static cn.lunadeer.dominion.commands.Apis.*;

public class DominionOperate {
    /**
     * 创建领地
     * /dominion create <领地名称>
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
        Map<Integer, Location> points = Dominion.pointsSelect.get(player.getUniqueId());
        if (points == null || points.get(0) == null || points.get(1) == null) {
            Notification.error(sender, "请先使用箭矢选择领地的对角线两点，或使用 /dominion auto_create <领地名称> 创建自动领地");
            return;
        }
        String name = args[1];
        if (DominionController.create(player, name, points.get(0), points.get(1)) == null) {
            Notification.error(sender, "创建领地失败");
            return;
        }
        Notification.info(sender, "成功创建: " + name);
    }

    /**
     * 创建子领地
     * /dominion create_sub <子领地名称> [父领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void createSubDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2 && args.length != 3) {
            Notification.error(sender, "用法: /dominion create_sub <子领地名称> [父领地名称]");
            return;
        }
        Map<Integer, Location> points = Dominion.pointsSelect.get(player.getUniqueId());
        if (points == null || points.get(0) == null || points.get(1) == null) {
            Notification.error(sender, "请先使用箭矢选择子领地的对角线两点，或使用 /dominion auto_create_sub <子领地名称> [父领地名称] 创建自动子领地");
            return;
        }
        if (args.length == 2) {
            if (DominionController.create(player, args[1], points.get(0), points.get(1)) != null) {
                Notification.info(sender, "成功创建子领地: " + args[1]);
                return;
            }
        } else {
            if (DominionController.create(player, args[1], points.get(0), points.get(1), args[2]) != null) {
                Notification.info(sender, "成功创建子领地: " + args[1]);
                return;
            }
        }
        Notification.error(sender, "创建子领地失败");
    }

    /**
     * 自动创建领地
     * 会在玩家当前位置的周围创建一个领地
     * /dominion auto_create <领地名称>
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
        autoPoints(player);
        createDominion(sender, args);
    }

    /**
     * 自动创建子领地
     * 会在玩家当前位置的周围创建一个子领地
     * /dominion auto_create_sub <子领地名称> [父领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void autoCreateSubDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2 && args.length != 3) {
            Notification.error(sender, "用法: /dominion auto_create_sub <子领地名称> [父领地名称]");
            return;
        }
        autoPoints(player);
        createSubDominion(sender, args);
    }

    /**
     * 扩张领地
     * /dominion expand [大小] [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void expandDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2 && args.length != 3) {
            Notification.error(sender, "用法: /dominion expand [大小] [领地名称]");
            return;
        }
        int size = 10;
        String name = "";
        try {
            size = Integer.parseInt(args[1]);
        } catch (Exception e) {
            Notification.error(sender, "大小格式错误");
            return;
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

    /**
     * 缩小领地
     * /dominion contract [大小] [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void contractDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2 && args.length != 3) {
            Notification.error(sender, "用法: /dominion contract [大小] [领地名称]");
            return;
        }
        int size = 10;
        String name = "";
        try {
            size = Integer.parseInt(args[1]);
        } catch (Exception e) {
            Notification.error(sender, "大小格式错误");
            return;
        }
        if (args.length == 3) {
            name = args[2];
        }
        DominionDTO dominionDTO;
        if (name.isEmpty()) {
            dominionDTO = DominionController.contract(player, size);
        } else {
            dominionDTO = DominionController.contract(player, size, name);
        }
        if (dominionDTO == null) {
            Notification.error(sender, "缩小领地失败");
        } else {
            Notification.info(sender, "成功缩小领地: " + dominionDTO.getName() + " " + size);
            sizeInfo(sender, dominionDTO);
        }
    }

    /**
     * 删除领地
     * /dominion delete <领地名称> [force]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void deleteDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length == 2) {
            String name = args[1];
            DominionController.delete(player, name, false);
            return;
        }
        if (args.length == 3) {
            String name = args[1];
            if (args[2].equals("force")) {
                DominionController.delete(player, name, true);
                return;
            }
        }
        Notification.error(sender, "用法: /dominion delete <领地名称>");
    }

    /**
     * 设置领地进入提示
     * /dominion set_enter_msg <提示语> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setEnterMessage(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length == 2) {
            DominionController.setJoinMessage(player, args[1]);
            return;
        }
        if (args.length == 3) {
            DominionController.setJoinMessage(player, args[1], args[2]);
            return;
        }
        Notification.error(sender, "用法: /dominion set_enter_msg <提示语> [领地名称]");
    }

    /**
     * 设置领地离开提示
     * /dominion set_leave_msg <提示语> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setLeaveMessage(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length == 2) {
            DominionController.setLeaveMessage(player, args[1]);
            return;
        }
        if (args.length == 3) {
            DominionController.setLeaveMessage(player, args[1], args[2]);
            return;
        }
        Notification.error(sender, "用法: /dominion set_leave_msg <提示语> [领地名称]");
    }

    /**
     * 重命名领地
     * /dominion rename <原领地名称> <新领地名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void renameDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 3) {
            Notification.error(sender, "用法: /dominion rename <原领地名称> <新领地名称>");
            return;
        }
        DominionController.rename(player, args[1], args[2]);
    }

    /**
     * 转让领地
     * /dominion give <领地名称> <玩家名称> [force]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void giveDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length == 3) {
            String dom_name = args[1];
            String player_name = args[2];
            DominionController.give(player, dom_name, player_name, false);
            return;
        }
        if (args.length == 4) {
            String dom_name = args[1];
            String player_name = args[2];
            if (args[3].equals("force")) {
                DominionController.give(player, dom_name, player_name, true);
                return;
            }
        }
        Notification.error(sender, "用法: /dominion give <领地名称> <玩家名称>");
    }
}
