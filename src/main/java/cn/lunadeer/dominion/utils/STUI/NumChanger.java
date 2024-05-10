package cn.lunadeer.dominion.utils.STUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;

public class NumChanger {

    private final Double value;
    private final String changeCommand;
    private Integer pageNumber;
    private Double step;

    private NumChanger(Double value, String changeCommand) {
        this.value = value;
        this.changeCommand = changeCommand;
        this.step = 1.0;
    }

    private NumChanger(Double value, String changeCommand, Double step) {
        this.value = value;
        this.changeCommand = changeCommand;
        this.step = step;
    }

    public NumChanger setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public static NumChanger create(Double value, String changeCommand) {
        return new NumChanger(value, changeCommand);
    }

    public static NumChanger create(Double value, String changeCommand, Double step) {
        return new NumChanger(value, changeCommand, step);
    }

    public static NumChanger create(Float value, String changeCommand) {
        return new NumChanger(value.doubleValue(), changeCommand);
    }

    public static NumChanger create(Float value, String changeCommand, Double step) {
        return new NumChanger(value.doubleValue(), changeCommand, step);
    }

    public static NumChanger create(Integer value, String changeCommand) {
        return new NumChanger(value.doubleValue(), changeCommand);
    }

    public static NumChanger create(Integer value, String changeCommand, Double step) {
        return new NumChanger(value.doubleValue(), changeCommand, step);
    }

    private static String intIfNoDecimal(Double value) {
        if (value % 1 == 0) {
            return String.valueOf(value.intValue());
        }
        return String.valueOf(value);
    }

    public TextComponent build() {
        TextComponent plus = Component.text("+").clickEvent(
                ClickEvent.clickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        changeCommand + " " + intIfNoDecimal(value + step) + (pageNumber == null ? "" : " " + pageNumber)
                )
        ).hoverEvent(
                Component.text("增加" + intIfNoDecimal(step)).asHoverEvent()
        ).color(ViewStyles.action_color);
        TextComponent minus = Component.text("-").clickEvent(
                ClickEvent.clickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        changeCommand + " " + intIfNoDecimal(value - step) + (pageNumber == null ? "" : " " + pageNumber)
                )
        ).hoverEvent(
                Component.text("减少" + intIfNoDecimal(step)).asHoverEvent()
        ).color(ViewStyles.action_color);
        TextComponent plus10 = Component.text(">>").clickEvent(
                ClickEvent.clickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        changeCommand + " " + intIfNoDecimal(value + step * 10) + (pageNumber == null ? "" : " " + pageNumber)
                )
        ).hoverEvent(
                Component.text("增加" + intIfNoDecimal(step * 10)).asHoverEvent()
        ).color(ViewStyles.action_color);
        TextComponent minus10 = Component.text("<<").clickEvent(
                ClickEvent.clickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        changeCommand + " " + intIfNoDecimal(value - step * 10) + (pageNumber == null ? "" : " " + pageNumber)
                )
        ).hoverEvent(
                Component.text("减少" + intIfNoDecimal(step * 10)).asHoverEvent()
        ).color(ViewStyles.action_color);
        return Component.text()
                .append(minus10).append(minus)
                .append(Component.text(" "))
                .append(Component.text(intIfNoDecimal(value)))
                .append(Component.text(" "))
                .append(plus).append(plus10).build();
    }
}
