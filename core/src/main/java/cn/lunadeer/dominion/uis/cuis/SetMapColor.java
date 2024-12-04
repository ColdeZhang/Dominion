package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.events.dominion.modify.DominionSetMapColorEvent;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class SetMapColor {

    private static class setMapColorCB implements CuiTextInput.InputCallback {
        private final Player sender;
        private final String dominionName;

        public setMapColorCB(Player sender, String dominionName) {
            this.sender = sender;
            this.dominionName = dominionName;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("editLeaveMessageCB.run: %s", input);
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            DominionDTO dominion = DominionDTO.select(dominionName);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, dominionName);
                return;
            }
            Color color = Color.fromRGB(Integer.parseInt(input, 16));
            new DominionSetMapColorEvent(operator, dominion, color).call();
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
        CuiTextInput.InputCallback setMapColorCB = new SetMapColor.setMapColorCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(setMapColorCB).setText(dominion.getColor()).title(Translation.CUI_Input_SetMapColor.trans());
        view.setSuggestCommand(Translation.Commands_Dominion_SetMapColorUsage.trans());
        view.open(player);
    }

}
