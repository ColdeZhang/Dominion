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
            DominionController.setMapColor(operator, input, dominionName);
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
        CuiTextInput.InputCallback setMapColorCB = new SetMapColor.setMapColorCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(setMapColorCB).setText(dominion.getColor()).title("输入卫星地图地块颜色（16进制）");
        view.setSuggestCommand("/dominion set_map_color <颜色> [领地名称]");
        view.open(player);
    }

}
