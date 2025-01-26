package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PreFlag;
import cn.lunadeer.dominion.managers.Translation;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EventUtils {
    public static boolean canByPass(Player player, DominionDTO dom, MemberDTO prev) {
        if (player.isOp() && Dominion.config.getLimitOpBypass()) {
            return true;
        }
        if (dom == null) {
            return true;
        }
        if (dom.getOwner().equals(player.getUniqueId())) {
            return true;
        }
        if (prev != null) {
            if (prev.getGroupId() == -1) {
                return prev.getAdmin();
            } else {
                GroupDTO group = Cache.instance.getGroup(prev.getGroupId());
                return group != null && group.getAdmin();
            }
        }
        return false;
    }

    public static DominionDTO getInvDominion(Player bukkitPlayer, Inventory inv) {
        if (inv.getLocation() == null) {
            return null;
        } else {
            return Cache.instance.getDominionByLoc(inv.getLocation());
        }
    }

    public static boolean checkPrivilegeFlag(@Nullable DominionDTO dom, @NotNull PreFlag flag, @NotNull Player player, @Nullable Cancellable event) {
        if (!flag.getEnable()) {
            return true;
        }
        if (dom == null) {
            return true;
        }
        MemberDTO prev = Cache.instance.getMember(player, dom);
        if (canByPass(player, dom, prev)) {
            return true;
        }
        if (prev != null) {
            GroupDTO group = Cache.instance.getGroup(prev.getGroupId());
            if (prev.getGroupId() != -1 && group != null) {
                if (group.getFlagValue(flag)) {
                    return true;
                }
            } else {
                if (prev.getFlagValue(flag)) {
                    return true;
                }
            }
        } else {
            if (dom.getGuestPrivilegeFlagValue().get(flag)) {
                return true;
            }
        }
        String msg = String.format(Translation.Messages_NoPermissionForFlag.trans(), flag.getDisplayName(), flag.getDescription());
        msg = "&#FF0000" + "&l" + msg;
        MessageDisplay.show(player, Dominion.config.getMessageDisplayNoPermission(), msg);
        if (event != null) {
            event.setCancelled(true);
        }
        return false;
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
}
