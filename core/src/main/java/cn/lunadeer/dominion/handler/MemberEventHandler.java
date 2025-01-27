package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.dtos.MemberDTO;
import cn.lunadeer.dominion.events.member.MemberAddedEvent;
import cn.lunadeer.dominion.events.member.MemberRemovedEvent;
import cn.lunadeer.dominion.managers.Translation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static cn.lunadeer.dominion.utils.ControllerUtils.*;

public class MemberEventHandler implements Listener {

    public MemberEventHandler(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMemberAddEvent(MemberAddedEvent event) {
        String player_name = event.getPlayer().getLastKnownName();
        String dominionName = event.getDominion().getName();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_AddMemberSuccess, player_name, dominionName);
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_AddMemberFailed, player_name, dominionName);
        if (notAdminOrOwner(event.getOperator(), event.getDominion())) {
            event.setCancelledAdnComplete(true);
            return;
        }
        if (event.getOperator().getUniqueId().equals(event.getPlayer().getUuid())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_OwnerCannotBeMember, player_name, dominionName);
            return;
        }
        if (!event.isCancelled()) {
            MemberDTO member = MemberDTO.insert(new MemberDTO(event.getPlayer().getUuid(), event.getDominion()));
            event.setMember(member);
        }
        event.getOperator().completeResult();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMemberRemoveEvent(MemberRemovedEvent event) {
        String player_name = event.getMember().getPlayer().getLastKnownName();
        String dominionName = event.getDominion().getName();
        event.getOperator().addResultHeader(AbstractOperator.ResultType.SUCCESS, Translation.Messages_RemoveMemberSuccess, player_name, dominionName);
        event.getOperator().addResultHeader(AbstractOperator.ResultType.FAILURE, Translation.Messages_RemoveMemberFailed, player_name, dominionName);
        if (notAdminOrOwner(event.getOperator(), event.getDominion())) {
            event.setCancelledAdnComplete(true);
            return;
        }
        if (isAdmin(event.getMember()) && notOwner(event.getOperator(), event.getDominion())) {
            event.setCancelled(true, AbstractOperator.ResultType.FAILURE, Translation.Messages_NotDominionOwnerForRemoveAdmin, dominionName);
        }
        if (!event.isCancelled()) {
            MemberDTO.delete(event.getMember().getPlayerUUID(), event.getDominion().getId());
        }
        event.getOperator().completeResult();
    }

}
