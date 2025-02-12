package cn.lunadeer.dominion.utils.command;

import java.util.List;
import java.util.Objects;

public class Argument {
    private final String name;
    private final boolean required;
    private Suggestion suggestion = null;
    private String value = "";

    public Argument(String name, boolean required) {
        this(name, required, null);
    }

    public Argument(String name, String defaultValue) {
        this(name, false, null);
        this.value = defaultValue;
    }

    public Argument(String name, boolean required, Suggestion suggestion) {
        this.name = name;
        this.required = required;
        this.suggestion = suggestion;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public Suggestion getSuggestion() {
        return Objects.requireNonNullElseGet(suggestion, () -> (sender) -> List.of(Argument.this.toString()));
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }

    public String toString() {
        if (required) {
            return "<" + name + ">";
        } else {
            return "[" + name + "]";
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
