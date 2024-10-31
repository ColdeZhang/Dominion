package cn.lunadeer.dominion.utils.Residence;

import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.Notification;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResCommands implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Notification.warn(commandSender, Translation.Commands_Residence_Command);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
