package cn.lunadeer.dominion.utils.stui.components.buttons;

import net.kyori.adventure.text.event.ClickEvent;

public class CopyButton extends Button {
    public CopyButton(String text, String copyText) {
        super(text);
        this.action = ClickEvent.Action.COPY_TO_CLIPBOARD;
        this.clickExecute = copyText;
    }
}
