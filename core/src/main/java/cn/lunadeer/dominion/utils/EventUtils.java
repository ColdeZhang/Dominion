package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.CacheImpl;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
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
                GroupDTO group = CacheImpl.instance.getGroup(prev.getGroupId());
                return group != null && group.getAdmin();
            }
        }
        return false;
    }

    public static DominionDTO getInvDominion(Player bukkitPlayer, Inventory inv) {
        if (inv.getLocation() == null) {
            return null;
        } else {
            return CacheImpl.instance.getDominionByLoc(inv.getLocation());
        }
    }

    public static boolean checkFlag(DominionDTO dom, Flag flag, Player player, Cancellable event) {
        if (!flag.getEnable()) {
            return true;
        }
        if (dom == null) {
            return true;
        }
        MemberDTO prev = CacheImpl.instance.getMember(player, dom);
        if (canByPass(player, dom, prev)) {
            return true;
        }
        if (prev != null) {
            GroupDTO group = CacheImpl.instance.getGroup(prev.getGroupId());
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
            if (dom.getFlagValue(flag)) {
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

    public static boolean checkFlag(@Nullable DominionDTO dom, @NotNull Flag flag, @Nullable Cancellable event) {
        if (!flag.getEnable()) {
            return true;
        }
        if (dom == null) {
            return true;
        }
        if (dom.getFlagValue(flag)) {
            return true;
        }
        if (event != null) {
            event.setCancelled(true);
        }
        return false;
    }
}
