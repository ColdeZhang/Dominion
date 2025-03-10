package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.events.dominion.DominionCreateEvent;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.uis.tuis.MigrateList;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.dominion.utils.command.Argument;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

import static cn.lunadeer.dominion.Dominion.adminPermission;
import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.toPlayer;
import static cn.lunadeer.dominion.misc.Converts.toWorld;

public class MigrationCommand {

    public static class MigrationCommandText extends ConfigurationPart {
        public String createPlayerFailed = "Failed to create player cache for {0}.";
        public String migrateSuccess = "Migrated residence {0} to dominion successfully.";
        public String migrateFailed = "Failed to migrate residence. Reason: {1}";
        public String missingResidence = "Residence {0} not found.";
        public String notYourResidence = "Residence {0} is not yours.";
    }

    /**
     * Creates a ListViewButton for migration.
     *
     * @param sender        the command sender
     * @param residenceName the name of the residence
     * @return the ListViewButton
     */
    public static ListViewButton button(CommandSender sender, String residenceName) {
        return (ListViewButton) new ListViewButton(Language.migrateListText.button) {
            @Override
            public void function(String pageStr) {
                migrate(sender, residenceName, pageStr);
            }
        }.needPermission(defaultPermission);
    }

    /**
     * Secondary command for migration.
     */
    public static SecondaryCommand migrate = new SecondaryCommand("migrate", List.of(
            new Argument("residence_name", true),
            new CommandArguments.OptionalPageArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            migrate(sender, getArgumentValue(0), getArgumentValue(1));
        }
    }.needPermission(defaultPermission).register();

    /**
     * Handles the migration process.
     *
     * @param sender  the command sender
     * @param resName the name of the residence
     * @param pageStr the page string
     */
    public static void migrate(CommandSender sender, String resName, String pageStr) {
        try {
            if (!Configuration.residenceMigration) {
                Notification.error(sender, Language.migrateListText.notEnabled);
                return;
            }
            List<ResMigration.ResidenceNode> res_data = CacheManager.instance.getResidenceCache().getResidenceData();
            if (res_data == null) {
                throw new DominionException(Language.migrateListText.noData);
            }
            ResMigration.ResidenceNode resNode = res_data.stream().filter(node -> node.name.equals(resName)).findFirst().orElse(null);
            if (resNode == null) {
                throw new DominionException(Language.migrationCommandText.missingResidence, resName);
            }
            Player player = toPlayer(resNode.ownerName);
            if (!resNode.owner.equals(player.getUniqueId()) && !player.hasPermission(adminPermission)) {
                throw new DominionException(Language.migrationCommandText.notYourResidence, resName);
            }
            doMigrateCreate(player, resNode, null);
            MigrateList.show(sender, pageStr);
        } catch (Exception e) {
            Notification.error(sender, Language.migrationCommandText.migrateFailed, e.getMessage());
        }
    }

    /**
     * Performs the actual migration creation process.
     *
     * @param player the player
     * @param node   the residence node
     * @param parent the parent dominion DTO
     * @throws Exception if an error occurs during migration
     */
    private static void doMigrateCreate(Player player, ResMigration.ResidenceNode node, DominionDTO parent) throws Exception {
        PlayerDTO ownerDTO = cn.lunadeer.dominion.dtos.PlayerDTO.create(node.owner, node.ownerName);
        if (ownerDTO == null) {
            Notification.error(player, Language.migrationCommandText.createPlayerFailed, node.ownerName);
            return;
        }
        CuboidDTO cuboidDTO = new CuboidDTO(node.loc1, node.loc2);
        World world = toWorld(node.world.getUID());
        DominionCreateEvent event = new DominionCreateEvent(player, node.name, ownerDTO.getUuid(), world, cuboidDTO, parent);
        event.setSkipEconomy(true);
        event.callEvent();
        if (!event.isCancelled()) {
            Notification.info(player, Language.migrationCommandText.migrateSuccess, node.name);
            DominionDTO dominionCreated = event.getDominion();
            Objects.requireNonNull(Objects.requireNonNull(dominionCreated.setTpLocation(node.tpLoc))
                            .setJoinMessage(node.joinMessage))
                    .setLeaveMessage(node.leaveMessage);
            if (node.children != null) {
                for (ResMigration.ResidenceNode child : node.children) {
                    doMigrateCreate(player, child, dominionCreated);
                }
            }
        }
    }
}
