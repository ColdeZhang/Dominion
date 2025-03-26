package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.doos.GroupDOO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GroupCache extends Cache {

    private final Integer serverId;
    private ConcurrentHashMap<Integer, GroupDTO> idGroups;            // Group ID -> GroupDTO
    private ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> dominionGroupsMap;  // Dominion ID -> Groups ID

    public GroupCache(Integer serverId) {
        this.serverId = serverId;
    }

    public @Nullable GroupDTO getGroup(Integer id) {
        return idGroups.get(id);
    }

    public @NotNull List<GroupDTO> getDominionGroups(DominionDTO dominion) {
        if (dominion == null) return new ArrayList<>();
        return getDominionGroups(dominion.getId());
    }

    public @NotNull List<GroupDTO> getDominionGroups(Integer dominionId) {
        if (!dominionGroupsMap.containsKey(dominionId)) return List.of();
        List<GroupDTO> groups = new ArrayList<>();
        dominionGroupsMap.get(dominionId).forEach(groupId -> groups.add(idGroups.get(groupId)));
        return groups;
    }

    @Override
    void loadExecution() throws Exception {
        idGroups = new ConcurrentHashMap<>();
        dominionGroupsMap = new ConcurrentHashMap<>();

        List<GroupDOO> allGroups = GroupDOO.select();
        for (GroupDOO group : allGroups) {
            DominionDTO dominion = CacheManager.instance.getDominion(group.getDomID());
            if (dominion == null || !Objects.equals(dominion.getServerId(), serverId)) continue;
            idGroups.put(group.getId(), group);
            dominionGroupsMap.computeIfAbsent(dominion.getId(), k -> new CopyOnWriteArrayList<>())
                    .add(group.getId());
        }
    }

    @Override
    void loadExecution(Integer idToLoad) throws Exception {
        GroupDTO group = GroupDOO.select(idToLoad);
        if (group == null) return;
        GroupDTO old = idGroups.put(group.getId(), group);
        if (old != null) {
            dominionGroupsMap.get(old.getDomID()).remove(old.getId());
        }
        dominionGroupsMap.computeIfAbsent(group.getDomID(), k -> new CopyOnWriteArrayList<>())
                .add(group.getId());
    }

    @Override
    void deleteExecution(Integer idToDelete) throws Exception {
        GroupDTO group = idGroups.remove(idToDelete);
        if (group == null) return;
        dominionGroupsMap.get(group.getDomID()).remove(group.getId());
    }

    public Integer count() {
        return idGroups.size();
    }
}
