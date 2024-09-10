package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.GroupController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.GroupList;
import cn.lunadeer.minecraftpluginutils.ColorParser;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

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
            GroupController.createGroup(operator, dominionName, ColorParser.getPlainText(input), input);
            GroupList.show(sender, dominionName);
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
        CuiTextInput.InputCallback createGroupCB = new createGroupCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(createGroupCB).setText(Translation.Commands_Group_NewGroupName.trans()).title(Translation.CUI_Input_CreateGroup.trans());
        view.setSuggestCommand(Translation.Commands_Group_CreateGroupUsage.trans());
        view.open(player);
    }

}
