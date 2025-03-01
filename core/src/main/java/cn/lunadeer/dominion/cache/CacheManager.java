package cn.lunadeer.dominion.cache;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public enum ReLoadType {
        DOMINION,
        MEMBER,
        GROUP
    }

    public Cache getCache() {
        return thisServerCache;
    }

    public Map<Integer, Cache> getOtherServerCaches() {
        return otherServerCaches;
    }

    public void reloadCache(@NotNull ReLoadType type, @Nullable Integer idToLoad) {
        reloadCache(thisServerCache, type, idToLoad);
    }

    public void reloadServerCache(@NotNull Integer serverId, @NotNull ReLoadType type, @Nullable Integer idToLoad) {
        if (!otherServerCaches.containsKey(serverId)) {
            XLogger.debug("Server cache not found for serverId: {0}", serverId);
            return;
        }
        reloadCache(otherServerCaches.get(serverId), type, idToLoad);
    }

    private static void reloadCache(@NotNull Cache cache, @NotNull ReLoadType type, @Nullable Integer idToLoad) {
        switch (type) {
            case DOMINION:
                // todo
                break;
            case MEMBER:
                // todo
                break;
            case GROUP:
                // todo
                break;
        }
    }
}
