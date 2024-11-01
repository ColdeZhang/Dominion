package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.MemberController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberList;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.SelectPlayer;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

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
            operator.getResponse().thenAccept(result -> {
                if (Objects.equals(result.getStatus(), AbstractOperator.Result.SUCCESS)) {
                    MemberList.show(sender, dominionName);
                } else {
                    SelectPlayer.show(sender, dominionName, 1);
                }
            });
            MemberController.memberAdd(operator, dominionName, input);
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
