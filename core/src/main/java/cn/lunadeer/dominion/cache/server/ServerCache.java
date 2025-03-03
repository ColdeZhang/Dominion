package cn.lunadeer.dominion.cache.server;

import org.jetbrains.annotations.NotNull;

public class ServerCache {

    private final int serverId;

    private final DominionCache dominionCache = new DominionCache();
    private final MemberCache memberCache = new MemberCache();
    private final GroupCache groupCache = new GroupCache();

    public ServerCache(int serverId) {
        this.serverId = serverId;
        this.dominionCache.load();
        this.memberCache.load();
        this.groupCache.load();
    }

    public Integer getServerId() {
        return serverId;
    }

    public @NotNull DominionCache getDominionCache() {
        return dominionCache;
    }

    public @NotNull MemberCache getMemberCache() {
        return memberCache;
    }

    public @NotNull GroupCache getGroupCache() {
        return groupCache;
    }
}
