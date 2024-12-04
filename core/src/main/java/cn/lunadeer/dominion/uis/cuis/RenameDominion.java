package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.events.dominion.modify.DominionRenameEvent;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class RenameDominion {

    private static class renameDominionCB implements CuiTextInput.InputCallback {
        private final Player sender;
        private final String oldName;

        public renameDominionCB(Player sender, String oldName) {
            this.sender = sender;
            this.oldName = oldName;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("renameDominionCB.run: %s", input);
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            DominionDTO dominion = DominionDTO.select(oldName);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, oldName);
                return;
            }
            if (new DominionRenameEvent(operator, dominion, input).call()) {
                DominionManage.show(sender, new String[]{"manage", input});
            }
        }
    }

    public static void open(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        CuiTextInput.InputCallback renameDominionCB = new renameDominionCB(player, args[1]);
        CuiTextInput view = CuiTextInput.create(renameDominionCB).setText(args[1]).title(Translation.CUI_Input_RenameDominion.trans());
        view.setSuggestCommand(Translation.Commands_Dominion_RenameDominionUsage.trans());
        view.open(player);
    }
}
