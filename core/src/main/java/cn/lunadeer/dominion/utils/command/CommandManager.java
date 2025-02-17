package cn.lunadeer.dominion.utils.command;

import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages the registration and execution of commands.
 * <p>
 * CommandManager is a class that manages the command registration and execution.
 * This is used to create command system such as:
 * <blockquote><pre>
 * /rootCommand subCommand <arg1> <arg2> [arg3] ...
 * /rootCommand subCommand2 <arg1> [arg3] ...
 * ...
 * </pre></blockquote>
 */
public class CommandManager implements TabExecutor {

    private static String rootCommand;

    /**
     * Constructs a CommandManager with the specified root command.
     *
     * @param rootCommand The root command to be managed, should start with a slash.
     */
    public CommandManager(String rootCommand) {
        CommandManager.rootCommand = "/" + rootCommand;
        Objects.requireNonNull(Bukkit.getPluginCommand(rootCommand)).setExecutor(this);
        XLogger.debug("Registered {0} commands.", commands.size());
    }

    /**
     * Retrieves the root command.
     *
     * @return The root command.
     */
    public static String getRootCommand() {
        return rootCommand;
    }

    private static final Map<String, SecondaryCommand> commands = new HashMap<>();

    /**
     * Registers a secondary command.
     *
     * @param command The secondary command to be registered.
     */
    public static void registerCommand(SecondaryCommand command) {
        commands.put(command.getCommand(), command);
    }

    /**
     * Unregisters a secondary command.
     *
     * @param command The secondary command to be unregistered.
     */
    public static void unregisterCommand(SecondaryCommand command) {
        commands.remove(command.getCommand());
    }

    /**
     * Unregisters a secondary command by its name.
     *
     * @param command The name of the secondary command to be unregistered.
     */
    public static void unregisterCommand(String command) {
        commands.remove(command);
    }

    /**
     * Retrieves a secondary command by its name.
     *
     * @param command The name of the secondary command.
     * @return The corresponding SecondaryCommand object, or null if not found.
     */
    public static SecondaryCommand getCommand(String command) {
        return commands.get(command);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            return true;
        }
        SecondaryCommand cmd = getCommand(strings[0]);
        if (cmd == null) {
            return true;
        }
        cmd.run(commandSender, strings);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return new ArrayList<>(commands.keySet());
        }
        SecondaryCommand cmd = getCommand(strings[0]);
        if (cmd == null) {
            return null;
        }
        List<Argument> args = cmd.getArguments();
        if (strings.length - 1 > args.size()) {
            return null;
        }
        for (int i = 1; i < strings.length - 1; i++) {
            args.get(i - 1).setValue(strings[i]);
        }
        for (Argument arg : args) {
            if (arg instanceof ConditionalArgument cond) {
                for (Integer key : cond.getConditionArguments().keySet()) {
                    if (key < strings.length - 2) {
                        cond.setConditionArguments(key, strings[key + 1]);
                    }
                }
            }
        }
        return args.get(strings.length - 2).getSuggestion().get(commandSender);
    }
}
