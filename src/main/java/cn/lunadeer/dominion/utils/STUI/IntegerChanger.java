package cn.lunadeer.dominion.utils.STUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;

public class IntegerChanger {

    private final Integer value;
    private final String changeCommand;
    private Integer pageNumber;

    private IntegerChanger(Integer value, String changeCommand) {
        this.value = value;
        this.changeCommand = changeCommand;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public static IntegerChanger create(Integer value, String changeCommand) {
        return new IntegerChanger(value, changeCommand);
    }

    public TextComponent build() {
        TextComponent plus = Component.text("+").clickEvent(
                ClickEvent.clickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        changeCommand + " " + (value + 1) + (pageNumber == null ? "" : " " + pageNumber)
                )
        ).hoverEvent(
                Component.text("增加1").asHoverEvent()
        ).color(ViewStyles.action_color);
        TextComponent minus = Component.text("-").clickEvent(
                ClickEvent.clickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        changeCommand + " " + (value - 1) + (pageNumber == null ? "" : " " + pageNumber)
                )
        ).hoverEvent(
                Component.text("减少1").asHoverEvent()
        ).color(ViewStyles.action_color);
        TextComponent plus10 = Component.text(">>").clickEvent(
                ClickEvent.clickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        changeCommand + " " + (value + 10) + (pageNumber == null ? "" : " " + pageNumber)
                )
        ).hoverEvent(
                Component.text("增加10").asHoverEvent()
        ).color(ViewStyles.action_color);
        TextComponent minus10 = Component.text("<<").clickEvent(
                ClickEvent.clickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        changeCommand + " " + (value - 10) + (pageNumber == null ? "" : " " + pageNumber)
                )
        ).hoverEvent(
                Component.text("减少10").asHoverEvent()
        ).color(ViewStyles.action_color);
        return Component.text().append(minus).append(minus10).append(Component.text(value.toString())).append(plus10).append(plus).build();
    }
}
