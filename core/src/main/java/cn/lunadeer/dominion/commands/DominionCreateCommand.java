package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.events.dominion.DominionCreateEvent;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.command.Argument;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.*;
import static cn.lunadeer.dominion.misc.Others.autoPoints;

public class DominionCreateCommand {

    public static SecondaryCommand create = new SecondaryCommand("create", List.of(
            new Argument("name", true)
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            try {
                Player player = toPlayer(sender);
                World world = player.getWorld();
                Location[] points = getSelectedPoints(player);
                CuboidDTO cuboidDTO = new CuboidDTO(points[0], points[1]);
                new DominionCreateEvent(
                        sender,
                        getArgumentValue(0),
                        player.getUniqueId(),
                        world, cuboidDTO,
                        null
                ).call();
            } catch (Exception e) {
                Notification.error(sender, e);
            }
        }
    }.needPermission(defaultPermission).register();

    public static SecondaryCommand createSub = new SecondaryCommand("create_sub", List.of(
            new Argument("name", true),
            new CommandArguments.RequiredDominionArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            try {
                Player player = toPlayer(sender);
                World world = player.getWorld();
                Location[] points = getSelectedPoints(player);
                CuboidDTO cuboidDTO = new CuboidDTO(points[0], points[1]);
                new DominionCreateEvent(
                        sender,
                        getArgumentValue(0),
                        player.getUniqueId(),
                        world, cuboidDTO,
                        toDominionDTO(getArgumentValue(1))
                ).call();
            } catch (Exception e) {
                Notification.error(sender, e);
            }
        }
    }.needPermission(defaultPermission).register();

    public static SecondaryCommand autoCreate = new SecondaryCommand("auto_create", List.of(
            new Argument("name", true)
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            autoCreate(sender, getArgumentValue(0));
        }
    }.needPermission(defaultPermission).register();

    public static void autoCreate(CommandSender sender, String dominionName) {
        try {
            Player player = toPlayer(sender);
            World world = player.getWorld();
            Location[] points = autoPoints(player);
            CuboidDTO cuboidDTO = new CuboidDTO(points[0], points[1]);
            new DominionCreateEvent(
                    sender,
                    dominionName,
                    player.getUniqueId(),
                    world, cuboidDTO,
                    null
            ).call();
        } catch (Exception e) {
            Notification.error(sender, e);
        }
    }

    public static SecondaryCommand autoCreateSub = new SecondaryCommand("auto_create_sub", List.of(
            new Argument("name", true),
            new CommandArguments.RequiredDominionArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            try {
                Player player = toPlayer(sender);
                World world = player.getWorld();
                Location[] points = autoPoints(player);
                CuboidDTO cuboidDTO = new CuboidDTO(points[0], points[1]);
                new DominionCreateEvent(
                        sender,
                        getArgumentValue(0),
                        player.getUniqueId(),
                        world, cuboidDTO,
                        toDominionDTO(getArgumentValue(1))
                ).call();
            } catch (Exception e) {
                Notification.error(sender, e);
            }
        }
    }.needPermission(defaultPermission).register();

}
