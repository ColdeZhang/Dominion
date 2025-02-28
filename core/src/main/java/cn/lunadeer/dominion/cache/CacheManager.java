package cn.lunadeer.dominion.cache;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.configuration.Configuration;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {
    private final Cache thisServerCache;
    private final Map<Integer, Cache> otherServerCaches;

    public CacheManager() {
        this.thisServerCache = new Cache(Configuration.multiServer.serverId);
        this.otherServerCaches = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this.thisServerCache, Dominion.instance);
    }
}
