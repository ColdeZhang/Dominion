package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.utils.i18n.i18n;
import cn.lunadeer.dominion.utils.i18n.i18nField;
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
import static cn.lunadeer.dominion.utils.CommandUtils.*;
import static cn.lunadeer.dominion.utils.EventUtils.canByPass;

public class DominionOperate {
    /**
     * 创建领地
     * /dominion create <领地名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void createDominion(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2) {
            Notification.error(sender, CreateDominionUsage.trans());
            return;
        }
        Map<Integer, Location> points = Dominion.pointsSelect.get(player.getUniqueId());
        if (points == null || points.get(0) == null || points.get(1) == null) {
            Notification.error(sender, CreateSelectPointsFirst.trans());
            return;
        }
        String name = args[1];
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        DominionController.create(operator, name, points.get(0), points.get(1));
    }

    @i18nField(defaultValue = "用法: /dominion create <领地名称>")
    static i18n CreateDominionUsage;

    @i18nField(defaultValue = "请先使用工具选择领地的对角线两点，或使用 /dominion auto_create <领地名称> 创建自动领地")
    static i18n CreateSelectPointsFirst;

    /**
     * 创建子领地
     * /dominion create_sub <子领地名称> [父领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void createSubDominion(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2 && args.length != 3) {
            Notification.error(sender, CreateSubDominionUsage.trans());
            return;
        }
        Map<Integer, Location> points = Dominion.pointsSelect.get(player.getUniqueId());
        if (points == null || points.get(0) == null || points.get(1) == null) {
            Notification.error(sender, CreateSubSelectPointsFirst.trans());
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        if (args.length == 2) {
            DominionController.create(operator, args[1], points.get(0), points.get(1));
        } else {
            DominionController.create(operator, args[1], points.get(0), points.get(1), args[2]);
        }
    }

    @i18nField(defaultValue = "用法: /dominion create_sub <子领地名称> [父领地名称]")
    static i18n CreateSubDominionUsage;

    @i18nField(defaultValue = "请先使用工具选择子领地的对角线两点，或使用 /dominion auto_create_sub <子领地名称> [父领地名称] 创建自动子领地")
    static i18n CreateSubSelectPointsFirst;

    /**
     * 自动创建领地
     * 会在玩家当前位置的周围创建一个领地
     * /dominion auto_create <领地名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void autoCreateDominion(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2) {
            Notification.error(sender, AutoCreateDominionUsage.trans());
            return;
        }
        if (Dominion.config.getAutoCreateRadius() < 0) {
            Notification.error(sender, AutoCreateDominionDisabled.trans());
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
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2 && args.length != 3) {
            Notification.error(sender, AutoCreateSubDominionUsage.trans());
            return;
        }
        if (Dominion.config.getAutoCreateRadius() < 0) {
            Notification.error(sender, AutoCreateDominionDisabled.trans());
            return;
        }
        autoPoints(player);
        createSubDominion(sender, args);
    }

    @i18nField(defaultValue = "用法: /dominion auto_create <领地名称>")
    static i18n AutoCreateDominionUsage;

    @i18nField(defaultValue = "用法: /dominion auto_create_sub <子领地名称> [父领地名称]")
    static i18n AutoCreateSubDominionUsage;

    @i18nField(defaultValue = "自动创建领地功能已关闭")
    static i18n AutoCreateDominionDisabled;

    /**
     * 扩张领地
     * /dominion expand [大小] [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void expandDominion(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2 && args.length != 3) {
            Notification.error(sender, ExpandDominionUsage.trans());
            return;
        }
        int size = 10;
        String name = "";
        try {
            size = Integer.parseInt(args[1]);
        } catch (Exception e) {
            Notification.error(sender, SizeShouldBeInteger.trans());
            return;
        }
        if (size <= 0) {
            Notification.error(sender, SizeShouldBePositive.trans());
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
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2 && args.length != 3) {
            Notification.error(sender, ContractDominionUsage.trans());
            return;
        }
        int size = 10;
        String name = "";
        try {
            size = Integer.parseInt(args[1]);
        } catch (Exception e) {
            Notification.error(sender, SizeShouldBeInteger.trans());
            return;
        }
        if (size <= 0) {
            Notification.error(sender, SizeShouldBePositive.trans());
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

    @i18nField(defaultValue = "用法: /dominion expand [大小] [领地名称]")
    static i18n ExpandDominionUsage;

    @i18nField(defaultValue = "用法: /dominion contract [大小] [领地名称]")
    static i18n ContractDominionUsage;


    @i18nField(defaultValue = "大小应当为整数")
    static i18n SizeShouldBeInteger;

    @i18nField(defaultValue = "大小应当大于0")
    static i18n SizeShouldBePositive;

    /**
     * 删除领地
     * /dominion delete <领地名称> [force]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void deleteDominion(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
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
        Notification.error(sender, DeleteDominionUsage.trans());
    }

    @i18nField(defaultValue = "用法: /dominion delete <领地名称>")
    static i18n DeleteDominionUsage;

    /**
     * 设置领地进入提示
     * /dominion set_enter_msg <提示语> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setEnterMessage(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
        if (args.length == 2) {
            DominionController.setJoinMessage(operator, args[1]);
            return;
        }
        if (args.length == 3) {
            DominionController.setJoinMessage(operator, args[1], args[2]);
            return;
        }
        Notification.error(sender, SetEnterMessageUsage.trans());
    }

    @i18nField(defaultValue = "用法: /dominion set_enter_msg <提示语> [领地名称]")
    static i18n SetEnterMessageUsage;

    /**
     * 设置领地离开提示
     * /dominion set_leave_msg <提示语> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setLeaveMessage(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
        if (args.length == 2) {
            DominionController.setLeaveMessage(operator, args[1]);
            return;
        }
        if (args.length == 3) {
            DominionController.setLeaveMessage(operator, args[1], args[2]);
            return;
        }
        Notification.error(sender, SetLeaveMessageUsage.trans());
    }

    @i18nField(defaultValue = "用法: /dominion set_leave_msg <提示语> [领地名称]")
    static i18n SetLeaveMessageUsage;

    /**
     * 设置领地传送点
     * /dominion set_tp_location [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setTpLocation(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
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
        Notification.error(sender, SetTpLocationUsage.trans());
    }

    @i18nField(defaultValue = "用法: /dominion set_tp_location [领地名称]")
    static i18n SetTpLocationUsage;

    /**
     * 重命名领地
     * /dominion rename <原领地名称> <新领地名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void renameDominion(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
        if (args.length != 3) {
            Notification.error(sender, RenameDominionUsage.trans());
            return;
        }
        DominionController.rename(operator, args[1], args[2]);
    }

    @i18nField(defaultValue = "用法: /dominion rename <原领地名称> <新领地名称>")
    static i18n RenameDominionUsage;

    /**
     * 转让领地
     * /dominion give <领地名称> <玩家名称> [force]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void giveDominion(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
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
        Notification.error(sender, GiveDominionUsage.trans());
    }

    @i18nField(defaultValue = "用法: /dominion give <领地名称> <玩家名称>")
    static i18n GiveDominionUsage;

    /**
     * 传送到领地
     * /dominion tp <领地名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void teleportToDominion(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2) {
            Notification.error(sender, TpDominionUsage.trans());
            return;
        }
        DominionDTO dominionDTO = DominionDTO.select(args[1]);
        if (dominionDTO == null) {
            Notification.error(sender, DominionNotExist.trans());
            return;
        }
        if (player.isOp() && Dominion.config.getLimitOpBypass()) {
            Notification.warn(sender, OpBypassTpLimit.trans());
            Location location = dominionDTO.getTpLocation();
            if (location == null) {
                int x = (dominionDTO.getX1() + dominionDTO.getX2()) / 2;
                int z = (dominionDTO.getZ1() + dominionDTO.getZ2()) / 2;
                World world = dominionDTO.getWorld();
                if (world == null) {
                    Notification.error(sender, WorldNotExist.trans());
                    return;
                }
                location = new Location(world, x, player.getLocation().getY(), z);
                XLogger.warn(DominionNoTpLocation.trans(), dominionDTO.getName());
            }
            Teleport.doTeleportSafely(player, location);
            Notification.info(player, TpToDominion.trans(), dominionDTO.getName());
            return;
        }
        if (!Dominion.config.getTpEnable()) {
            Notification.error(sender, TpDisabled.trans());
            return;
        }

        MemberDTO privilegeDTO = MemberDTO.select(player.getUniqueId(), dominionDTO.getId());
        if (!canByPass(player, dominionDTO, privilegeDTO)) {
            if (privilegeDTO == null) {
                if (!dominionDTO.getFlagValue(Flag.TELEPORT)) {
                    Notification.error(sender, DominionNoTp.trans());
                    return;
                }
            } else {
                GroupDTO groupDTO = Cache.instance.getGroup(privilegeDTO.getGroupId());
                if (privilegeDTO.getGroupId() != -1 && groupDTO != null) {
                    if (!groupDTO.getFlagValue(Flag.TELEPORT)) {
                        Notification.error(sender, GroupNoTp.trans());
                        return;
                    }
                } else {
                    if (!privilegeDTO.getFlagValue(Flag.TELEPORT)) {
                        Notification.error(sender, PrivilegeNoTp.trans());
                        return;
                    }
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next_time = Cache.instance.NextTimeAllowTeleport.get(player.getUniqueId());
        if (next_time != null) {
            if (now.isBefore(next_time)) {
                long secs_until_next = now.until(next_time, java.time.temporal.ChronoUnit.SECONDS);
                Notification.error(player, TpCoolDown.trans(), secs_until_next);
                return;
            }
        }
        if (Dominion.config.getTpDelay() > 0) {
            Notification.info(player, TpDelay.trans(), Dominion.config.getTpDelay());
            Scheduler.runTaskAsync(() -> {
                int i = Dominion.config.getTpDelay();
                while (i > 0) {
                    if (!player.isOnline()) {
                        return;
                    }
                    Notification.actionBar(player, TpCountDown.trans(), i);
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
            World world = dominionDTO.getWorld();
            if (world == null) {
                Notification.error(player, WorldNotExist.trans());
                return;
            }
            if (location == null) {
                location = new Location(world, center_x, player.getLocation().getY(), center_z);
                Notification.warn(player, DominionNoTpLocation.trans(), dominionDTO.getName());
            } else if (!isInDominion(dominionDTO, location)) {
                location = new Location(world, center_x, player.getLocation().getY(), center_z);
                Notification.warn(player, DominionTpLocationNotInDominion.trans(), dominionDTO.getName());
            }
            if (player.isOnline()) {
                Teleport.doTeleportSafely(player, location).thenAccept(b -> {
                    if (b) {
                        Notification.info(player, TpToDominion.trans(), dominionDTO.getName());
                    } else {
                        Notification.error(player, TpFailed.trans());
                    }
                });
            }
        }, 20L * Dominion.config.getTpDelay());
    }

    @i18nField(defaultValue = "用法: /dominion tp <领地名称>")
    static i18n TpDominionUsage;

    @i18nField(defaultValue = "领地不存在")
    static i18n DominionNotExist;

    @i18nField(defaultValue = "你是OP，将忽略领地传送限制")
    static i18n OpBypassTpLimit;

    @i18nField(defaultValue = "领地所在世界不存在")
    static i18n WorldNotExist;

    @i18nField(defaultValue = "领地 %s 没有设置传送点，将尝试传送到中心点")
    static i18n DominionNoTpLocation;

    @i18nField(defaultValue = "领地 %s 传送点不在领地内，将尝试传送到中心点")
    static i18n DominionTpLocationNotInDominion;

    @i18nField(defaultValue = "已将你传送到 %s")
    static i18n TpToDominion;

    @i18nField(defaultValue = "管理员没有开启领地传送功能")
    static i18n TpDisabled;

    @i18nField(defaultValue = "此领地禁止传送")
    static i18n DominionNoTp;

    @i18nField(defaultValue = "你所在的权限组组不被允许传送到这个领地")
    static i18n GroupNoTp;

    @i18nField(defaultValue = "你不被允许传送到这个领地")
    static i18n PrivilegeNoTp;

    @i18nField(defaultValue = "请等待 %d 秒后再传送")
    static i18n TpCoolDown;

    @i18nField(defaultValue = "传送将在 %d 秒后执行")
    static i18n TpDelay;

    @i18nField(defaultValue = "传送倒计时 %d 秒")
    static i18n TpCountDown;

    @i18nField(defaultValue = "传送失败，请重试")
    static i18n TpFailed;

    /**
     * 设置领地卫星地图地块颜色
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setMapColor(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        if (args.length < 2) {
            Notification.error(sender, SetMapColorUsage.trans());
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
        if (args.length == 2) {
            DominionController.setMapColor(operator, args[1]);
        } else {
            DominionController.setMapColor(operator, args[1], args[2]);
        }
    }

    @i18nField(defaultValue = "用法: /dominion set_map_color <颜色> [领地名称]")
    static i18n SetMapColorUsage;

    static {
        // 初始化国际化字段
        i18n.initializeI18nFields(DominionOperate.class);
    }

}
