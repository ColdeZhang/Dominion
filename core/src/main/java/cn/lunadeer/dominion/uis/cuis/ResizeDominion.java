package cn.lunadeer.dominion.uis.cuis;

import cn.lunadeer.dominion.DominionInterface;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import cn.lunadeer.dominion.events.dominion.modify.DominionSizeChangeEvent;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.SizeInfo;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.scui.CuiTextInput;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;

public class ResizeDominion {

    private static class resizeDominionCB implements CuiTextInput.InputCallback {
        private final Player sender;
        private final DominionDTO dominion;
        private final DominionSizeChangeEvent.SizeChangeType type;
        private final BlockFace direction;

        public resizeDominionCB(Player sender,
                                DominionDTO dominion,
                                DominionSizeChangeEvent.SizeChangeType type,
                                BlockFace direction) {
            this.sender = sender;
            this.dominion = dominion;
            this.type = type;
            this.direction = direction;
        }

        @Override
        public void handleData(String input) {
            int size;
            try {
                size = Integer.parseInt(input);
                if (size < 1) {
                    Notification.error(sender, Translation.Commands_Dominion_SizeShouldBePositive);
                    return;
                }
            } catch (NumberFormatException e) {
                Notification.error(sender, Translation.Commands_Dominion_SizeShouldBeInteger);
                return;
            }
            BukkitPlayerOperator operator = BukkitPlayerOperator.create(sender);
            new DominionSizeChangeEvent(operator, dominion, type, direction, size).call();
            SizeInfo.show(sender, dominion.getName());
        }
    }

    /**
     * Open the resize dominion CUI
     *
     * @param sender Command sender
     * @param args   Command arguments
     *               args[1] - Dominion name
     *               args[2] - Type of resize
     *               args[3] - Direction of resize
     */
    public static void open(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = DominionInterface.instance.getDominion(args[1]);
        if (dominion == null) {
            Notification.error(sender, Translation.Messages_DominionNotExist, args[1]);
            return;
        }
        DominionSizeChangeEvent.SizeChangeType type = DominionSizeChangeEvent.SizeChangeType.valueOf(args[2]);
        String typeStr;
        if (type == DominionSizeChangeEvent.SizeChangeType.EXPAND) {
            typeStr = Translation.TUI_ResizeDominion_Expand.trans();
        } else {
            typeStr = Translation.TUI_ResizeDominion_Contract.trans();
        }
        BlockFace direction = BlockFace.valueOf(args[3]);
        String directionStr;
        switch (direction) {
            case NORTH:
                directionStr = Translation.TUI_ResizeDominion_North.trans();
                break;
            case EAST:
                directionStr = Translation.TUI_ResizeDominion_East.trans();
                break;
            case SOUTH:
                directionStr = Translation.TUI_ResizeDominion_South.trans();
                break;
            case WEST:
                directionStr = Translation.TUI_ResizeDominion_West.trans();
                break;
            case UP:
                directionStr = Translation.TUI_ResizeDominion_Up.trans();
                break;
            case DOWN:
                directionStr = Translation.TUI_ResizeDominion_Down.trans();
                break;
            default:
                Notification.error(sender, Translation.Messages_InvalidDirection);
                return;
        }
        CuiTextInput.InputCallback resizeDominionCB = new resizeDominionCB(player, dominion, type, direction);
        CuiTextInput view = CuiTextInput.create(resizeDominionCB).setText(String.valueOf(10)).title(
                Translation.CUI_Input_ResizeDominion.trans().formatted(typeStr, direction.name(), directionStr)
        );
        view.open(player);
    }

}
