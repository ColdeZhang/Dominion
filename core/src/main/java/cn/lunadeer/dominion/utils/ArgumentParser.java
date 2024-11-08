package cn.lunadeer.dominion.utils;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ArgumentParser {
    private final Map<String, String> arguments = new HashMap<>();

    public ArgumentParser(String[] args) {
        for (String arg : args) {
            String[] split = arg.split("=");
            if (split.length == 2) {
                arguments.put(split[0], split[1]);
            }
        }
    }

    public @Nullable String getVal(String key) {
        return arguments.get(key);
    }

    public @NotNull String getVal(String key, String def) {
        return arguments.getOrDefault(key, def);
    }

    public boolean hasKey(String key) {
        return arguments.containsKey(key);
    }

    public int getValInt(String key) throws NumberFormatException {
        return Integer.parseInt(arguments.get(key));
    }

    public boolean getValBool(String key) {
        return Boolean.parseBoolean(arguments.get(key));
    }

    public int getValInt(String key, int def) throws IllegalArgumentException {
        return arguments.containsKey(key) ? Integer.parseInt(arguments.get(key)) : def;
    }

    public boolean getValBool(String key, boolean def) throws IllegalArgumentException {
        return arguments.containsKey(key) ? Boolean.parseBoolean(arguments.get(key)) : def;
    }
}
