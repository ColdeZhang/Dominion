package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.doos.GroupDOO;
import cn.lunadeer.dominion.doos.MemberDOO;
import cn.lunadeer.dominion.events.group.*;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static cn.lunadeer.dominion.misc.Asserts.*;


public class GroupEventHandler implements Listener {

    public static class GroupEventHandlerText extends ConfigurationPart {
        public String ownerOnly = "Only the owner can manage admin group.";
        public String setFlagSuccess = "Set group {0} flag {1} to {2} successfully.";
        public String setFlagFailed = "Failed to set group flag, reason: {3}";

        public String createGroupSuccess = "Group {0} created successfully.";
        public String createGroupFailed = "Failed to create group, reason: {0}";

        public String deleteGroupSuccess = "Group {0} deleted successfully.";
        public String deleteGroupFailed = "Failed to delete group, reason: {0}";

        public String renameGroupSuccess = "Group {0} renamed to {1} successfully.";
        public String renameGroupFailed = "Failed to rename group, reason: {0}";

        public String addMemberSuccess = "Member {0} added to group {1} successfully.";
        public String addMemberFailed = "Failed to add member to group, reason: {0}";

        public String removeMemberSuccess = "Member {0} removed from group {1} successfully.";
        public String removeMemberFailed = "Failed to remove member from group, reason: {0}";

    }

    public GroupEventHandler(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGroupSetFlagEvent(GroupSetFlagEvent event) {
        if (event.isCancelled()) return;
        try {
            DominionDTO dominion = event.getDominion();
            if (event.getFlag().equals(Flags.ADMIN)) {
                assertDominionOwner(event.getOperator(), dominion);
            } else {
                assertDominionAdmin(event.getOperator(), dominion);
            }
            assertGroupBelongDominion(event.getGroup(), dominion);
            GroupDTO group = event.getGroup();
            group.setFlagValue(event.getFlag(), event.getNewValue());
            Notification.info(event.getOperator(), Language.groupEventHandlerText.setFlagSuccess, group.getNamePlain(), event.getFlag().getDisplayName(), event.getNewValue());
        } catch (Exception e) {
            event.setCancelled(true);
            Notification.error(event.getOperator(), Language.groupEventHandlerText.setFlagFailed, e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGroupCreateEvent(GroupCreateEvent event) {
        if (event.isCancelled()) return;
        try {
            assertDominionOwner(event.getOperator(), event.getDominion());
            assertGroupName(event.getDominion(), event.getGroupNamePlain());
            GroupDTO group = GroupDOO.create(event.getGroupNameColored(), event.getDominion());
            event.setGroup(group);
            Notification.info(event.getOperator(), Language.groupEventHandlerText.createGroupSuccess, event.getGroupNamePlain());
        } catch (Exception e) {
            event.setCancelled(true);
            Notification.error(event.getOperator(), Language.groupEventHandlerText.createGroupFailed, e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGroupDeleteEvent(GroupDeleteEvent event) {
        if (event.isCancelled()) return;
        try {
            assertDominionOwner(event.getOperator(), event.getDominion());
            assertGroupBelongDominion(event.getGroup(), event.getDominion());
            GroupDOO.deleteById(event.getGroup().getId());
            Notification.info(event.getOperator(), Language.groupEventHandlerText.deleteGroupSuccess, event.getGroup().getNamePlain());
        } catch (Exception e) {
            event.setCancelled(true);
            Notification.error(event.getOperator(), Language.groupEventHandlerText.deleteGroupFailed, e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGroupRenameEvent(GroupRenamedEvent event) {
        if (event.isCancelled()) return;
        try {
            assertDominionOwner(event.getOperator(), event.getDominion());
            assertGroupBelongDominion(event.getGroup(), event.getDominion());
            assertGroupName(event.getDominion(), event.getNewNamePlain());
            GroupDTO group = event.getGroup().setName(event.getNewNameColored());
            event.setGroup(group);
            Notification.info(event.getOperator(), Language.groupEventHandlerText.renameGroupSuccess, event.getOldNamePlain(), event.getNewNamePlain());
        } catch (Exception e) {
            event.setCancelled(true);
            Notification.error(event.getOperator(), Language.groupEventHandlerText.renameGroupFailed, e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGroupAddMemberEvent(GroupAddMemberEvent event) {
        if (event.isCancelled()) return;
        try {
            if (event.getGroup().getFlagValue(Flags.ADMIN) || event.getMember().getFlagValue(Flags.ADMIN)) {
                assertDominionOwner(event.getOperator(), event.getDominion());
            } else {
                assertDominionAdmin(event.getOperator(), event.getDominion());
            }
            assertMemberBelongDominion(event.getMember(), event.getDominion());
            assertGroupBelongDominion(event.getGroup(), event.getDominion());
            MemberDTO member = ((MemberDOO) event.getMember()).setGroupId(event.getGroup().getId());
            event.setMember(member);
            Notification.info(event.getOperator(), Language.groupEventHandlerText.addMemberSuccess, event.getMember().getPlayer().getLastKnownName(), event.getGroup().getNamePlain());
        } catch (Exception e) {
            event.setCancelled(true);
            Notification.error(event.getOperator(), Language.groupEventHandlerText.addMemberFailed, e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGroupRemoveMemberEvent(GroupRemoveMemberEvent event) {
        if (event.isCancelled()) return;
        try {
            if (event.getGroup().getFlagValue(Flags.ADMIN)) {
                assertDominionOwner(event.getOperator(), event.getDominion());
            } else {
                assertDominionAdmin(event.getOperator(), event.getDominion());
            }
            assertGroupBelongDominion(event.getGroup(), event.getDominion());
            assertMemberBelongDominion(event.getMember(), event.getDominion());
            MemberDTO member = ((MemberDOO) event.getMember()).setGroupId(-1);
            event.setMember(member);
            Notification.info(event.getOperator(), Language.groupEventHandlerText.removeMemberSuccess, event.getMember().getPlayer().getLastKnownName(), event.getGroup().getNamePlain());
        } catch (Exception e) {
            event.setCancelled(true);
            Notification.error(event.getOperator(), Language.groupEventHandlerText.removeMemberFailed, e.getMessage());
        }
    }

}
