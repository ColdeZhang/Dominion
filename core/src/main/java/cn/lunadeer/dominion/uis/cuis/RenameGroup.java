package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.events.group.GroupRenamedEvent;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.GroupSetting;
import cn.lunadeer.minecraftpluginutils.ColorParser;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

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
            DominionDTO dominion = DominionDTO.select(dominionName);
            if (dominion == null) {
                Notification.error(sender, Translation.Messages_DominionNotExist, dominionName);
                return;
            }
            GroupDTO group = GroupDTO.select(dominion.getId(), oldName);
            if (group == null) {
                Notification.error(sender, Translation.Messages_GroupNotExist, oldName);
                return;
            }
            new GroupRenamedEvent(operator, group, input).call();
            GroupSetting.show(sender, dominionName, ColorParser.getPlainText(input));
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
        CuiTextInput.InputCallback renameGroupCB = new renameGroupCB(player, dominion.getName(), args[2]);
        CuiTextInput view = CuiTextInput.create(renameGroupCB).setText(args[2]).title(Translation.CUI_Input_RenameGroup.trans());
        view.setSuggestCommand(Translation.Commands_Group_RenameGroupUsage.trans());
        view.open(player);
    }

}
