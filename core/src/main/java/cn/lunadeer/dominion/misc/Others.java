package cn.lunadeer.dominion.misc;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.utils.MessageDisplay;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        List<DominionDTO> sub_dominions = CacheManager.instance.getCache().getDominionCache().getChildrenOf(dominion.getId());
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
        MemberDTO member = CacheManager.instance.getMember(dom, player);
        try {
            assertDominionAdmin(player, dom);
            return true;
        } catch (Exception e) {
            if (member != null) {
                GroupDTO group = CacheManager.instance.getGroup(member.getGroupId());
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

    public static boolean isInDominion(@Nullable DominionDTO dominion, @NotNull Location location) {
        if (dominion == null) return false;
        if (!Objects.equals(dominion.getWorldUid(), location.getWorld().getUID())) return false;
        return dominion.getCuboid().contain(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static void lightOrNot(@NotNull Player player, @Nullable DominionDTO dominion) {
        if (!Flags.GLOW.getEnable()) {
            return;
        }
        if (dominion == null) {
            player.setGlowing(false);
            return;
        }
        MemberDTO member = CacheManager.instance.getCache().getMemberCache().getMember(dominion, player);
        if (member != null) {
            if (member.getGroupId() == -1) {
                player.setGlowing(member.getFlagValue(Flags.GLOW));
            } else {
                GroupDTO group = CacheManager.instance.getCache().getGroupCache().getGroup(member.getGroupId());
                if (group != null) {
                    player.setGlowing(group.getFlagValue(Flags.GLOW));
                } else {
                    player.setGlowing(dominion.getGuestPrivilegeFlagValue().get(Flags.GLOW));
                }
            }
        } else {
            player.setGlowing(dominion.getGuestPrivilegeFlagValue().get(Flags.GLOW));
        }
    }

    public static void flyOrNot(@NotNull Player player, @Nullable DominionDTO dominion) {
        for (String flyPN : Configuration.flyPermissionNodes) {
            if (player.hasPermission(flyPN)) {
                return;
            }
        }
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (player.isOp() && Configuration.adminBypass) {
            return;
        }
        if (!Flags.FLY.getEnable()) {
            player.setAllowFlight(false);
            return;
        }
        if (dominion == null) {
            player.setAllowFlight(false);
            return;
        }
        MemberDTO member = CacheManager.instance.getCache().getMemberCache().getMember(dominion, player);
        if (member != null) {
            if (member.getGroupId() == -1) {
                player.setAllowFlight(member.getFlagValue(Flags.FLY));
            } else {
                GroupDTO group = CacheManager.instance.getCache().getGroupCache().getGroup(member.getGroupId());
                if (group != null) {
                    player.setAllowFlight(group.getFlagValue(Flags.FLY));
                } else {
                    player.setAllowFlight(dominion.getGuestPrivilegeFlagValue().get(Flags.FLY));
                }
            }
        } else {
            player.setAllowFlight(dominion.getGuestPrivilegeFlagValue().get(Flags.FLY));
        }
    }

    public static boolean isCrop(@NotNull Material material) {
        return material == Material.COCOA ||
                material == Material.WHEAT ||
                material == Material.CARROTS ||
                material == Material.POTATOES ||
                material == Material.BEETROOTS ||
                material == Material.NETHER_WART ||
                material == Material.SWEET_BERRY_BUSH ||
                material == Material.MELON ||
                material == Material.PUMPKIN ||
                material == Material.SUGAR_CANE ||
                material == Material.BAMBOO ||
                material == Material.CACTUS ||
                material == Material.CHORUS_PLANT ||
                material == Material.CHORUS_FLOWER ||
                material == Material.KELP ||
                material == Material.KELP_PLANT;
    }
}
