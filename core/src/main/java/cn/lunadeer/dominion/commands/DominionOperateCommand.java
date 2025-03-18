package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.events.dominion.DominionDeleteEvent;
import cn.lunadeer.dominion.events.dominion.modify.*;
import cn.lunadeer.dominion.managers.TeleportManager;
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
            new Option(Arrays.stream(DominionReSizeEvent.TYPE.values()).map(Enum::name).map(String::toLowerCase).toList()),
            new Argument("size", true),
            new Option(Arrays.stream(DominionReSizeEvent.DIRECTION.values()).map(Enum::name).map(String::toLowerCase).toList(), "")
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
            new Option(Arrays.stream(DominionSetMessageEvent.TYPE.values()).map(Enum::name).map(String::toLowerCase).toList()),
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
            new CommandArguments.RequiredPlayerArgument(),
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
                Notification.error(sender, e);
            }
        }
    }.needPermission(defaultPermission).register();

    public static SecondaryCommand tp = new SecondaryCommand("tp", List.of(
            new CommandArguments.RequiredDominionArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            try {
                Player player = toPlayer(sender);
                DominionDTO dominion = toDominionDTO(getArgumentValue(0));
                TeleportManager.teleportToDominion(player, dominion);
            } catch (Exception e) {
                Notification.error(sender, e);
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
            Notification.error(sender, e);
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
            Notification.error(sender, e);
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
            Notification.error(sender, e);
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
            Notification.error(sender, e);
        }
    }

    public static void setMapColor(CommandSender sender, String dominionName, String colorStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            Color color = toColor(colorStr);
            new DominionSetMapColorEvent(sender, dominion, color).call();
        } catch (Exception e) {
            Notification.error(sender, e);
        }
    }

    public static void setTp(CommandSender sender, String dominionName) {
        try {
            Player player = toPlayer(sender);
            DominionDTO dominion = toDominionDTO(dominionName);
            new DominionSetTpLocationEvent(sender, dominion, player.getLocation()).call();
        } catch (Exception e) {
            Notification.error(sender, e);
        }
    }

}
