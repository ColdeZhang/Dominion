package cn.lunadeer.dominion.cuis;

import cn.lunadeer.dominion.controllers.AbstractOperator;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.controllers.PrivilegeController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.tuis.dominion.manage.member.MemberList;
import cn.lunadeer.dominion.tuis.dominion.manage.member.SelectPlayer;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static cn.lunadeer.dominion.commands.Apis.playerOnly;

public class CreatePrivilege {

    private static class createPrivilegeCB implements CuiTextInput.InputCallback {
        private final Player sender;
        private final String dominionName;

        public createPrivilegeCB(Player sender, String dominionName) {
            this.sender = sender;
            this.dominionName = dominionName;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("createPrivilegeCB.run: %s", input);
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            operator.getResponse().thenAccept(result -> {
                if (Objects.equals(result.getStatus(), AbstractOperator.Result.SUCCESS)){
                    MemberList.show(sender, new String[]{"privilege_list", dominionName});
                } else {
                    SelectPlayer.show(sender, new String[]{"select_player_create_privilege", dominionName});
                }
            });
            PrivilegeController.createPrivilege(operator, input, dominionName);
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
        CuiTextInput.InputCallback createPrivilegeCB = new createPrivilegeCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(createPrivilegeCB).setText("Steve").title("输入玩家名称以添加为成员");
        view.setSuggestCommand("/dominion create_privilege <玩家名称> [领地名称]");
        view.open(player);
    }

}
