package cn.lunadeer.dominion.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.GroupController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.tuis.dominion.manage.group.GroupSetting;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class RenameGroup {

    private static class renameGroupCB implements CuiTextInput.InputCallback {

        private final Player sender;
        private final String dominionName;
        private final String oldName;

        public renameGroupCB(Player sender, String dominionName, String oldName) {
            this.sender = sender;
            this.dominionName = dominionName;
            this.oldName = oldName;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("renameGroupCB.run: %s", input);
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            GroupController.renameGroup(operator, dominionName, oldName, input);
            GroupSetting.show(sender, dominionName, input);
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
        CuiTextInput.InputCallback renameGroupCB = new renameGroupCB(player, dominion.getName(), args[2]);
        CuiTextInput view = CuiTextInput.create(renameGroupCB).setText(args[2]).title("输入新的权限组名称");
        view.setSuggestCommand("/dominion group rename <领地名称> <权限组旧名称> <新名称>");
        view.open(player);
    }

}
