package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.controllers.DominionController;
import cn.lunadeer.dominion.controllers.PrivilegeController;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.tuis.DominionManage;
import cn.lunadeer.dominion.tuis.DominionPrivilegeList;
import cn.lunadeer.dominion.tuis.SelectPlayer;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static cn.lunadeer.dominion.commands.Apis.autoPoints;
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
        public void handleData(String input) {
            XLogger.debug("renameDominionCB.run: %s", input);
            DominionController.rename(sender, oldName, input);
            DominionManage.show(sender, new String[]{"manage", input});
        }
    }

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
            DominionController.setJoinMessage(sender, input, dominionName);
            DominionManage.show(sender, new String[]{"manage", dominionName});
        }
    }

    private static class editLeaveMessageCB implements CuiTextInput.InputCallback {
        private final Player sender;
        private final String dominionName;

        public editLeaveMessageCB(Player sender, String dominionName) {
            this.sender = sender;
            this.dominionName = dominionName;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("editLeaveMessageCB.run: %s", input);
            DominionController.setLeaveMessage(sender, input, dominionName);
            DominionManage.show(sender, new String[]{"manage", dominionName});
        }
    }

    private static class createDominionCB implements CuiTextInput.InputCallback {
        private final Player sender;

        public createDominionCB(Player sender) {
            this.sender = sender;
        }

        @Override
        public void handleData(String input) {
            XLogger.debug("createDominionCB.run: %s", input);
            autoPoints(sender);
            Map<Integer, Location> points = Dominion.pointsSelect.get(sender.getUniqueId());
            if (points == null || points.get(0) == null || points.get(1) == null) {
                Notification.error(sender, "自动选点失败");
                return;
            }
            if (DominionController.create(sender, input, points.get(0), points.get(1)) != null) {
                Notification.info(sender, "成功创建: %s", input);
                DominionManage.show(sender, new String[]{"list"});
            } else {
                Notification.error(sender, "创建领地失败");
            }
        }
    }

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
            if (PrivilegeController.createPrivilege(sender, input, dominionName)) {
                DominionPrivilegeList.show(sender, new String[]{"privilege_list", dominionName});
            } else {
                SelectPlayer.show(sender, new String[]{"select_player_create_privilege", dominionName});
            }
        }
    }

    public static void RenameDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        CuiTextInput.InputCallback renameDominionCB = new renameDominionCB(player, args[1]);
        CuiTextInput view = CuiTextInput.create(renameDominionCB).setText(args[1]).title("领地重命名");
        view.open(player);
    }

    public static void EditJoinMessage(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Notification.error(sender, "领地不存在");
            return;
        }
        CuiTextInput.InputCallback editJoinMessageCB = new editJoinMessageCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(editJoinMessageCB).setText(dominion.getJoinMessage()).title("编辑欢迎提示语");
        view.open(player);
    }

    public static void EditLeaveMessage(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Notification.error(sender, "领地不存在");
            return;
        }
        CuiTextInput.InputCallback editLeaveMessageCB = new editLeaveMessageCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(editLeaveMessageCB).setText(dominion.getLeaveMessage()).title("编辑离开提示语");
        view.open(player);
    }

    public static void CreateDominion(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        CuiTextInput.InputCallback createDominionCB = new createDominionCB(player);
        CuiTextInput view = CuiTextInput.create(createDominionCB).setText("未命名领地").title("输入要创建的领地名称");
        view.open(player);
    }

    public static void CreatePrivilege(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionDTO.select(args[1]);
        if (dominion == null) {
            Notification.error(sender, "领地不存在");
            return;
        }
        CuiTextInput.InputCallback createPrivilegeCB = new createPrivilegeCB(player, dominion.getName());
        CuiTextInput view = CuiTextInput.create(createPrivilegeCB).setText("Steve").title("输入玩家名称以添加为成员");
        view.open(player);
    }
}
