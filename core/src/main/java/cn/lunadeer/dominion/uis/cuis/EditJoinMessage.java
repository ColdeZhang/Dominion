package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.DominionInterface;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.events.dominion.modify.DominionSetMessageEvent;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class EditJoinMessage {

    private static class editJoinMessageCB implements CuiTextInput.InputCallback {
        private final Player sender;
        private final String dominionName;

        public editJoinMessageCB(Player sender, String dominionName) {
            this.sender = sender;
            this.dominionName = dominionName;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("editJoinMessageCB.run: %s", input);
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            DominionDTO dominion = DominionInterface.instance.getDominion(dominionName);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, dominionName);
                return;
            }
            new DominionSetMessageEvent(operator, dominion, DominionSetMessageEvent.MessageChangeType.ENTER, input).callEvent();
            DominionManage.show(sender, new String[]{"manage", dominionName});
        }
    }

    public static void open(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionInterface.instance.getDominion(args[1]);
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[1]);
            return;
        }
        CuiTextInput.InputCallback editJoinMessageCB = new editJoinMessageCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(editJoinMessageCB).setText(dominion.getJoinMessage()).title(Translation.CUI_Input_EditEnterMessage.trans());
        view.setSuggestCommand(Translation.Commands_Dominion_SetEnterMessageUsage.trans());
        view.open(player);
    }
}
