package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.utils.XLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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
        for (Integer groupId : dominionGroupsMap.get(dominionId)) {
            groups.add(idGroups.get(groupId));
        }
        return groups;
    }

    @Override
    void loadExecution() throws Exception {
        idGroups = new ConcurrentHashMap<>();
        dominionGroupsMap = new ConcurrentHashMap<>();

        List<DominionDTO> thisServerDominions = Objects.requireNonNull(CacheManager.instance.getCache(serverId)).getDominionCache().getAllDominions();
        for (DominionDTO dominion : thisServerDominions) {
            List<GroupDTO> groups = new ArrayList<>(cn.lunadeer.dominion.dtos.GroupDTO.selectByDominionId(dominion.getId()));
            CompletableFuture.runAsync(() -> {
                for (GroupDTO group : groups) {
                    idGroups.put(group.getId(), group);
                    dominionGroupsMap.computeIfAbsent(dominion.getId(), k -> new CopyOnWriteArrayList<>())
                            .add(group.getId());
                }
            }).exceptionally(e -> {
                XLogger.error(e);
                return null;
            });
        }
    }

    @Override
    void loadExecution(Integer idToLoad) throws Exception {
        GroupDTO group = cn.lunadeer.dominion.dtos.GroupDTO.select(idToLoad);
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
