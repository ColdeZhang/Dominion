package cn.lunadeer.dominion.utils.STUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class Button {

    public static TextComponent create(String text, String command) {
        return Component.text("[" + text + "]", ViewStyles.action_color)
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, command));
    }
}
