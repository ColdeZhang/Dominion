package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.events.dominion.DominionDeleteEvent;
import cn.lunadeer.dominion.events.dominion.modify.*;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.command.Argument;
import cn.lunadeer.dominion.utils.command.Option;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.*;

public class DominionOperateCommand {

    public static SecondaryCommand resize = new SecondaryCommand("resize", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new Option(Arrays.stream(DominionReSizeEvent.TYPE.values()).map(Enum::name).toList()),
            new Argument("size", true),
            new Option(Arrays.stream(DominionReSizeEvent.DIRECTION.values()).map(Enum::name).toList(), "")
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            resize(sender, getArgumentValue(0), getArgumentValue(1), getArgumentValue(2), getArgumentValue(3));
        }
    }.needPermission(defaultPermission).register();

    public static SecondaryCommand delete = new SecondaryCommand("delete", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new Option(List.of("force"), "")
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            delete(sender, getArgumentValue(0), getArgumentValue(1));
        }
    }.needPermission(defaultPermission).register();

    public static SecondaryCommand setMessage = new SecondaryCommand("set_msg", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new Option(Arrays.stream(DominionSetMessageEvent.TYPE.values()).map(Enum::name).toList()),
            new Argument("message", true)
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            setMessage(sender, getArgumentValue(0), getArgumentValue(1), getArgumentValue(2));
        }
    }.needPermission(defaultPermission).register();

    public static SecondaryCommand setTeleport = new SecondaryCommand("set_tp", List.of(
            new CommandArguments.RequiredDominionArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            setTp(sender, getArgumentValue(0));
        }
    }.needPermission(defaultPermission).register();

    public static SecondaryCommand rename = new SecondaryCommand("rename", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new Argument("newName", true)
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            rename(sender, getArgumentValue(0), getArgumentValue(1));
        }
    }.needPermission(defaultPermission).register();

    public static SecondaryCommand setMapColor = new SecondaryCommand("set_map_color", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new Argument("color", true)
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            setMapColor(sender, getArgumentValue(0), getArgumentValue(1));
        }
    }.needPermission(defaultPermission).register();

    public static SecondaryCommand give = new SecondaryCommand("give", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new CommandArguments.OptionalPageArgument(),
            new Option(List.of("force"), "")
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            try {
                DominionDTO dominion = toDominionDTO(getArgumentValue(0));
                PlayerDTO player = toPlayerDTO(getArgumentValue(1));
                boolean force = getArgumentValue(2).equals("force");
                DominionTransferEvent event = new DominionTransferEvent(sender, dominion, player);
                event.setForce(force);
                event.call();
            } catch (Exception e) {
                Notification.error(sender, e.getMessage());
            }
        }
    }.needPermission(defaultPermission).register();

    /**
     * Adjusts the size of a specified dominion.
     *
     * @param sender       The command sender who initiates the size adjustment.
     * @param dominionName The name of the dominion to be resized.
     * @param operation    The operation to perform, either "expand" or "contract".
     * @param sizeStr      The size value to adjust by.
     * @param faceStr      The direction to adjust the size in (e.g., "N", "S", "E", "W", "U", "D").
     */
    public static void resize(CommandSender sender, String dominionName, String operation, String sizeStr, String faceStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            DominionReSizeEvent.TYPE type = toResizeType(operation);
            int size = toIntegrity(sizeStr);
            DominionReSizeEvent.DIRECTION dir = faceStr.isEmpty() ? toDirection(toPlayer(sender)) : toDirection(faceStr);
            new DominionReSizeEvent(
                    sender,
                    dominion,
                    type,
                    dir,
                    size
            ).call();
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * Sets a message for a specified dominion.
     *
     * @param sender       The command sender who initiates the message setting.
     * @param dominionName The name of the dominion for which the message is being set.
     * @param typeStr      The type of message being set, either "enter" or "leave".
     * @param msg          The message content to be set.
     */
    public static void setMessage(CommandSender sender, String dominionName, String typeStr, String msg) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            DominionSetMessageEvent.TYPE type = toMessageType(typeStr);
            new DominionSetMessageEvent(
                    sender,
                    dominion,
                    type,
                    msg
            ).call();
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * Deletes a specified dominion.
     *
     * @param sender       The command sender who initiates the deletion.
     * @param dominionName The name of the dominion to be deleted.
     * @param forceStr     A string indicating whether the deletion should be forced ("force").
     */
    public static void delete(CommandSender sender, String dominionName, String forceStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            boolean force = forceStr.equals("force");
            DominionDeleteEvent even = new DominionDeleteEvent(sender, dominion);
            even.setForce(force);
            even.call();
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * Renames a specified dominion.
     *
     * @param sender       The command sender who initiates the renaming.
     * @param dominionName The name of the dominion to be renamed.
     * @param newName      The new name for the dominion.
     */
    public static void rename(CommandSender sender, String dominionName, String newName) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            new DominionRenameEvent(sender, dominion, newName).call();
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    public static void setMapColor(CommandSender sender, String dominionName, String colorStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            Color color = toColor(colorStr);
            new DominionSetMapColorEvent(sender, dominion, color).call();
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    public static void setTp(CommandSender sender, String dominionName) {
        try {
            Player player = toPlayer(sender);
            DominionDTO dominion = toDominionDTO(dominionName);
            new DominionSetTpLocationEvent(sender, dominion, player.getLocation()).call();
        } catch (Exception e) {
            Notification.error(sender, e.getMessage());
        }
    }

    /**
     * 传送到领地
     * /dominion tp <领地名称>
     *
     * @param sender 命令发送者
     * @param args   命令参数
     */
//    public static void teleportToDominion(CommandSender sender, String[] args) {
//        if (!hasPermission(sender, defaultPermission)) {
//            return;
//        }
//        Player player = playerOnly(sender);
//        if (player == null) return;
//        if (args.length != 2) {
//            Notification.error(sender, Translation.Commands_Dominion_TpDominionUsage);
//            return;
//        }
//        DominionDTO dominionDTO = DominionInterface.instance.getDominion(args[1]);
//        if (dominionDTO == null) {
//            Notification.error(sender, Translation.Commands_Dominion_DominionNotExist);
//            return;
//        }
//        if (player.isOp() && Configuration.adminBypass) {
//            Notification.warn(sender, Translation.Messages_OpBypassTpLimit);
//            Location location = dominionDTO.getTpLocation();
//            if (location == null) {
//                int x = (dominionDTO.getX1() + dominionDTO.getX2()) / 2;
//                int z = (dominionDTO.getZ1() + dominionDTO.getZ2()) / 2;
//                World world = dominionDTO.getWorld();
//                if (world == null) {
//                    Notification.error(sender, Translation.Messages_WorldNotExist);
//                    return;
//                }
//                location = new Location(world, x, player.getLocation().getY(), z);
//                XLogger.warn(Translation.Messages_NoTpLocation, dominionDTO.getName());
//            }
//            Teleport.doTeleportSafely(player, location);
//            Notification.info(player, Translation.Messages_TpToDominion, dominionDTO.getName());
//            return;
//        }
//        if (!Configuration.getPlayerLimitation(player).teleportation.enable) {
//            Notification.error(sender, Translation.Messages_TpDisabled);
//            return;
//        }
//
//        MemberDTO privilegeDTO = DominionInterface.instance.getMember(player.getUniqueId(), dominionDTO);
//        if (!canByPass(player, dominionDTO, privilegeDTO)) {
//            if (privilegeDTO == null) {
//                if (!dominionDTO.getGuestPrivilegeFlagValue().get(Flags.TELEPORT)) {
//                    Notification.error(sender, Translation.Messages_DominionNoTp);
//                    return;
//                }
//            } else {
//                GroupDTO groupDTO = Cache.instance.getGroup(privilegeDTO.getGroupId());
//                if (privilegeDTO.getGroupId() != -1 && groupDTO != null) {
//                    if (!groupDTO.getFlagValue(Flags.TELEPORT)) {
//                        Notification.error(sender, Translation.Messages_GroupNoTp);
//                        return;
//                    }
//                } else {
//                    if (!privilegeDTO.getFlagValue(Flags.TELEPORT)) {
//                        Notification.error(sender, Translation.Messages_PrivilegeNoTp);
//                        return;
//                    }
//                }
//            }
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime next_time = Cache.instance.NextTimeAllowTeleport.get(player.getUniqueId());
//        if (next_time != null) {
//            if (now.isBefore(next_time)) {
//                long secs_until_next = now.until(next_time, java.time.temporal.ChronoUnit.SECONDS);
//                Notification.error(player, Translation.Messages_TpCoolDown, secs_until_next);
//                return;
//            }
//        }
//        if (Configuration.getPlayerLimitation(player).teleportation.delay > 0) {
//            Notification.info(player, Translation.Messages_TpDelay, Configuration.getPlayerLimitation(player).teleportation.delay);
//            Scheduler.runTaskAsync(() -> {
//                int i = Configuration.getPlayerLimitation(player).teleportation.delay;
//                while (i > 0) {
//                    if (!player.isOnline()) {
//                        return;
//                    }
//                    Notification.actionBar(player, Translation.Messages_TpCountDown, i);
//                    i--;
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        XLogger.err(e.getMessage());
//                    }
//                }
//            });
//        }
//        Cache.instance.NextTimeAllowTeleport.put(player.getUniqueId(), now.plusSeconds(Configuration.getPlayerLimitation(player).teleportation.cooldown));
//        Scheduler.runTaskLater(() -> {
//            Location location = dominionDTO.getTpLocation();
//            int center_x = (dominionDTO.getX1() + dominionDTO.getX2()) / 2;
//            int center_z = (dominionDTO.getZ1() + dominionDTO.getZ2()) / 2;
//            World world = dominionDTO.getWorld();
//            if (world == null) {
//                Notification.error(player, Translation.Messages_WorldNotExist);
//                return;
//            }
//            if (location == null) {
//                location = new Location(world, center_x, player.getLocation().getY(), center_z);
//                Notification.warn(player, Translation.Messages_NoTpLocation, dominionDTO.getName());
//            } else if (!isInDominion(dominionDTO, location)) {
//                location = new Location(world, center_x, player.getLocation().getY(), center_z);
//                Notification.warn(player, Translation.Messages_TpLocationNotInside, dominionDTO.getName());
//            }
//            if (player.isOnline()) {
//                Teleport.doTeleportSafely(player, location).thenAccept(b -> {
//                    if (b) {
//                        Notification.info(player, Translation.Messages_TpToDominion, dominionDTO.getName());
//                    } else {
//                        Notification.error(player, Translation.Messages_TpFailed);
//                    }
//                });
//            }
//        }, 20L * Configuration.getPlayerLimitation(player).teleportation.delay);
//    }

}
