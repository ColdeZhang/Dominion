package cn.lunadeer.dominion.uis.tuis.dominion.manage;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.events.dominion.modify.DominionSizeChangeEvent;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.stui.ListView;
import cn.lunadeer.minecraftpluginutils.stui.components.Button;
import cn.lunadeer.minecraftpluginutils.stui.components.Line;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.lunadeer.dominion.utils.CommandUtils.playerOnly;
import static cn.lunadeer.dominion.utils.TuiUtils.getDominionNameArg_1;

public class Resize {

    public static void show(CommandSender sender, String dominionName) {
        show(sender, new String[]{"resize", dominionName});
    }

    /**
     * Show the resize UI
     *
     * @param sender The sender
     * @param args   The arguments
     *               args[1] - Dominion name
     */
    public static void show(CommandSender sender, String[] args) {
        Player player = playerOnly(sender);
        if (player == null) return;
        DominionDTO dominion = getDominionNameArg_1(player, args);

        ListView view = ListView.create(10, "/dominion resize " + dominion.getName());
        view.title(String.format(Translation.TUI_ResizeDominion.trans(), dominion.getName()));
        view.navigator(Line.create()
                .append(Button.create(Translation.TUI_Navigation_Manage).setExecuteCommand("/dominion manage " + dominion.getName()).build())
                .append(Button.create(Translation.TUI_DominionManage_InfoButton).setExecuteCommand("/dominion info " + dominion.getName()).build())
                .append(Translation.TUI_ResizeButton));

        view.add(
                Line.create()
                        .append(Translation.TUI_ResizeDominion_North)
                        .append(Button.create(Translation.TUI_ResizeDominion_Expand).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.EXPAND, BlockFace.NORTH)
                        ).build())
                        .append(Button.create(Translation.TUI_ResizeDominion_Contract).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.CONTRACT, BlockFace.NORTH)
                        ).build())
        );
        view.add(
                Line.create()
                        .append(Translation.TUI_ResizeDominion_South)
                        .append(Button.create(Translation.TUI_ResizeDominion_Expand).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.EXPAND, BlockFace.SOUTH)
                        ).build())
                        .append(Button.create(Translation.TUI_ResizeDominion_Contract).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.CONTRACT, BlockFace.SOUTH)
                        ).build())
        );
        view.add(
                Line.create()
                        .append(Translation.TUI_ResizeDominion_West)
                        .append(Button.create(Translation.TUI_ResizeDominion_Expand).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.EXPAND, BlockFace.WEST)
                        ).build())
                        .append(Button.create(Translation.TUI_ResizeDominion_Contract).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.CONTRACT, BlockFace.WEST)
                        ).build())
        );
        view.add(
                Line.create()
                        .append(Translation.TUI_ResizeDominion_East)
                        .append(Button.create(Translation.TUI_ResizeDominion_Expand).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.EXPAND, BlockFace.EAST)
                        ).build())
                        .append(Button.create(Translation.TUI_ResizeDominion_Contract).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.CONTRACT, BlockFace.EAST)
                        ).build())
        );
        view.add(
                Line.create()
                        .append(Translation.TUI_ResizeDominion_Up)
                        .append(Button.create(Translation.TUI_ResizeDominion_Expand).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.EXPAND, BlockFace.UP)
                        ).build())
                        .append(Button.create(Translation.TUI_ResizeDominion_Contract).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.CONTRACT, BlockFace.UP)
                        ).build())
        );
        view.add(
                Line.create()
                        .append(Translation.TUI_ResizeDominion_Down)
                        .append(Button.create(Translation.TUI_ResizeDominion_Expand).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.EXPAND, BlockFace.DOWN)
                        ).build())
                        .append(Button.create(Translation.TUI_ResizeDominion_Contract).setExecuteCommand(
                                getCommand(dominion.getName(), DominionSizeChangeEvent.SizeChangeType.CONTRACT, BlockFace.DOWN)
                        ).build())
        );

        view.showOn(player, 1);
    }

    public static String getCommand(String dominionName, DominionSizeChangeEvent.SizeChangeType type, BlockFace direction) {
        return "/dominion cui_resize_dominion " + dominionName + " " + type.name() + " " + direction.name();
    }

}
