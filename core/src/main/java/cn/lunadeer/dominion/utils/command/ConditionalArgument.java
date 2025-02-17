package cn.lunadeer.dominion.utils.command;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ConditionalArgument extends Argument {

    private final Map<Integer, String> conditionArguments = new HashMap<>();

    public abstract List<String> handelCondition(CommandSender sender);

    public ConditionalArgument(String name, List<Integer> conditionArgumentsIndex) {
        super(name, true);
        this.setSuggestion(this::handelCondition);
        for (Integer index : conditionArgumentsIndex) {
            this.conditionArguments.put(index, null);
        }
    }

    public ConditionalArgument setConditionArguments(Integer index, String value) {
        if (!conditionArguments.containsKey(index)) {
            throw new IllegalArgumentException("Index out of range.");
        }
        conditionArguments.put(index, value);
        return this;
    }

    public Map<Integer, String> getConditionArguments() {
        return conditionArguments;
    }
}
