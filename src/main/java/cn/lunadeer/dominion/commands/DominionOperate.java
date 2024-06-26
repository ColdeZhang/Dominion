package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.Scheduler;
import cn.lunadeer.minecraftpluginutils.Teleport;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.Map;

import static cn.lunadeer.dominion.DominionNode.isInDominion;
import static cn.lunadeer.dominion.commands.Apis.autoPoints;
import static cn.lunadeer.dominion.commands.Apis.playerOnly;

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
            Notification.error(sender, "请先使用工具选择领地的对角线两点，或使用 /dominion auto_create <领地名称> 创建自动领地");
            return;
        }
        String name = args[1];
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        DominionController.create(operator, name, points.get(0), points.get(1));
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
            Notification.error(sender, "请先使用工具选择子领地的对角线两点，或使用 /dominion auto_create_sub <子领地名称> [父领地名称] 创建自动子领地");
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length == 2) {
            DominionController.create(operator, args[1], points.get(0), points.get(1));
        } else {
            DominionController.create(operator, args[1], points.get(0), points.get(1), args[2]);
        }
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
        if (Dominion.config.getAutoCreateRadius() < 0) {
            Notification.error(sender, "自动创建领地功能已关闭");
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
        if (Dominion.config.getAutoCreateRadius() < 0) {
            Notification.error(sender, "自动创建领地功能已关闭");
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
        if (size <= 0) {
            Notification.error(sender, "大小必须大于0");
            return;
        }
        if (args.length == 3) {
            name = args[2];
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (name.isEmpty()) {
            DominionController.expand(operator, size);
        } else {
            DominionController.expand(operator, size, name);
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
        if (size <= 0) {
            Notification.error(sender, "大小必须大于0");
            return;
        }
        if (args.length == 3) {
            name = args[2];
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (name.isEmpty()) {
            DominionController.contract(operator, size);
        } else {
            DominionController.contract(operator, size, name);
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
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length == 2) {
            String name = args[1];
            DominionController.delete(operator, name, false);
            return;
        }
        if (args.length == 3) {
            String name = args[1];
            if (args[2].equals("force")) {
                DominionController.delete(operator, name, true);
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
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length == 2) {
            DominionController.setJoinMessage(operator, args[1]);
            return;
        }
        if (args.length == 3) {
            DominionController.setJoinMessage(operator, args[1], args[2]);
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
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length == 2) {
            DominionController.setLeaveMessage(operator, args[1]);
            return;
        }
        if (args.length == 3) {
            DominionController.setLeaveMessage(operator, args[1], args[2]);
            return;
        }
        Notification.error(sender, "用法: /dominion set_leave_msg <提示语> [领地名称]");
    }

    /**
     * 设置领地传送点
     * /dominion set_tp_location [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setTpLocation(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length == 1) {
            DominionController.setTpLocation(operator,
                    player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
            return;
        }
        if (args.length == 2) {
            DominionController.setTpLocation(operator,
                    player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(),
                    args[1]);
            return;
        }
        Notification.error(sender, "用法: /dominion set_tp_location [领地名称]");
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
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length != 3) {
            Notification.error(sender, "用法: /dominion rename <原领地名称> <新领地名称>");
            return;
        }
        DominionController.rename(operator, args[1], args[2]);
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
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length == 3) {
            String dom_name = args[1];
            String player_name = args[2];
            DominionController.give(operator, dom_name, player_name, false);
            return;
        }
        if (args.length == 4) {
            String dom_name = args[1];
            String player_name = args[2];
            if (args[3].equals("force")) {
                DominionController.give(operator, dom_name, player_name, true);
                return;
            }
        }
        Notification.error(sender, "用法: /dominion give <领地名称> <玩家名称>");
    }

    /**
     * 传送到领地
     * /dominion tp <领地名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void teleportToDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2) {
            Notification.error(sender, "用法: /dominion tp <领地名称>");
            return;
        }
        DominionDTO dominionDTO = DominionDTO.select(args[1]);
        if (dominionDTO == null) {
            Notification.error(sender, "领地不存在");
            return;
        }
        if (player.isOp() && Dominion.config.getLimitOpBypass()) {
            Notification.warn(sender, "你是OP，将忽略领地传送限制");
            Location location = dominionDTO.getTpLocation();
            if (location == null) {
                int x = (dominionDTO.getX1() + dominionDTO.getX2()) / 2;
                int z = (dominionDTO.getZ1() + dominionDTO.getZ2()) / 2;
                World world = Dominion.instance.getServer().getWorld(dominionDTO.getWorld());
                location = new Location(world, x, player.getLocation().getY(), z);
                XLogger.warn("领地 %s 没有设置传送点，将尝试传送到中心点", dominionDTO.getName());
            }
            Teleport.doTeleportSafely(player, location);
            Notification.info(player, "已将你传送到 " + dominionDTO.getName());
            return;
        }
        if (!Dominion.config.getTpEnable()) {
            Notification.error(sender, "管理员没有开启领地传送功能");
            return;
        }

        PlayerPrivilegeDTO privilegeDTO = PlayerPrivilegeDTO.select(player.getUniqueId(), dominionDTO.getId());
        if (!player.getUniqueId().equals(dominionDTO.getOwner())) { // 领地所有人可以传送到自己的领地
            if (privilegeDTO == null) {
                if (!dominionDTO.getFlagValue(Flag.TELEPORT)) {
                    Notification.error(sender, "此领地禁止传送");
                    return;
                }
            } else {
                if (!privilegeDTO.getFlagValue(Flag.TELEPORT)) {
                    Notification.error(sender, "你不被允许传送到这个领地");
                    return;
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next_time = Cache.instance.NextTimeAllowTeleport.get(player.getUniqueId());
        if (next_time != null) {
            if (now.isBefore(next_time)) {
                long secs_until_next = now.until(next_time, java.time.temporal.ChronoUnit.SECONDS);
                Notification.error(player, "请等待 %d 秒后再传送", secs_until_next);
                return;
            }
        }
        if (Dominion.config.getTpDelay() > 0) {
            Notification.info(player, "传送将在 %d 秒后执行", Dominion.config.getTpDelay());
            Scheduler.runTaskAsync(() -> {
                int i = Dominion.config.getTpDelay();
                while (i > 0) {
                    if (!player.isOnline()) {
                        return;
                    }
                    Notification.actionBar(player, "传送倒计时 %d 秒", i);
                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        XLogger.err(e.getMessage());
                    }
                }
            });
        }
        Cache.instance.NextTimeAllowTeleport.put(player.getUniqueId(), now.plusSeconds(Dominion.config.getTpCoolDown()));
        Scheduler.runTaskLater(() -> {
            Location location = dominionDTO.getTpLocation();
            int center_x = (dominionDTO.getX1() + dominionDTO.getX2()) / 2;
            int center_z = (dominionDTO.getZ1() + dominionDTO.getZ2()) / 2;
            World world = Dominion.instance.getServer().getWorld(dominionDTO.getWorld());
            if (location == null) {
                location = new Location(world, center_x, player.getLocation().getY(), center_z);
                XLogger.warn("领地 %s 没有设置传送点，将尝试传送到中心点", dominionDTO.getName());
            } else if (!isInDominion(dominionDTO, location)) {
                location = new Location(world, center_x, player.getLocation().getY(), center_z);
                XLogger.warn("领地 %s 传送点不在领地内，将尝试传送到中心点", dominionDTO.getName());
            }
            if (player.isOnline()) {
                Teleport.doTeleportSafely(player, location).thenAccept(b -> {
                    if (b) {
                        Notification.info(player, "已将你传送到 " + dominionDTO.getName());
                    } else {
                        Notification.error(player, "传送失败，请重试");
                    }
                });
            }
        }, 20L * Dominion.config.getTpDelay());
    }

    /**
     * 设置领地卫星地图地块颜色
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setMapColor(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length < 2) {
            Notification.error(sender, "用法: /dominion set_map_color <颜色> [领地名称]");
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length == 2) {
            DominionController.setMapColor(operator, args[1]);
        } else {
            DominionController.setMapColor(operator, args[1], args[2]);
        }
    }
}
