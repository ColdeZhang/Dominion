package cn.lunadeer.dominion.cuis;

import cn.lunadeer.dominion.controllers.AbstractOperator;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.tuis.dominion.DominionManage;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;

import static cn.lunadeer.dominion.commands.Apis.autoPoints;
import static cn.lunadeer.dominion.commands.Apis.playerOnly;

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
            operator.getResponse().thenAccept(result -> {
                if (Objects.equals(result.getStatus(), AbstractOperator.Result.SUCCESS)) {
                    DominionManage.show(sender, new String[]{"list"});
                }
            });
            DominionController.create(operator, input, points.get(0), points.get(1));
        }
    }

    public static void open(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        CuiTextInput.InputCallback createDominionCB = new createDominionCB(player);
        CuiTextInput view = CuiTextInput.create(createDominionCB).setText("未命名领地").title("输入要创建的领地名称");
        view.setSuggestCommand("/dominion auto_create <领地名称>");
        view.open(player);
    }
}
