package cn.lunadeer.dominion.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.tuis.dominion.DominionManage;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

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
            DominionController.rename(operator, oldName, input);
            DominionManage.show(sender, new String[]{"manage", input});
        }
    }

    public static void open(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        CuiTextInput.InputCallback renameDominionCB = new renameDominionCB(player, args[1]);
        CuiTextInput view = CuiTextInput.create(renameDominionCB).setText(args[1]).title("领地重命名");
        view.setSuggestCommand("/dominion rename <原领地名称> <新领地名称>");
        view.open(player);
    }
}
