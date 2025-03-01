package cn.lunadeer.dominion.cache;

import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Cache implements Listener {
    private final int serverId;

    private ConcurrentHashMap<Integer, GroupDTO> idGroups;                     // LOAD: Group ID -> GroupDTO
    private ConcurrentHashMap<Integer, List<Integer>> dominionToGroupsMap;     // BUILD: Dominion ID -> List of GroupDTO

    private ConcurrentHashMap<Integer, MemberDTO> idMembers;                    // LOAD: Member ID -> MemberDTO
    private ConcurrentHashMap<Integer, List<MemberDTO>> dominionToMembersMap;   // BUILD: Dominion ID -> List of MemberDTO
    private ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, MemberDTO>> playerToMembersMap;    // Player UUID -> Map of Dominion ID -> MemberDTO

    private final Map<UUID, Integer> playerCurrentDominionId = new HashMap<>(); // 玩家当前所在领地

    private final AtomicLong _last_update_member = new AtomicLong(0);
    private final AtomicBoolean _update_member_is_scheduled = new AtomicBoolean(false);

    private final AtomicLong _last_update_group = new AtomicLong(0);
    private final AtomicBoolean _update_group_is_scheduled = new AtomicBoolean(false);

    private boolean recheckPlayerState = false; // 是否需要重新检查玩家状态（发光、飞行）

    public static final long UPDATE_INTERVAL = 1000 * 4;


    public Cache(int serverId) {
        this.serverId = serverId;
    }

    public Integer getServerId() {
        return serverId;
    }


}
