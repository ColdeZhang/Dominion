package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.events.group.*;
import cn.lunadeer.dominion.managers.Translation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static cn.lunadeer.dominion.utils.ControllerUtils.notAdminOrOwner;
import static cn.lunadeer.dominion.utils.ControllerUtils.notOwner;

public class GroupEventHandler implements Listener {

    public GroupEventHandler(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGroupCreateEvent(GroupCreateEvent event) {
        String groupName = event.getGroupNamePlain();
        String dominionName = event.getDominion().getName();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_CreateGroupSuccess, groupName, dominionName);
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_CreateGroupFailed, groupName, dominionName);
        if (notOwner(event.getOperator(), event.getDominion())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwner, dominionName);
            return;
        }
        if (groupName.contains(" ")) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_GroupNameInvalid);
            return;
        }
        GroupDTO group = GroupDTO.select(event.getDominion().getId(), groupName);
        if (group != null) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_GroupNameExist, dominionName, groupName);
        }
        if (!event.isCancelled()) {
            group = GroupDTO.create(event.getGroupNameColored(), event.getDominion());
            if (group == null) {
                event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            } else {
                event.setGroup(group);
            }
        }
        event.getOperator().completeResult();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGroupDeleteEvent(GroupDeleteEvent event) {
        String groupName = event.getGroup().getNamePlain();
        String dominionName = event.getDominion().getName();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_DeleteGroupSuccess, groupName);
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_DeleteGroupFailed, groupName);
        if (notOwner(event.getOperator(), event.getDominion())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwner, dominionName);
            return;
        }
        if (!event.isCancelled()) {
            GroupDTO.deleteById(event.getGroup().getId());
        }
        event.getOperator().completeResult();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGroupRenameEvent(GroupRenamedEvent event) {
        String oldName = event.getGroupBefore().getNamePlain();
        String newName = event.getNewNamePlain();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_RenameGroupSuccess, oldName, newName);
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_RenameGroupFailed, oldName, newName);
        if (newName.contains(" ")) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_GroupNameInvalid);
            return;
        }
        DominionDTO dominion = cn.lunadeer.dominion.dtos.DominionDTO.select(event.getGroupBefore().getDomID());
        if (dominion == null) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Commands_Title_GroupDominionNotExist, oldName);
            return;
        }
        if (notOwner(event.getOperator(), dominion)) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwner, dominion.getName());
            return;
        }
        GroupDTO group = GroupDTO.select(dominion.getId(), oldName);
        if (group == null) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_GroupNotExist, dominion.getName(), oldName);
            return;
        }
        if (!event.isCancelled()) {
            group = group.setName(event.getNewNameColored());
            if (group == null) {
                event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            } else {
                event.setGroupAfter(group);
            }
        }
        event.getOperator().completeResult();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGroupAddMemberEvent(GroupAddMemberEvent event) {
        PlayerDTO player = event.getMember().getPlayer();
        String playerName = player.getLastKnownName();
        String groupName = event.getGroup().getNamePlain();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_AddGroupMemberSuccess, playerName, groupName);
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_AddGroupMemberFailed, playerName, groupName);
        DominionDTO dominion = cn.lunadeer.dominion.dtos.DominionDTO.select(event.getGroup().getDomID());
        if (dominion == null) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Commands_Title_GroupDominionNotExist, groupName);
            return;
        }
        if (notAdminOrOwner(event.getOperator(), dominion)) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NoPermissionForGroupMember, dominion.getName(), groupName);
            return;
        }
        cn.lunadeer.dominion.dtos.MemberDTO member = cn.lunadeer.dominion.dtos.MemberDTO.select(player.getUuid(), dominion.getId());
        if (member == null) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_PlayerNotDominionMember, playerName, dominion.getName());
            return;
        }
        if (member.getGroupId().equals(event.getGroup().getId())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_PlayerAlreadyInGroup, playerName, groupName);
            return;
        }
        // only owner can add a member to an admin group
        if (notOwner(event.getOperator(), dominion) && event.getGroup().getAdmin()) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwnerForGroupMember, dominion.getName());
            return;
        }
        // only owner can add an admin to a group
        if (notOwner(event.getOperator(), dominion) && member.getAdmin()) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_PlayerIsOwnerForGroupMember, playerName, dominion.getName());
            return;
        }

        if (!event.isCancelled()) {
            member = member.setGroupId(event.getGroup().getId());
            if (member == null) {
                event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            }
        }
        event.getOperator().completeResult();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGroupRemoveMemberEvent(GroupRemoveMemberEvent event) {
        PlayerDTO player = event.getMember().getPlayer();
        String playerName = player.getLastKnownName();
        String groupName = event.getGroup().getNamePlain();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_RemoveGroupMemberSuccess, playerName, groupName);
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_RemoveGroupMemberFailed, playerName, groupName);
        DominionDTO dominion = cn.lunadeer.dominion.dtos.DominionDTO.select(event.getGroup().getDomID());
        if (dominion == null) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Commands_Title_GroupDominionNotExist, groupName);
            return;
        }
        if (notAdminOrOwner(event.getOperator(), dominion)) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NoPermissionForGroupMember, dominion.getName(), groupName);
            return;
        }
        if (notOwner(event.getOperator(), dominion) && event.getGroup().getAdmin()) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwnerForGroup, dominion.getName());
            return;
        }
        cn.lunadeer.dominion.dtos.MemberDTO member = cn.lunadeer.dominion.dtos.MemberDTO.select(player.getUuid(), dominion.getId());
        if (member == null) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_PlayerNotDominionMember, playerName, dominion.getName());
            return;
        }
        if (!member.getGroupId().equals(event.getGroup().getId())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_PlayerNotInGroup, playerName, groupName);
            return;
        }
        if (!event.isCancelled()) {
            member = member.setGroupId(-1);
            if (member == null) {
                event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_DatabaseError);
            }
        }
        event.getOperator().completeResult();
    }

}
