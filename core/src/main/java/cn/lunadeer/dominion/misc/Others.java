package cn.lunadeer.dominion.misc;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.utils.MessageDisplay;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static cn.lunadeer.dominion.Dominion.adminPermission;
import static cn.lunadeer.dominion.misc.Asserts.assertDominionAdmin;
import static cn.lunadeer.dominion.utils.Misc.formatString;

public class Others {

    public static class OthersText extends ConfigurationPart {
        public String autoCleanStart = "Start auto clean players who have not logged in for {0} days.";
        public String autoCleaningPlayer = "Cleaned {0}'s data.";
        public String autoCleanEnd = "Auto clean finished.";
        public String noPermissionForFlag = "You do not have {0}({1}) permission.";
    }

    public static boolean bypassLimit(Player player) {
        return player.isOp() || player.hasPermission(adminPermission);
    }

    /**
     * Recursively retrieves all sub-dominions of the given dominion.
     * This method fetches the direct sub-dominions from the cache and then recursively
     * retrieves their sub-dominions, building a complete list of all nested sub-dominions.
     *
     * @param dominion the dominion for which to retrieve sub-dominions
     * @return a list of all sub-dominions of the given dominion
     */
    public static List<DominionDTO> getSubDominionsRecursive(DominionDTO dominion) {
        List<DominionDTO> res = new ArrayList<>();
        List<DominionDTO> sub_dominions = Cache.instance.getDominionsByParentId(dominion.getId());
        for (DominionDTO sub_dominion : sub_dominions) {
            res.add(sub_dominion);
            res.addAll(getSubDominionsRecursive(sub_dominion));
        }
        return res;
    }

    /**
     * Calculates the automatic points for a player based on their current location and the configured radius.
     * This method considers the player's current location and expands it by the configured radius to determine
     * two diagonal points of a cuboid area. If vertical inclusion is enabled, it adjusts the Y coordinates
     * based on the player's world settings.
     *
     * @param player The player for whom to calculate the automatic points.
     * @return An array of two Location objects representing the diagonal points of the cuboid area.
     */
    public static Location[] autoPoints(Player player) {
        int size = Configuration.autoCreateRadius;
        Location location = player.getLocation();
        Location location1 = new Location(location.getWorld(), location.getX() - size, location.getY() - size, location.getZ() - size);
        Location location2 = new Location(location.getWorld(), location.getX() + size, location.getY() + size, location.getZ() + size);
        if (Configuration.getPlayerLimitation(player).getWorldSettings(player.getWorld()).autoIncludeVertical) {
            location1.setY(Configuration.getPlayerLimitation(player).getWorldSettings(player.getWorld()).noLowerThan);
            location2.setY(Configuration.getPlayerLimitation(player).getWorldSettings(player.getWorld()).noHigherThan - 1);
        }
        return new Location[]{location1, location2};
    }

    /**
     * Automatically cleans up player data for players who have not logged in for a configured number of days.
     * This method checks the last login time of each player and deletes their data if they have not logged in
     * within the configured number of days. It logs the start, progress, and end of the cleaning process.
     */
    public static void autoClean() {
        if (Configuration.autoCleanAfterDays == -1) {
            return;
        }
        XLogger.info(Language.othersText.autoCleanStart, Configuration.autoCleanAfterDays);
        int auto_clean_after_days = Configuration.autoCleanAfterDays;
        List<cn.lunadeer.dominion.api.dtos.PlayerDTO> players = PlayerDTO.all();
        for (cn.lunadeer.dominion.api.dtos.PlayerDTO p : players) {
            if (((PlayerDTO) p).getLastJoinAt() + (long) auto_clean_after_days * 24 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                PlayerDTO.delete((PlayerDTO) p);
                XLogger.info(Language.othersText.autoCleaningPlayer, p.getLastKnownName());
            }
        }
        XLogger.info(Language.othersText.autoCleanEnd);
    }

    public static boolean checkPrivilegeFlag(@Nullable DominionDTO dom, @NotNull PriFlag flag, @NotNull Player player, @Nullable Cancellable event) {
        if (!flag.getEnable()) {
            return true;
        }
        if (dom == null) {
            return true;
        }
        MemberDTO member = Cache.instance.getMember(player, dom);
        try {
            assertDominionAdmin(player, dom);
            return true;
        } catch (Exception e) {
            if (member != null) {
                GroupDTO group = Cache.instance.getGroup(member.getGroupId());
                if (member.getGroupId() != -1 && group != null) {
                    if (group.getFlagValue(flag)) {
                        return true;
                    }
                } else {
                    if (member.getFlagValue(flag)) {
                        return true;
                    }
                }
            } else {
                if (dom.getGuestPrivilegeFlagValue().get(flag)) {
                    return true;
                }
            }
            String msg = formatString(Language.othersText.noPermissionForFlag, flag.getDisplayName(), flag.getDescription());
            msg = "&#FF0000" + "&l" + msg;
            MessageDisplay.show(player, MessageDisplay.Place.valueOf(Configuration.pluginMessage.noPermissionDisplayPlace.toUpperCase()), msg);
            if (event != null) {
                event.setCancelled(true);
            }
            return false;
        }
    }

    public static boolean checkEnvironmentFlag(@Nullable DominionDTO dom, @NotNull EnvFlag flag, @Nullable Cancellable event) {
        if (!flag.getEnable()) {
            return true;
        }
        if (dom == null) {
            return true;
        }
        if (dom.getEnvironmentFlagValue().get(flag)) {
            return true;
        }
        if (event != null) {
            event.setCancelled(true);
        }
        return false;
    }

    public static DominionDTO getInventoryDominion(Player bukkitPlayer, Inventory inv) {
        if (inv.getLocation() == null) {
            return null;
        } else {
            return Cache.instance.getDominionByLoc(inv.getLocation());
        }
    }
}
