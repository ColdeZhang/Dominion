package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.minecraftpluginutils.XLogger;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PlaceHolderApi extends PlaceholderExpansion {

    private final JavaPlugin plugin;

    public static PlaceHolderApi instance;

    public PlaceHolderApi(JavaPlugin plugin) {
        this.plugin = plugin;
        this.register();
        instance = this;
        XLogger.info("成功注册 PlaceholderAPI 扩展");
    }

    @Override
    public String onPlaceholderRequest(Player bukkitPlayer, @NotNull String params) {
        if (params.equalsIgnoreCase("group_title")) {
            GroupDTO group = Cache.instance.getPlayerUsingGroupTitle(bukkitPlayer.getUniqueId());
            if (group == null) {
                return "";
            }
            return group.getNameColoredBukkit();
        }
        return null; //
    }

    @Override
    public @NotNull String getIdentifier() {
        return "dominion";
    }

    @Override
    public @NotNull String getAuthor() {
        return "zhangyuheng";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

}
