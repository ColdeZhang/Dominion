package cn.lunadeer.dominion.utils.stui.components.buttons;

import net.kyori.adventure.text.event.ClickEvent;

public class CommandButton extends Button {

    public CommandButton(String text, String command) {
        super(text);
        this.action = ClickEvent.Action.RUN_COMMAND;
        this.clickExecute = command;
    }
}
