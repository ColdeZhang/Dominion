package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.cache.DominionNode;
import cn.lunadeer.dominion.cache.DominionNodeSectored;
import cn.lunadeer.dominion.misc.DominionException;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DominionCache extends Cache {
    private final Integer serverId;

    private ConcurrentHashMap<Integer, DominionDTO> idDominions;            // Dominion ID -> DominionDTO
    private ConcurrentHashMap<Integer, List<Integer>> dominionChildrenMap;  // Dominion ID -> Children Dominion ID
    private ConcurrentHashMap<String, Integer> dominionNameToId;            // Dominion name -> Dominion ID
    private ConcurrentHashMap<UUID, List<Integer>> playerOwnDominions;          // Player UUID -> Dominion ID
    private ConcurrentHashMap<UUID, List<DominionNode>> playerDominionNodes;  // Player UUID -> DominionNode
    private ConcurrentHashMap<Integer, DominionNode> dominionNodeMap;        // Dominion ID -> DominionNode

    // dominion nodes sectored by location, for fast location-based dominion lookup
    private final DominionNodeSectored dominionNodeSectored = new DominionNodeSectored();

    public DominionCache(Integer serverId) {
        this.serverId = serverId;
    }

    /**
     * Retrieves a DominionDTO by its ID.
     *
     * @param id the ID of the dominion to retrieve
     * @return the DominionDTO associated with the given ID
     * @throws DominionException if the dominion ID is not found
     */
    public @Nullable DominionDTO getDominion(Integer id) {
        return idDominions.get(id);
    }

    /**
     * Retrieves a DominionDTO by its name.
     *
     * @param name the name of the dominion to retrieve
     * @return the DominionDTO associated with the given name
     * @throws DominionException if the dominion name is not found
     */
    public @Nullable DominionDTO getDominion(String name) {
        return getDominion(dominionNameToId.get(name));
    }

    /**
     * Retrieves a DominionDTO by its location.
     *
     * @param location the location of the dominion to retrieve
     * @return the DominionDTO associated with the given location, or null if not found
     */
    public @Nullable DominionDTO getDominion(@NotNull Location location) {
        return dominionNodeSectored.getDominionByLocation(location);
    }

    /**
     * Retrieves the dominion nodes managed by a player.
     *
     * @param player the UUID of the player
     * @return a list of DominionNode objects managed by the player
     */
    public @NotNull List<DominionNode> getPlayerDominionNodes(UUID player) {
        return playerDominionNodes.getOrDefault(player, new ArrayList<>());
    }

    /**
     * Retrieves all dominion nodes.
     *
     * @return a list of all DominionNode objects
     */
    public @NotNull List<DominionNode> getAllDominionNodes() {
        return new ArrayList<>(dominionNodeMap.values());
    }

    /**
     * Retrieves the direct children of a dominion by its ID.
     *
     * @param id the ID of the parent dominion
     * @return a list of DominionDTO objects representing the children of the given dominion
     */
    public @NotNull List<DominionDTO> getChildrenOf(Integer id) {
        if (dominionChildrenMap.containsKey(id)) {
            return dominionChildrenMap.get(id).stream().map(this::getDominion).toList();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves the names of all dominions.
     *
     * @return a list of all dominion names
     */
    public List<String> getAllDominionNames() {
        return new ArrayList<>(dominionNameToId.keySet());
    }

    /**
     * Retrieves the DominionDTOs of the dominions owned by a player.
     *
     * @param player the UUID of the player
     * @return a list of DominionDTOs owned by the player
     */
    public List<DominionDTO> getPlayerOwnDominionDTOs(UUID player) {
        List<Integer> dominionIds = playerOwnDominions.getOrDefault(player, new ArrayList<>());
        List<DominionDTO> dominions = new ArrayList<>();
        for (Integer id : dominionIds) {
            dominions.add(getDominion(id));
        }
        return dominions;
    }

    public List<DominionDTO> getPlayerAdminDominionDTOs(UUID player) {
        List<DominionDTO> dominions = new ArrayList<>();
        List<MemberDTO> playerBelongedDominionMembers = Objects.requireNonNull(CacheManager.instance.getCache(serverId)).getMemberCache().getMemberBelongedDominions(player);
        for (MemberDTO member : playerBelongedDominionMembers) {
            if (member.getGroupId() != -1) {
                GroupDTO group = CacheManager.instance.getGroup(member.getGroupId());
                if (group == null) {
                    continue;
                }
                if (group.getFlagValue(Flags.ADMIN)) {
                    dominions.add(getDominion(member.getDomID()));
                }
            } else {
                if (member.getFlagValue(Flags.ADMIN)) {
                    dominions.add(getDominion(member.getDomID()));
                }
            }
        }
        return dominions;
    }

    public @NotNull List<DominionDTO> getAllDominions() {
        return new ArrayList<>(idDominions.values());
    }

    @Override
    void loadExecution() throws Exception {
        idDominions = new ConcurrentHashMap<>();
        dominionChildrenMap = new ConcurrentHashMap<>();
        dominionNameToId = new ConcurrentHashMap<>();
        playerDominionNodes = new ConcurrentHashMap<>();
        playerOwnDominions = new ConcurrentHashMap<>();
        dominionNodeMap = new ConcurrentHashMap<>();
        List<DominionDTO> dominions = new ArrayList<>(cn.lunadeer.dominion.dtos.DominionDTO.selectAll(serverId));

        for (DominionDTO dominion : dominions) {
            idDominions.put(dominion.getId(), dominion);
        }

        // build tree
        CompletableFuture<Void> buildTreeFuture = CompletableFuture.runAsync(() -> {
            List<DominionNode> nodeTree = DominionNode.BuildNodeTree(-1, dominions);
            for (DominionNode node : nodeTree) {
                dominionNodeMap.put(node.getDominion().getId(), node);
                playerDominionNodes.computeIfAbsent(node.getDominion().getOwner(), k -> new ArrayList<>()).add(node);
            }
            dominionNodeSectored.build(nodeTree);
        });

        // build other caches
        CompletableFuture<Void> buildCachesFuture = CompletableFuture.runAsync(() -> {
            for (DominionDTO dominion : dominions) {
                dominionNameToId.put(dominion.getName(), dominion.getId());
                playerOwnDominions.computeIfAbsent(dominion.getOwner(), k -> new ArrayList<>()).add(dominion.getId());
                if (dominion.getParentDomId() != -1) {
                    dominionChildrenMap.computeIfAbsent(dominion.getParentDomId(), k -> new ArrayList<>()).add(dominion.getId());
                }
            }
        });

        CompletableFuture.allOf(buildTreeFuture, buildCachesFuture).join();
    }

    @Override
    void loadExecution(Integer idToLoad) throws Exception {
        DominionDTO dominion = cn.lunadeer.dominion.dtos.DominionDTO.select(idToLoad);
        if (dominion == null) {
            return;
        }
        DominionDTO oldData = idDominions.putIfAbsent(dominion.getId(), dominion);
        // remove old data
        if (oldData != null) {
            dominionNameToId.entrySet().removeIf(entry -> entry.getValue().equals(oldData.getId()));
            playerOwnDominions.computeIfAbsent(oldData.getOwner(), k -> new ArrayList<>()).remove(oldData.getName());
        }
        // update data
        dominionNameToId.put(dominion.getName(), dominion.getId());
        playerOwnDominions.computeIfAbsent(dominion.getOwner(), k -> new ArrayList<>()).add(dominion.getId());
        // update node tree
        rebuildTreeAsync();
    }

    @Override
    void deleteExecution(Integer idToDelete) throws Exception {
        DominionDTO dominionToDelete = idDominions.remove(idToDelete);
        // remove children map
        dominionChildrenMap.remove(idToDelete);
        if (dominionChildrenMap.containsKey(dominionToDelete.getParentDomId())) {
            dominionChildrenMap.get(dominionToDelete.getParentDomId()).remove(idToDelete);
        }
        // remove name map
        dominionNameToId.entrySet().removeIf(entry -> entry.getValue().equals(idToDelete));
        playerOwnDominions.entrySet().removeIf(entry -> entry.getValue().contains(dominionToDelete.getId()));
        // update node tree
        rebuildTreeAsync();
    }

    private void rebuildTreeAsync() {
        CompletableFuture.runAsync(() -> {
            List<DominionNode> nodeTree = DominionNode.BuildNodeTree(-1, new ArrayList<>(idDominions.values()));
            for (DominionNode node : nodeTree) {
                dominionNodeMap.put(node.getDominion().getId(), node);
                playerDominionNodes.computeIfAbsent(node.getDominion().getOwner(), k -> new ArrayList<>()).add(node);
            }
            dominionNodeSectored.build(nodeTree);
        });
    }

    public Integer count() {
        return idDominions.size();
    }
}
