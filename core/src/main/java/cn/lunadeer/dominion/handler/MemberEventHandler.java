package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.events.member.MemberAddedEvent;
import cn.lunadeer.dominion.events.member.MemberRemovedEvent;
import cn.lunadeer.dominion.events.member.MemberSetFlagEvent;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static cn.lunadeer.dominion.misc.Asserts.*;

public class MemberEventHandler implements Listener {

    public static class MemberEventHandlerText extends ConfigurationPart {
        public String setFlagSuccess = "Successfully set flag {0} for {1} in {2}.";
        public String ownerOnly = "Only owner can manage admin member.";
        public String groupAlready = "This member belong to group {0} so you can't manage it separately.";
        public String setFlagFailed = "Failed to set flag, reason: {0}";

        public String addMemberSuccess = "Successfully added {0} to {1}.";
        public String alreadyMember = "{0} is already a member of {1}.";
        public String cantBeOwner = "You can't add dominion owner as a member.";
        public String addMemberFailed = "Failed to add member, reason: {0}";

        public String removeMemberSuccess = "Successfully removed {0} from {1}.";
        public String removeMemberFailed = "Failed to remove member, reason: {0}";
    }

    public MemberEventHandler(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMemberSetFlag(MemberSetFlagEvent event) {
        if (event.isCancelled()) return;
        try {
            DominionDTO dominion = event.getDominion();
            if (event.getFlag().equals(Flags.ADMIN)) {
                assertDominionOwner(event.getOperator(), dominion);
            } else {
                assertDominionAdmin(event.getOperator(), dominion);
            }
            assertMemberBelongDominion(event.getMember(), dominion);
            MemberDTO member = event.getMember();
            if (member.getGroupId() != -1) {
                throw new DominionException(Language.memberEventHandlerText.groupAlready, Cache.instance.getGroup(member.getGroupId()).getNamePlain());
            }
            member.setFlagValue(event.getFlag(), event.getNewValue());
            Notification.info(event.getOperator(), Language.memberEventHandlerText.setFlagSuccess, event.getFlag().getFlagName(), member.getPlayer().getLastKnownName(), dominion.getName());
        } catch (Exception e) {
            event.setCancelled(true);
            Notification.error(event.getOperator(), Language.memberEventHandlerText.setFlagFailed, e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMemberAddEvent(MemberAddedEvent event) {
        if (event.isCancelled()) return;
        try {
            DominionDTO dominion = event.getDominion();
            assertDominionAdmin(event.getOperator(), dominion);
            PlayerDTO player = event.getPlayer();
            if (player.getUuid().equals(dominion.getOwner())) {
                throw new DominionException(Language.memberEventHandlerText.cantBeOwner);
            }
            MemberDTO member = Cache.instance.getMember(player.getUuid(), dominion);
            if (member != null) {
                throw new DominionException(Language.memberEventHandlerText.alreadyMember, event.getPlayer().getLastKnownName(), dominion.getName());
            }
            member = cn.lunadeer.dominion.dtos.MemberDTO.insert(new cn.lunadeer.dominion.dtos.MemberDTO(player.getUuid(), dominion));
            event.setMember(member);
            Notification.info(event.getOperator(), Language.memberEventHandlerText.addMemberSuccess, event.getPlayer().getLastKnownName(), dominion.getName());
        } catch (Exception e) {
            event.setCancelled(true);
            Notification.error(event.getOperator(), Language.memberEventHandlerText.addMemberFailed, e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMemberRemoveEvent(MemberRemovedEvent event) {
        if (event.isCancelled()) return;
        try {
            DominionDTO dominion = event.getDominion();
            assertDominionAdmin(event.getOperator(), dominion);
            MemberDTO member = event.getMember();
            assertMemberBelongDominion(member, dominion);
            boolean owner = false;
            try {
                assertDominionOwner(event.getOperator(), dominion);
                owner = true;
            } catch (DominionException ignored) {
            }
            GroupDTO group = Cache.instance.getGroup(member.getGroupId());
            if (group != null) {
                if (group.getFlagValue(Flags.ADMIN) && !owner) {
                    throw new DominionException(Language.groupEventHandlerText.ownerOnly);
                }
            } else {
                if (member.getFlagValue(Flags.ADMIN) && !owner) {
                    throw new DominionException(Language.memberEventHandlerText.ownerOnly);
                }
            }
            cn.lunadeer.dominion.dtos.MemberDTO.delete(member.getPlayerUUID(), dominion.getId());
            Notification.info(event.getOperator(), Language.memberEventHandlerText.removeMemberSuccess, event.getMember().getPlayer().getLastKnownName(), dominion.getName());
        } catch (Exception e) {
            event.setCancelled(true);
            Notification.error(event.getOperator(), Language.memberEventHandlerText.removeMemberFailed, e.getMessage());
        }
    }

}
