package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.events.member.MemberAddedEvent;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberList;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.SelectPlayer;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class MemberAdd {

    private static class memberAddCB implements CuiTextInput.InputCallback {
        private final Player sender;
        private final String dominionName;

        public memberAddCB(Player sender, String dominionName) {
            this.sender = sender;
            this.dominionName = dominionName;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("createPrivilegeCB.run: %s", input);
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            DominionDTO dominion = DominionDTO.select(dominionName);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, dominionName);
                return;
            }
            PlayerDTO player = PlayerDTO.select(input);
            if (player == null) {
                Notification.error(sender, Translation.Messages_PlayerNotExist, input);
                return;
            }
            if (new MemberAddedEvent(operator, dominion, player).call()) {
                MemberList.show(sender, dominionName);
            } else {
                SelectPlayer.show(sender, dominionName, 1);
            }
        }
    }

    public static void open(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[1]);
            return;
        }
        CuiTextInput.InputCallback createPrivilegeCB = new memberAddCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(createPrivilegeCB).setText("Steve").title(Translation.CUI_Input_AddMember.trans());
        view.setSuggestCommand(Translation.Commands_Member_DominionAddMemberUsage.trans());
        view.open(player);
    }

}
