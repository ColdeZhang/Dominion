package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class OpenCUI {
    private static class renameDominionCB implements CuiTextInput.InputCallback {

        private final Player sender;
        private final String oldName;

        public renameDominionCB(Player sender, String oldName) {
            this.sender = sender;
            this.oldName = oldName;
        }

        @Override
        public void run(String input) {
            Dominion.logger.debug("renameDominionCB.run: %s", input);
            DominionController.rename(sender, oldName, input);
        }
    }

    public static void RenameDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        CuiTextInput.InputCallback renameDominionCB = new renameDominionCB(player, args[1]);
        CuiTextInput view = CuiTextInput.create(renameDominionCB).setText(args[1]).title("领地重命名");
        view.open(player);
    }
}
