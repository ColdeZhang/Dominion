package cn.lunadeer.dominion.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.tuis.dominion.DominionManage;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

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
            DominionController.setJoinMessage(operator, input, dominionName);
            DominionManage.show(sender, new String[]{"manage", dominionName});
        }
    }

    public static void open(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Notification.error(sender, "领地不存在");
            return;
        }
        CuiTextInput.InputCallback editJoinMessageCB = new editJoinMessageCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(editJoinMessageCB).setText(dominion.getJoinMessage()).title("编辑欢迎提示语");
        view.setSuggestCommand("/dominion set_enter_msg <提示语> [领地名称]");
        view.open(player);
    }
}
