package cn.lunadeer.dominion.utils.stui.components.buttons;

import cn.lunadeer.dominion.utils.command.Argument;
import cn.lunadeer.dominion.utils.command.CommandManager;
import cn.lunadeer.dominion.utils.command.NoPermissionException;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public abstract class ListViewButton extends PermissionButton {

    public abstract void function(String page);

    public ListViewButton(String text) {
        super(text);
        UUID uuid = UUID.randomUUID();
        new SecondaryCommand("tui_page_btn_future_" + uuid, List.of(
                new Argument("page", "1")
        )) {
            @Override
            public void executeHandler(CommandSender sender) {
                for (String permission : permissions) {
                    if (!sender.hasPermission(permission))
                        throw new NoPermissionException(permission);
                }
                function(getArgumentValue(0));
            }
        }.hideUsage().register();
        this.action = ClickEvent.Action.RUN_COMMAND;
        this.clickExecute = CommandManager.getRootCommand() + " tui_page_btn_future_" + uuid;
    }

}
