package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.Inventory;

public class Apis {
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
            return prev.getAdmin();
        }
        return false;
    }

    public static DominionDTO getInvDominion(Player bukkitPlayer, Inventory inv) {
        if (inv.getLocation() == null) {
            return null;
        } else {
            return Cache.instance.getDominion(inv.getLocation());
        }
    }

    public static boolean checkFlag(DominionDTO dom, Flag flag, Player player, Cancellable event) {
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
            if (prev.getGroupId() == -1) {
                if (prev.getFlagValue(flag)) {
                    return true;
                }
            } else {
                GroupDTO group = Cache.instance.getGroup(prev.getGroupId());
                if (group == null) {
                    return false;
                }
                if (group.getFlagValue(flag)) {
                    return true;
                }
            }
        } else {
            if (dom.getFlagValue(flag)) {
                return true;
            }
        }
        TextComponent msg = Component.text(
                        String.format("你没有 %s (%s) 权限", flag.getDisplayName(), flag.getDescription()),
                        Style.style(TextColor.color(0xFF0000), TextDecoration.BOLD))
                .hoverEvent(Component.text(flag.getDescription()));
        Notification.actionBar(player, msg);
        if (event != null) {
            event.setCancelled(true);
        }
        return false;
    }

    public static boolean checkFlag(DominionDTO dom, Flag flag, Cancellable event) {
        if (!flag.getEnable()) {
            return true;
        }
        if (dom == null) {
            return true;
        }
        if (dom.getFlagValue(flag)) {
            return true;
        }
        event.setCancelled(true);
        return false;
    }
}
