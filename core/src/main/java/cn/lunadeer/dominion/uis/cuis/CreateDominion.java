package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.events.dominion.DominionCreateEvent;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static cn.lunadeer.dominion.utils.CommandUtils.autoPoints;
import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class CreateDominion {

    private static class createDominionCB implements CuiTextInput.InputCallback {
        private final Player sender;

        public createDominionCB(Player sender) {
            this.sender = sender;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("createDominionCB.run: %s", input);

            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            Map<Integer, Location> points = autoPoints(sender);
            if (new DominionCreateEvent(operator, input, sender.getUniqueId(), points.get(0), points.get(1), null).call()) {
                DominionManage.show(sender, new String[]{"manage", input});
            }
        }
    }

    public static void open(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        CuiTextInput.InputCallback createDominionCB = new createDominionCB(player);
        CuiTextInput view = CuiTextInput.create(createDominionCB).setText(Translation.Commands_NewDominionName.trans()).title(Translation.CUI_Input_CreateDominion.trans());
        view.setSuggestCommand(Translation.Commands_Dominion_AutoCreateDominionUsage.trans());
        view.open(player);
    }
}
