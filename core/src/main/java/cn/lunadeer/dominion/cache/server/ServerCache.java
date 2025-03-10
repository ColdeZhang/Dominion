package cn.lunadeer.dominion.cache.server;

import org.jetbrains.annotations.NotNull;

public class ServerCache {

    private final int serverId;

    private final DominionCache dominionCache;
    private final MemberCache memberCache;
    private final GroupCache groupCache;

    public ServerCache(int serverId) {
        this.serverId = serverId;
        this.dominionCache = new DominionCache(serverId);
        this.memberCache = new MemberCache(serverId);
        this.groupCache = new GroupCache(serverId);
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
