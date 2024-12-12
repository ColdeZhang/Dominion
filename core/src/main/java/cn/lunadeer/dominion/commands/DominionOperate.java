package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.DominionInterface;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.events.dominion.DominionCreateEvent;
import cn.lunadeer.dominion.events.dominion.DominionDeleteEvent;
import cn.lunadeer.dominion.events.dominion.modify.*;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.utils.ArgumentParser;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.Scheduler;
import cn.lunadeer.minecraftpluginutils.Teleport;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.Map;

import static cn.lunadeer.dominion.DominionNode.isInDominion;
import static cn.lunadeer.dominion.utils.CommandUtils.*;
import static cn.lunadeer.dominion.utils.ControllerUtils.getPlayerCurrentDominion;
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
            Notification.error(sender, Translation.Commands_Dominion_CreateDominionUsage);
            return;
        }
        Map<Integer, Location> points = Dominion.pointsSelect.get(player.getUniqueId());
        if (points == null || points.get(0) == null || points.get(1) == null) {
            Notification.error(sender, Translation.Commands_Dominion_CreateSelectPointsFirst);
            return;
        }
        if (!points.get(0).getWorld().getUID().equals(points.get(1).getWorld().getUID())) {
            Notification.error(sender, Translation.Messages_SelectPointsWorldNotSame);
            return;
        }
        if (!player.getWorld().getUID().equals(points.get(0).getWorld().getUID())) {
            Notification.error(sender, Translation.Messages_CrossWorldOperationDisallowed);
            return;
        }
        String name = args[1];
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        new DominionCreateEvent(operator, name, player.getUniqueId(), points.get(0), points.get(1), null).call();
    }

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
            Notification.error(sender, Translation.Commands_Dominion_CreateSubDominionUsage);
            return;
        }
        Map<Integer, Location> points = Dominion.pointsSelect.get(player.getUniqueId());
        if (points == null || points.get(0) == null || points.get(1) == null) {
            Notification.error(sender, Translation.Commands_Dominion_CreateSubSelectPointsFirst);
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        DominionDTO parent;
        if (args.length == 2) {
            parent = getPlayerCurrentDominion(operator);
            if (parent == null) {
                Notification.error(sender, Translation.Messages_CannotGetDominionAuto);
                return;
            }
        } else {
            parent = DominionInterface.instance.getDominion(args[2]);
            if (parent == null) {
                Notification.error(sender, Translation.Messages_ParentDominionNotExist, args[2]);
                return;
            }
        }
        new DominionCreateEvent(operator, args[1], player.getUniqueId(), points.get(0), points.get(1), parent).call();
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
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        if (args.length != 2) {
            Notification.error(sender, Translation.Commands_Dominion_AutoCreateDominionUsage);
            return;
        }
        if (Dominion.config.getAutoCreateRadius() < 0) {
            Notification.error(sender, Translation.Commands_Dominion_AutoCreateDominionDisabled);
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
            Notification.error(sender, Translation.Commands_Dominion_AutoCreateSubDominionUsage);
            return;
        }
        if (Dominion.config.getAutoCreateRadius() < 0) {
            Notification.error(sender, Translation.Commands_Dominion_AutoCreateDominionDisabled);
            return;
        }
        autoPoints(player);
        createSubDominion(sender, args);
    }


    /**
     * 扩张领地
     * /dominion expand [size=10] [face=NORTH,SOUTH,EAST,WEST,UP,DOWN] [name=领地名称]
     * /dominion contract [size=10] [face=NORTH,SOUTH,EAST,WEST,UP,DOWN] [name=领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     * @param type   扩张类型
     */
    public static void sizeChangeDominion(CommandSender sender, String[] args, DominionSizeChangeEvent.SizeChangeType type) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        Player player = playerOnly(sender);
        if (player == null) return;
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(player);
        ArgumentParser parser = new ArgumentParser(args);
        // get size
        int size;
        try {
            size = parser.getValInt("size", 10);
        } catch (NumberFormatException e) {
            Notification.error(sender, Translation.Commands_Dominion_SizeShouldBeInteger);
            return;
        }
        if (size <= 0) {
            Notification.error(sender, Translation.Commands_Dominion_SizeShouldBePositive);
            return;
        }
        // get direction
        BlockFace blockFace;
        if (parser.hasKey("face")) {
            blockFace = BlockFace.valueOf(parser.getVal("face"));
        } else {
            blockFace = operator.getDirection();
            if (blockFace == null) {
                Notification.error(sender, Translation.Messages_CannotGetDirection);
                return;
            }
        }
        // get dominion
        DominionDTO dominion;
        if (parser.hasKey("name")) {
            dominion = DominionInterface.instance.getDominion(parser.getVal("name", ""));
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, parser.getVal("name"));
                return;
            }
        } else {
            dominion = getPlayerCurrentDominion(operator);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_CannotGetDominionAuto);
                return;
            }
        }
        new DominionSizeChangeEvent(operator, dominion, type, blockFace, size).call();
    }

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
        if (args.length < 2) {
            Notification.error(sender, Translation.Commands_Dominion_DeleteDominionUsage);
            return;
        }
        boolean force = false;
        if (args.length == 3) {
            if (args[2].equals("force")) {
                force = true;
            }
        }
        DominionDTO dominion = DominionInterface.instance.getDominion(args[1]);
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[1]);
            return;
        }
        DominionDeleteEvent event = new DominionDeleteEvent(operator, dominion);
        event.setForce(force);
        event.call();
    }

    /**
     * 设置领地进入提示
     * /dominion set_enter_msg <提示语> [领地名称]
     * /dominion set_leave_msg <提示语> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setEnterLeaveMessage(CommandSender sender, String[] args, DominionSetMessageEvent.MessageChangeType type) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
        if (args.length < 2) {
            if (type == DominionSetMessageEvent.MessageChangeType.ENTER) {
                Notification.error(sender, Translation.Commands_Dominion_SetEnterMessageUsage);
            } else {
                Notification.error(sender, Translation.Commands_Dominion_SetLeaveMessageUsage);
            }
            return;
        }
        DominionDTO dominion;
        if (args.length == 2) {
            dominion = getPlayerCurrentDominion(operator);
        } else {
            dominion = DominionInterface.instance.getDominion(args[2]);
        }
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[2]);
            return;
        }
        if (type == DominionSetMessageEvent.MessageChangeType.ENTER) {
            new DominionSetMessageEvent(operator, dominion, DominionSetMessageEvent.MessageChangeType.ENTER, args[1]).call();
        } else {
            new DominionSetMessageEvent(operator, dominion, DominionSetMessageEvent.MessageChangeType.LEAVE, args[1]).call();
        }
    }

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
        if (args.length < 1) {
            Notification.error(sender, Translation.Commands_Dominion_SetTpLocationUsage);
            return;
        }
        DominionDTO dominion;
        if (args.length == 1) {
            dominion = getPlayerCurrentDominion(operator);
        } else {
            dominion = DominionInterface.instance.getDominion(args[1]);
        }
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[1]);
            return;
        }
        Location location = player.getLocation();
        new DominionSetTpLocationEvent(operator, dominion, location).call();
    }

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
            Notification.error(sender, Translation.Commands_Dominion_RenameDominionUsage);
            return;
        }
        DominionDTO dominion = DominionInterface.instance.getDominion(args[1]);
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[1]);
            return;
        }
        new DominionRenameEvent(operator, dominion, args[2]).call();
    }

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
        if (args.length < 3) {
            Notification.error(sender, Translation.Commands_Dominion_GiveDominionUsage);
            return;
        }
        boolean force = false;
        if (args.length == 4) {
            if (args[3].equals("force")) {
                force = true;
            }
        }
        DominionDTO dominion = DominionInterface.instance.getDominion(args[1]);
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[1]);
            return;
        }
        PlayerDTO playerDTO = DominionInterface.instance.getPlayerDTO(args[2]);
        if (playerDTO == null) {
            Notification.error(sender, Translation.Messages_PlayerNotExist, args[2]);
            return;
        }
        DominionTransferEvent event = new DominionTransferEvent(operator, dominion, playerDTO);
        event.setForce(force);
        event.call();
    }

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
            Notification.error(sender, Translation.Commands_Dominion_TpDominionUsage);
            return;
        }
        DominionDTO dominionDTO = DominionInterface.instance.getDominion(args[1]);
        if (dominionDTO == null) {
            Notification.error(sender, Translation.Commands_Dominion_DominionNotExist);
            return;
        }
        if (player.isOp() && Dominion.config.getLimitOpBypass()) {
            Notification.warn(sender, Translation.Messages_OpBypassTpLimit);
            Location location = dominionDTO.getTpLocation();
            if (location == null) {
                int x = (dominionDTO.getX1() + dominionDTO.getX2()) / 2;
                int z = (dominionDTO.getZ1() + dominionDTO.getZ2()) / 2;
                World world = dominionDTO.getWorld();
                if (world == null) {
                    Notification.error(sender, Translation.Messages_WorldNotExist);
                    return;
                }
                location = new Location(world, x, player.getLocation().getY(), z);
                XLogger.warn(Translation.Messages_NoTpLocation, dominionDTO.getName());
            }
            Teleport.doTeleportSafely(player, location);
            Notification.info(player, Translation.Messages_TpToDominion, dominionDTO.getName());
            return;
        }
        if (!Dominion.config.getTpEnable()) {
            Notification.error(sender, Translation.Messages_TpDisabled);
            return;
        }

        MemberDTO privilegeDTO = DominionInterface.instance.getMember(player.getUniqueId(), dominionDTO);
        if (!canByPass(player, dominionDTO, privilegeDTO)) {
            if (privilegeDTO == null) {
                if (!dominionDTO.getGuestPrivilegeFlagValue().get(Flags.TELEPORT)) {
                    Notification.error(sender, Translation.Messages_DominionNoTp);
                    return;
                }
            } else {
                GroupDTO groupDTO = Cache.instance.getGroup(privilegeDTO.getGroupId());
                if (privilegeDTO.getGroupId() != -1 && groupDTO != null) {
                    if (!groupDTO.getFlagValue(Flags.TELEPORT)) {
                        Notification.error(sender, Translation.Messages_GroupNoTp);
                        return;
                    }
                } else {
                    if (!privilegeDTO.getFlagValue(Flags.TELEPORT)) {
                        Notification.error(sender, Translation.Messages_PrivilegeNoTp);
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
                Notification.error(player, Translation.Messages_TpCoolDown, secs_until_next);
                return;
            }
        }
        if (Dominion.config.getTpDelay() > 0) {
            Notification.info(player, Translation.Messages_TpDelay, Dominion.config.getTpDelay());
            Scheduler.runTaskAsync(() -> {
                int i = Dominion.config.getTpDelay();
                while (i > 0) {
                    if (!player.isOnline()) {
                        return;
                    }
                    Notification.actionBar(player, Translation.Messages_TpCountDown, i);
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
                Notification.error(player, Translation.Messages_WorldNotExist);
                return;
            }
            if (location == null) {
                location = new Location(world, center_x, player.getLocation().getY(), center_z);
                Notification.warn(player, Translation.Messages_NoTpLocation, dominionDTO.getName());
            } else if (!isInDominion(dominionDTO, location)) {
                location = new Location(world, center_x, player.getLocation().getY(), center_z);
                Notification.warn(player, Translation.Messages_TpLocationNotInside, dominionDTO.getName());
            }
            if (player.isOnline()) {
                Teleport.doTeleportSafely(player, location).thenAccept(b -> {
                    if (b) {
                        Notification.info(player, Translation.Messages_TpToDominion, dominionDTO.getName());
                    } else {
                        Notification.error(player, Translation.Messages_TpFailed);
                    }
                });
            }
        }, 20L * Dominion.config.getTpDelay());
    }

    /**
     * 设置领地卫星地图地块颜色
     * /dominion set_map_color <颜色> [领地名称]
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
    public static void setMapColor(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "dominion.default")) {
            return;
        }
        if (args.length < 2) {
            Notification.error(sender, Translation.Commands_Dominion_SetMapColorUsage);
            return;
        }
        BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
        DominionDTO dominion;
        if (args.length == 2) {
            dominion = getPlayerCurrentDominion(operator);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_CannotGetDominionAuto);
                return;
            }
        } else {
            dominion = DominionInterface.instance.getDominion(args[2]);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, args[2]);
                return;
            }
        }
        if (!args[1].matches("^#[0-9a-fA-F]{6}$")) {
            Notification.error(sender, Translation.Messages_MapColorInvalid);
            return;
        }
        Color color = Color.fromRGB(Integer.parseInt(args[1].substring(1), 16));
        new DominionSetMapColorEvent(operator, dominion, color).call();
    }

}
