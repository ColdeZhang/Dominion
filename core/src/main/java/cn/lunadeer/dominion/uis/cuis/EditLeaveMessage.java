package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class EditLeaveMessage {

    private static class editLeaveMessageCB implements CuiTextInput.InputCallback {
        private final Player sender;
        private final String dominionName;

        public editLeaveMessageCB(Player sender, String dominionName) {
            this.sender = sender;
            this.dominionName = dominionName;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("editLeaveMessageCB.run: %s", input);
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            DominionController.setLeaveMessage(operator, input, dominionName);
            DominionManage.show(sender, new String[]{"manage", dominionName});
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
        CuiTextInput.InputCallback editLeaveMessageCB = new editLeaveMessageCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(editLeaveMessageCB).setText(dominion.getLeaveMessage()).title(Translation.CUI_Input_EditLeaveMessage.trans());
        view.setSuggestCommand(Translation.Commands_Dominion_SetLeaveMessageUsage.trans());
        view.open(player);
    }
}
