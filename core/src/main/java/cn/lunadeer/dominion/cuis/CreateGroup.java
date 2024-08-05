package cn.lunadeer.dominion.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.GroupController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.tuis.dominion.manage.group.GroupList;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class CreateGroup {

    private static class createGroupCB implements CuiTextInput.InputCallback {

        private final Player sender;
        private final String dominionName;

        public createGroupCB(Player sender, String dominionName) {
            this.sender = sender;
            this.dominionName = dominionName;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("createGroupCB.run: %s", input);
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            GroupController.createGroup(operator, dominionName, input);
            GroupList.show(sender, dominionName);
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
        CuiTextInput.InputCallback createGroupCB = new createGroupCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(createGroupCB).setText("未命名权限组").title("输入要创建的权限组名称");
        view.setSuggestCommand("/dominion group create <领地名称> <权限组名称>");
        view.open(player);
    }

}
