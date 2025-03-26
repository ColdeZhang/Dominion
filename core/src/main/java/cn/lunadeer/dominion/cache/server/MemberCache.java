package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.doos.MemberDOO;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MemberCache extends Cache {
    private final Integer serverId;

    private ConcurrentHashMap<Integer, MemberDTO> idMembers;            // Member ID -> MemberDTO
    private ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> dominionMembersMap;  // Dominion ID -> Members ID
    private ConcurrentHashMap<UUID, Map<Integer, Integer>> playerDominionMemberMap;  // Player UUID -> (Dominion ID -> Member ID)
    private ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> groupMembersMap;  // Group ID -> Members ID

    public MemberCache(Integer serverId) {
        this.serverId = serverId;
    }

    public @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull Player player) {
        return getMember(dominion, player.getUniqueId());
    }

    public @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull UUID player_uuid) {
        if (dominion == null) return null;
        if (!playerDominionMemberMap.containsKey(player_uuid)) return null;
        Integer member_id = playerDominionMemberMap.get(player_uuid).get(dominion.getId());
        if (member_id == null) return null;
        return idMembers.get(member_id);
    }

    public List<MemberDTO> getMemberBelongedDominions(@NotNull UUID player) {
        if (!playerDominionMemberMap.containsKey(player)) return new ArrayList<>();
        Collection<Integer> member_ids = playerDominionMemberMap.get(player).values();
        List<MemberDTO> members = new ArrayList<>();
        for (Integer member_id : member_ids) {
            members.add(idMembers.get(member_id));
        }
        return members;
    }

    public @NotNull List<MemberDTO> getDominionMembers(@NotNull DominionDTO dominion) {
        if (!dominionMembersMap.containsKey(dominion.getId())) return new ArrayList<>();
        List<MemberDTO> members = new ArrayList<>();
        for (Integer member_id : dominionMembersMap.get(dominion.getId())) {
            members.add(idMembers.get(member_id));
        }
        return members;
    }

    public @NotNull List<MemberDTO> getGroupMembers(@NotNull GroupDTO group) {
        if (!groupMembersMap.containsKey(group.getId())) return new ArrayList<>();
        List<MemberDTO> members = new ArrayList<>();
        for (Integer member_id : groupMembersMap.get(group.getId())) {
            members.add(idMembers.get(member_id));
        }
        return members;
    }

    @Override
    void loadExecution() throws Exception {
        idMembers = new ConcurrentHashMap<>();
        dominionMembersMap = new ConcurrentHashMap<>();
        playerDominionMemberMap = new ConcurrentHashMap<>();
        groupMembersMap = new ConcurrentHashMap<>();

        List<MemberDOO> allMembers = MemberDOO.select();
        for (MemberDOO member : allMembers) {
            DominionDTO dominion = CacheManager.instance.getDominion(member.getDomID());
            if (dominion == null || !Objects.equals(dominion.getServerId(), serverId)) continue;
            idMembers.put(member.getId(), member);
            dominionMembersMap.computeIfAbsent(member.getDomID(), k -> new CopyOnWriteArrayList<>())
                    .add(member.getId());
            playerDominionMemberMap.computeIfAbsent(member.getPlayerUUID(), k -> new HashMap<>())
                    .put(member.getDomID(), member.getId());
            if (member.getGroupId() != -1) {
                groupMembersMap.computeIfAbsent(member.getGroupId(), k -> new CopyOnWriteArrayList<>())
                        .add(member.getId());
            }
        }
    }

    @Override
    void loadExecution(Integer idToLoad) throws Exception {
        MemberDTO member = MemberDOO.select(idToLoad);
        if (member == null) return;
        MemberDTO old = idMembers.put(member.getId(), member);
        if (old != null) {
            dominionMembersMap.get(old.getDomID()).remove(old.getId());
            playerDominionMemberMap.get(old.getPlayerUUID()).remove(old.getDomID());
            if (old.getGroupId() != -1)
                groupMembersMap.get(old.getGroupId()).remove(old.getId());
        }
        dominionMembersMap.computeIfAbsent(member.getDomID(), k -> new CopyOnWriteArrayList<>())
                .add(member.getId());
        playerDominionMemberMap.computeIfAbsent(member.getPlayerUUID(), k -> new HashMap<>())
                .put(member.getDomID(), member.getId());
        if (member.getGroupId() != -1)
            groupMembersMap.computeIfAbsent(member.getGroupId(), k -> new CopyOnWriteArrayList<>())
                    .add(member.getId());
    }

    @Override
    void deleteExecution(Integer idToDelete) throws Exception {
        MemberDTO member = idMembers.remove(idToDelete);
        if (member == null) return;
        dominionMembersMap.get(member.getDomID()).remove(member.getId());
        playerDominionMemberMap.get(member.getPlayerUUID()).remove(member.getDomID());
        if (member.getGroupId() != -1)
            groupMembersMap.get(member.getGroupId()).remove(member.getId());
    }

    public Integer count() {
        return idMembers.size();
    }
}
