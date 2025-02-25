package cn.lunadeer.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.events.PlayerCrossDominionBorderEvent;
import cn.lunadeer.dominion.events.PlayerMoveInDominionEvent;
import cn.lunadeer.dominion.events.PlayerMoveOutDominionEvent;
import cn.lunadeer.dominion.managers.PlaceHolderApi;
import cn.lunadeer.dominion.utils.*;
import cn.lunadeer.dominion.utils.webMap.MapRender;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static cn.lunadeer.dominion.DominionNode.getLocInDominionNode;
import static cn.lunadeer.dominion.DominionNode.isInDominion;

public class Cache implements Listener {

    public Cache() {
        instance = this;
        player_current_dominion_id = new HashMap<>();
        loadDominions();
        loadMembers();
        loadGroups();
        Bukkit.getPluginManager().registerEvents(this, Dominion.instance);
    }

    /**
     * 从数据库加载所有领地
     * 如果idToLoad为null，则加载所有领地
     *
     * @param idToLoad 领地ID
     */
    public void loadDominions(Integer idToLoad) {
        if (_last_update_dominion.get() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            XLogger.debug("run loadDominionsExecution directly");
            loadDominionsExecution(idToLoad);
        } else {
            if (_update_dominion_is_scheduled.get()) return;
            XLogger.debug("schedule loadDominionsExecution");
            _update_dominion_is_scheduled.set(true);
            long delay_tick = (UPDATE_INTERVAL - (System.currentTimeMillis() - _last_update_dominion.get())) / 1000 * 20L;
            Scheduler.runTaskLaterAsync(() -> {
                        XLogger.debug("run loadDominionsExecution scheduled");
                        loadDominionsExecution(idToLoad);
                        _update_dominion_is_scheduled.set(false);
                    },
                    delay_tick);
        }
    }

    public void loadDominions() {
        loadDominions(null);
    }

    private void loadDominionsExecution(Integer idToLoad) {
        Scheduler.runTaskAsync(() -> {
            long start = System.currentTimeMillis();
            int count = 0;
            if (idToLoad == null) {
                id_dominions = new ConcurrentHashMap<>();
                dominion_name_to_id = new ConcurrentHashMap<>();
                dominion_children = new ConcurrentHashMap<>();

                List<DominionDTO> dominions;
                try {
                    dominions = new ArrayList<>(cn.lunadeer.dominion.dtos.DominionDTO.selectAll());
                } catch (SQLException e) {
                    XLogger.error("loadDominionsExecution error: {0}", e.getMessage());
                    return;
                }
                CompletableFuture<Void> res = dominion_trees.initAsync(dominions);
                count = dominions.size();

                for (DominionDTO d : dominions) {
                    id_dominions.put(d.getId(), d);
                    dominion_name_to_id.put(d.getName(), d.getId());
                    if (!dominion_children.containsKey(d.getParentDomId())) {
                        dominion_children.put(d.getParentDomId(), new ArrayList<>());
                    }
                    dominion_children.get(d.getParentDomId()).add(d.getId());
                }

                res.join(); // 等待树的构建完成
            } else {
                DominionDTO dominion;
                try {
                    dominion = cn.lunadeer.dominion.dtos.DominionDTO.select(idToLoad);
                } catch (SQLException e) {
                    XLogger.error("loadDominionsExecution error: {0}", e.getMessage());
                    return;
                }
                if (dominion == null && id_dominions.containsKey(idToLoad)) {
                    id_dominions.remove(idToLoad);
                } else if (dominion != null) {
                    id_dominions.put(idToLoad, dominion);
                    count = 1;
                }
                // rebuild dominion_name_to_id and dominion_children
                dominion_name_to_id = new ConcurrentHashMap<>();
                dominion_children = new ConcurrentHashMap<>();
                for (DominionDTO d : id_dominions.values()) {
                    dominion_name_to_id.put(d.getName(), d.getId());
                    if (!dominion_children.containsKey(d.getParentDomId())) {
                        dominion_children.put(d.getParentDomId(), new ArrayList<>());
                    }
                    dominion_children.get(d.getParentDomId()).add(d.getId());
                }
            }
            MapRender.render();
            recheckPlayerState = true;
            _last_update_dominion.set(System.currentTimeMillis());
            XLogger.debug("loadDominionsExecution cost: {0} ms for {1} dominions"
                    , System.currentTimeMillis() - start, count);
        });
    }

    /**
     * 从数据库加载所有玩家特权
     * 如果player_uuid为null，则加载所有玩家的特权
     *
     * @param player_uuid 玩家UUID
     */
    public void loadMembers(UUID player_uuid) {
        if (_last_update_member.get() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            XLogger.debug("run loadMembersExecution directly");
            loadMembersExecution(player_uuid);
        } else {
            if (_update_member_is_scheduled.get()) return;
            XLogger.debug("schedule loadMembersExecution");
            _update_member_is_scheduled.set(true);
            long delay_tick = (UPDATE_INTERVAL - (System.currentTimeMillis() - _last_update_member.get())) / 1000 * 20L;
            Scheduler.runTaskLaterAsync(() -> {
                        XLogger.debug("run loadMembersExecution scheduled");
                        loadMembersExecution(player_uuid);
                        _update_member_is_scheduled.set(false);
                    },
                    delay_tick);
        }
    }

    public void loadMembers() {
        loadMembers(null);
    }

    private void loadMembersExecution(UUID player_to_update) {
        Scheduler.runTaskAsync(() -> {
            long start = System.currentTimeMillis();
            List<MemberDTO> all_privileges;
            try {
                if (player_to_update == null) {
                    all_privileges = new ArrayList<>(cn.lunadeer.dominion.dtos.MemberDTO.selectAll());
                    player_uuid_to_member = new ConcurrentHashMap<>();
                } else {
                    all_privileges = new ArrayList<>(cn.lunadeer.dominion.dtos.MemberDTO.selectAll(player_to_update));
                    if (!player_uuid_to_member.containsKey(player_to_update)) {
                        player_uuid_to_member.put(player_to_update, new ConcurrentHashMap<>());
                    } else {
                        player_uuid_to_member.get(player_to_update).clear();
                    }
                }
            } catch (SQLException e) {
                XLogger.error(e.getMessage());
                return;
            }
            for (MemberDTO privilege : all_privileges) {
                UUID player_uuid = privilege.getPlayerUUID();
                if (!player_uuid_to_member.containsKey(player_uuid)) {
                    player_uuid_to_member.put(player_uuid, new ConcurrentHashMap<>());
                }
                player_uuid_to_member.get(player_uuid).put(privilege.getDomID(), privilege);
            }
            recheckPlayerState = true;
            _last_update_member.set(System.currentTimeMillis());
            XLogger.debug("loadMembersExecution cost: {0} ms for {1} privileges"
                    , System.currentTimeMillis() - start, all_privileges.size());
        });
    }

    public void loadGroups() {
        loadGroups(null);
    }

    public void loadGroups(Integer groupId) {
        if (_last_update_group.get() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            XLogger.debug("run loadGroupsExecution directly");
            loadGroupExecution(groupId);
        } else {
            if (_update_group_is_scheduled.get()) return;
            XLogger.debug("schedule loadGroupsExecution");
            _update_group_is_scheduled.set(true);
            long delay_tick = (UPDATE_INTERVAL - (System.currentTimeMillis() - _last_update_group.get())) / 1000 * 20L;
            Scheduler.runTaskLaterAsync(() -> {
                        XLogger.debug("run loadGroupsExecution scheduled");
                        loadGroupExecution(groupId);
                        _update_group_is_scheduled.set(false);
                    },
                    delay_tick);
        }
    }

    private void loadGroupExecution(Integer groupId) {
        Scheduler.runTaskAsync(() -> {
            long start = System.currentTimeMillis();
            if (groupId == null) {
                id_groups = new ConcurrentHashMap<>();
                try {
                    List<GroupDTO> groups = new ArrayList<>(cn.lunadeer.dominion.dtos.GroupDTO.selectAll());
                    List<PlayerDTO> players = new ArrayList<>(cn.lunadeer.dominion.dtos.PlayerDTO.all());
                    for (GroupDTO group : groups) {
                        id_groups.put(group.getId(), group);
                    }
                    for (PlayerDTO player : players) {
                        map_player_using_group_title_id.put(player.getUuid(), player.getUsingGroupTitleID());
                    }
                } catch (SQLException e) {
                    XLogger.error(e.getMessage());
                    return;
                }
            } else {
                try {
                    GroupDTO group = cn.lunadeer.dominion.dtos.GroupDTO.select(groupId);
                    if (group == null && id_groups.containsKey(groupId)) {
                        id_groups.remove(groupId);
                    } else if (group != null) {
                        id_groups.put(groupId, group);
                    }
                } catch (SQLException e) {
                    XLogger.error(e.getMessage());
                    return;
                }
            }
            recheckPlayerState = true;
            _last_update_group.set(System.currentTimeMillis());
            XLogger.debug("loadGroupsExecution cost: {0} ms", System.currentTimeMillis() - start);
        });
    }

    public DominionDTO getPlayerCurrentDominion(@NotNull Player player) {
        try (AutoTimer ignored = new AutoTimer(Configuration.timer)) {
            Integer last_in_dom_id = player_current_dominion_id.get(player.getUniqueId());
            DominionDTO last_dominion = null;
            if (last_in_dom_id != null) {
                last_dominion = id_dominions.get(last_in_dom_id);
            }

            // 如果玩家仍在上次一记录的领地内，且领地没有子领地，则直接返回
            if (isInDominion(last_dominion, player.getLocation())
                    && (dominion_children.get(last_in_dom_id) == null || dominion_children.get(last_in_dom_id).isEmpty())) {
                // 如果缓存更新则需要重新检查玩家状态
                if (recheckPlayerState) {
                    checkPlayerStates(player, last_dominion);
                    recheckPlayerState = false;
                }
                return last_dominion;
            }

            // 否则获取玩家当前所在领地，然后对比上次记录的领地
            DominionDTO current_dominion = dominion_trees.getLocInDominionDTO(player.getLocation());
            int last_dom_id = last_dominion == null ? -1 : last_dominion.getId();
            int current_dom_id = current_dominion == null ? -1 : current_dominion.getId();

            // 如果玩家上次所在领地和当前所在领地相同，则直接返回
            if (last_dom_id == current_dom_id) {
                if (recheckPlayerState) {
                    checkPlayerStates(player, last_dominion);
                    recheckPlayerState = false;
                }
                return last_dominion;
            }

            // 如果玩家上次所在领地和当前所在领地不同，则触发玩家跨领地边界事件
            new PlayerCrossDominionBorderEvent(player, last_dominion, current_dominion).call();
            checkPlayerStates(player, last_dominion);   // 检查玩家状态

            // 如果上次记录的领地不为空，则触发玩家离开领地事件
            if (last_dom_id != -1) {
                new PlayerMoveOutDominionEvent(player, last_dominion).call();
            }
            // 如果当前领地不为空，则触发玩家进入领地事件
            if (current_dom_id != -1) {
                new PlayerMoveInDominionEvent(player, current_dominion).call();
            }
            // 更新玩家当前所在领地缓存
            if (current_dominion == null) {
                player_current_dominion_id.put(player.getUniqueId(), null);
                return null;
            } else {
                player_current_dominion_id.put(player.getUniqueId(), current_dominion.getId());
                return current_dominion;
            }
        }
    }

    public DominionDTO getDominionByLoc(@NotNull Location loc) {
        return dominion_trees.getLocInDominionDTO(loc);
    }

    /**
     * 玩家退出时调用 用于清除玩家当前所在领地
     * 会将玩家当前所在领地设置为null
     * 这样当玩家下次进入领地时，会重新检查玩家所在位置
     *
     * @param player 玩家
     */
    public void onPlayerQuit(Player player) {
        player_current_dominion_id.put(player.getUniqueId(), null);
    }

    /**
     * 检查玩家是否需要设置为发光
     *
     * @param player   玩家
     * @param dominion 领地
     */
    private void lightOrNot(Player player, DominionDTO dominion) {
        if (!Flags.GLOW.getEnable()) {
            return;
        }
        if (dominion == null) {
            player.setGlowing(false);
            return;
        }
        MemberDTO privilege = getMember(player, dominion);
        if (privilege != null) {
            if (privilege.getGroupId() == -1) {
                player.setGlowing(privilege.getFlagValue(Flags.GLOW));
            } else {
                GroupDTO group = getGroup(privilege.getGroupId());
                if (group != null) {
                    player.setGlowing(group.getFlagValue(Flags.GLOW));
                } else {
                    player.setGlowing(dominion.getGuestPrivilegeFlagValue().get(Flags.GLOW));
                }
            }
        } else {
            player.setGlowing(dominion.getGuestPrivilegeFlagValue().get(Flags.GLOW));
        }
    }

    private void flyOrNot(Player player, DominionDTO dominion) {
        for (String flyPN : Configuration.flyPermissionNodes) {
            if (player.hasPermission(flyPN)) {
                return;
            }
        }
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (player.isOp() && Configuration.adminBypass) {
            return;
        }
        if (!Flags.FLY.getEnable()) {
            player.setAllowFlight(false);
            return;
        }
        if (dominion == null) {
            player.setAllowFlight(false);
            return;
        }
        MemberDTO privilege = getMember(player, dominion);
        if (privilege != null) {
            if (privilege.getGroupId() == -1) {
                player.setAllowFlight(privilege.getFlagValue(Flags.FLY));
            } else {
                GroupDTO group = getGroup(privilege.getGroupId());
                if (group != null) {
                    player.setAllowFlight(group.getFlagValue(Flags.FLY));
                } else {
                    player.setAllowFlight(dominion.getGuestPrivilegeFlagValue().get(Flags.FLY));
                }
            }
        } else {
            player.setAllowFlight(dominion.getGuestPrivilegeFlagValue().get(Flags.FLY));
        }
    }

    public GroupDTO getGroup(@NotNull Integer id) {
        return id_groups.get(id);
    }

    public List<GroupDTO> getGroups(@NotNull Integer dominionId) {
        List<GroupDTO> groups = new ArrayList<>();
        for (GroupDTO group : id_groups.values()) {
            if (group.getDomID().equals(dominionId)) {
                groups.add(group);
            }
        }
        return groups;
    }

    public MemberDTO getMember(@NotNull Player player, cn.lunadeer.dominion.api.dtos.@NotNull DominionDTO dominion) {
        if (!player_uuid_to_member.containsKey(player.getUniqueId())) return null;
        return player_uuid_to_member.get(player.getUniqueId()).get(dominion.getId());
    }

    public MemberDTO getMember(@NotNull UUID player_uuid, cn.lunadeer.dominion.api.dtos.@NotNull DominionDTO dominion) {
        if (!player_uuid_to_member.containsKey(player_uuid)) return null;
        return player_uuid_to_member.get(player_uuid).get(dominion.getId());
    }

    public List<MemberDTO> getMembers(@NotNull Integer dominionId) {
        List<MemberDTO> members = new ArrayList<>();
        for (Map<Integer, MemberDTO> member : player_uuid_to_member.values()) {
            if (member.containsKey(dominionId)) {
                members.add(member.get(dominionId));
            }
        }
        return members;
    }

    /**
     * Retrieves the list of group titles for a player.
     * This method collects all group titles associated with the player, including those from the player's memberships
     * and the dominions they own.
     *
     * @param playerUuid The UUID of the player.
     * @return A list of GroupDTO objects representing the player's group titles.
     */
    public List<GroupDTO> getPlayerGroupTitleList(UUID playerUuid) {
        List<GroupDTO> groups = new ArrayList<>();
        if (!player_uuid_to_member.containsKey(playerUuid)) return groups;
        for (MemberDTO member : player_uuid_to_member.get(playerUuid).values()) {
            if (member.getGroupId() != -1) {
                GroupDTO group = getGroup(member.getGroupId());
                if (group != null) {
                    groups.add(group);
                }
            }
        }
        for (DominionDTO dominion : getPlayerDominions(playerUuid)) {
            for (GroupDTO group : getGroups(dominion.getId())) {
                if (!groups.contains(group)) {
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    public @Nullable DominionDTO getDominion(@NotNull Integer id) {
        return id_dominions.get(id);
    }

    public @Nullable DominionDTO getDominion(@NotNull String name) {
        if (dominion_name_to_id.containsKey(name)) {
            return id_dominions.get(dominion_name_to_id.get(name));
        }
        return null;
    }

    public List<DominionDTO> getDominionsByParentId(@NotNull Integer parent_id) {
        List<DominionDTO> dominions = new ArrayList<>();
        if (dominion_children.containsKey(parent_id)) {
            for (Integer id : dominion_children.get(parent_id)) {
                dominions.add(id_dominions.get(id));
            }
        }
        return dominions;
    }

    public String getPlayerName(UUID uuid) {
        if (!player_name_cache.containsKey(uuid)) {
            PlayerDTO playerDTO = cn.lunadeer.dominion.dtos.PlayerDTO.select(uuid);
            if (playerDTO != null) {
                player_name_cache.put(uuid, playerDTO.getLastKnownName());
            }
        }
        return player_name_cache.getOrDefault(uuid, "Unknown");
    }

    public int getPlayerDominionCount(UUID player_uuid) {
        int count = 0;
        for (DominionDTO dominion : id_dominions.values()) {
            if (dominion.getOwner().equals(player_uuid)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retrieves a list of dominions owned by a specific player.
     * <p>
     * Includes dominions on all servers.
     *
     * @param player_uuid The UUID of the player whose dominions are to be retrieved.
     * @return A list of DominionDTO objects representing the dominions owned by the player.
     */
    public List<DominionDTO> getPlayerDominions(UUID player_uuid) {
        List<DominionDTO> dominions = new ArrayList<>();
        for (DominionDTO dominion : id_dominions.values()) {
            if (dominion.getOwner().equals(player_uuid)) {
                dominions.add(dominion);
            }
        }
        return dominions;
    }

    /**
     * Retrieves a list of dominions where a specific player has admin privileges.
     * <p>
     * Only dominions on the current server are included in the list.
     *
     * @param player_uuid The UUID of the player whose admin dominions are to be retrieved.
     * @return A list of DominionDTO objects representing the dominions where the player has admin privileges.
     */
    public List<DominionDTO> getPlayerAdminDominions(UUID player_uuid) {
        List<DominionDTO> dominions = new ArrayList<>();
        for (DominionDTO dominion : id_dominions.values()) {
            if (dominion.getServerId() != Configuration.multiServer.serverId) {
                continue;
            }
            MemberDTO privilege = getMember(player_uuid, dominion);
            if (privilege != null && privilege.getFlagValue(Flags.ADMIN)) {
                dominions.add(dominion);
            }
        }
        return dominions;
    }

    private void updateResidenceData() {
        if (residence_data == null) {
            residence_data = new HashMap<>();
            List<ResMigration.ResidenceNode> residences = ResMigration.extractFromResidence(Dominion.instance);
            for (ResMigration.ResidenceNode node : residences) {
                if (node == null) {
                    continue;
                }
                if (!residence_data.containsKey(node.owner)) {
                    XLogger.debug("residence_data put {0}", node.owner);
                    residence_data.put(node.owner, new ArrayList<>());
                }
                residence_data.get(node.owner).add(node);
            }
            XLogger.debug("residence_data: {0}", residence_data.size());
        }
    }

    public List<ResMigration.ResidenceNode> getResidenceData() {
        updateResidenceData();
        return residence_data.values().stream().reduce(new ArrayList<>(), (a, b) -> {
            a.addAll(b);
            return a;
        });
    }

    public List<ResMigration.ResidenceNode> getResidenceData(UUID player_uuid) {
        updateResidenceData();
        return residence_data.get(player_uuid);
    }

    public @NotNull List<DominionDTO> getAllDominions() {
        return new ArrayList<>(id_dominions.values());
    }

    public int getDominionCounts() {
        return id_dominions.size();
    }

    public int getMemberCounts() {
        int count = 0;
        for (Map<Integer, MemberDTO> member : player_uuid_to_member.values()) {
            count += member.size();
        }
        return count;
    }

    public int getGroupCounts() {
        return id_groups.size();
    }

    public static Cache instance;
    private ConcurrentHashMap<Integer, DominionDTO> id_dominions;
    private ConcurrentHashMap<String, Integer> dominion_name_to_id;
    private ConcurrentHashMap<Integer, GroupDTO> id_groups;
    private final WorldDominionTreeSectored dominion_trees = new WorldDominionTreeSectored();
    private ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, MemberDTO>> player_uuid_to_member;   // 玩家所有的特权
    private final Map<UUID, Integer> player_current_dominion_id;                         // 玩家当前所在领地
    private ConcurrentHashMap<Integer, List<Integer>> dominion_children;
    private final AtomicLong _last_update_dominion = new AtomicLong(0);
    private final AtomicBoolean _update_dominion_is_scheduled = new AtomicBoolean(false);
    private final AtomicLong _last_update_member = new AtomicLong(0);
    private final AtomicBoolean _update_member_is_scheduled = new AtomicBoolean(false);
    private final AtomicLong _last_update_group = new AtomicLong(0);
    private final AtomicBoolean _update_group_is_scheduled = new AtomicBoolean(false);
    private static final long UPDATE_INTERVAL = 1000 * 4;
    private boolean recheckPlayerState = false;     // 是否需要重新检查玩家状态（发光、飞行）
    private final Map<UUID, String> player_name_cache = new HashMap<>();

    private Map<UUID, List<ResMigration.ResidenceNode>> residence_data = null;

    private final Map<UUID, Integer> map_player_using_group_title_id = new HashMap<>();

    private static class WorldDominionTreeSectored {
    /*
        D | C
        --+--
        B | A
     */

        private ConcurrentHashMap<UUID, List<DominionNode>> world_dominion_tree_sector_a; // x >= 0, z >= 0
        private ConcurrentHashMap<UUID, List<DominionNode>> world_dominion_tree_sector_b; // x <= 0, z >= 0
        private ConcurrentHashMap<UUID, List<DominionNode>> world_dominion_tree_sector_c; // x >= 0, z <= 0
        private ConcurrentHashMap<UUID, List<DominionNode>> world_dominion_tree_sector_d; // x <= 0, z <= 0
        private Integer section_origin_x = 0;
        private Integer section_origin_z = 0;

        public DominionDTO getLocInDominionDTO(@NotNull Location loc) {
            try (AutoTimer ignored = new AutoTimer(Configuration.timer)) {
                List<DominionNode> nodes = getNodes(loc);
                if (nodes == null) return null;
                if (nodes.isEmpty()) return null;
                DominionNode dominionNode = getLocInDominionNode(nodes, loc);
                return dominionNode == null ? null : dominionNode.getDominion();
            }
        }


        public List<DominionNode> getNodes(@NotNull Location loc) {
            return getNodes(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ());
        }

        public List<DominionNode> getNodes(World world, int x, int z) {
            return getNodes(world.getUID(), x, z);
        }

        public List<DominionNode> getNodes(UUID world, int x, int z) {
            if (x >= section_origin_x && z >= section_origin_z) {
                if (world_dominion_tree_sector_a == null) return null;
                return world_dominion_tree_sector_a.get(world);
            }
            if (x <= section_origin_x && z >= section_origin_z) {
                if (world_dominion_tree_sector_b == null) return null;
                return world_dominion_tree_sector_b.get(world);
            }
            if (x >= section_origin_x) {
                if (world_dominion_tree_sector_c == null) return null;
                return world_dominion_tree_sector_c.get(world);
            }
            if (world_dominion_tree_sector_d == null) return null;
            return world_dominion_tree_sector_d.get(world);
        }

        public CompletableFuture<Void> initAsync(List<DominionDTO> dominions) {
            return CompletableFuture.runAsync(() -> init(dominions));
        }

        private void init(List<DominionDTO> dominions) {
            try (AutoTimer ignored = new AutoTimer(Configuration.timer)) {
                world_dominion_tree_sector_a = new ConcurrentHashMap<>();
                world_dominion_tree_sector_b = new ConcurrentHashMap<>();
                world_dominion_tree_sector_c = new ConcurrentHashMap<>();
                world_dominion_tree_sector_d = new ConcurrentHashMap<>();

                Map<UUID, List<DominionDTO>> world_dominions_sector_a = new HashMap<>();
                Map<UUID, List<DominionDTO>> world_dominions_sector_b = new HashMap<>();
                Map<UUID, List<DominionDTO>> world_dominions_sector_c = new HashMap<>();
                Map<UUID, List<DominionDTO>> world_dominions_sector_d = new HashMap<>();

                // calculate the section origin point
                int max_x = dominions.stream().mapToInt(d -> d.getServerId() == Configuration.multiServer.serverId ? d.getCuboid().x2() : 0).max().orElse(0);
                int min_x = dominions.stream().mapToInt(d -> d.getServerId() == Configuration.multiServer.serverId ? d.getCuboid().x1() : 0).min().orElse(0);
                int max_z = dominions.stream().mapToInt(d -> d.getServerId() == Configuration.multiServer.serverId ? d.getCuboid().z2() : 0).max().orElse(0);
                int min_z = dominions.stream().mapToInt(d -> d.getServerId() == Configuration.multiServer.serverId ? d.getCuboid().z1() : 0).min().orElse(0);
                section_origin_x = (max_x + min_x) / 2;
                section_origin_z = (max_z + min_z) / 2;
                XLogger.debug("Cache init section origin: {0}, {1}", section_origin_x, section_origin_z);

                for (DominionDTO d : dominions) {
                    if (d.getServerId() != Configuration.multiServer.serverId) {
                        // skip other server's dominions when building dominion tree
                        continue;
                    }
                    // put dominions into different sectors
                    if (!world_dominions_sector_a.containsKey(d.getWorldUid()) ||
                            !world_dominions_sector_b.containsKey(d.getWorldUid()) ||
                            !world_dominions_sector_c.containsKey(d.getWorldUid()) ||
                            !world_dominions_sector_d.containsKey(d.getWorldUid())) {
                        world_dominions_sector_a.put(d.getWorldUid(), new ArrayList<>());
                        world_dominions_sector_b.put(d.getWorldUid(), new ArrayList<>());
                        world_dominions_sector_c.put(d.getWorldUid(), new ArrayList<>());
                        world_dominions_sector_d.put(d.getWorldUid(), new ArrayList<>());
                    }
                    if (d.getCuboid().x1() >= section_origin_x && d.getCuboid().z1() >= section_origin_z) {
                        world_dominions_sector_a.get(d.getWorldUid()).add(d);
                    } else if (d.getCuboid().x1() <= section_origin_x && d.getCuboid().z1() >= section_origin_z) {
                        if (d.getCuboid().x2() >= section_origin_x) {
                            world_dominions_sector_a.get(d.getWorldUid()).add(d);
                            world_dominions_sector_b.get(d.getWorldUid()).add(d);
                        } else {
                            world_dominions_sector_b.get(d.getWorldUid()).add(d);
                        }
                    } else if (d.getCuboid().x1() >= section_origin_x && d.getCuboid().z1() <= section_origin_z) {
                        if (d.getCuboid().z2() >= section_origin_z) {
                            world_dominions_sector_a.get(d.getWorldUid()).add(d);
                            world_dominions_sector_c.get(d.getWorldUid()).add(d);
                        } else {
                            world_dominions_sector_c.get(d.getWorldUid()).add(d);
                        }
                    } else {
                        if (d.getCuboid().x2() >= section_origin_x && d.getCuboid().z2() >= section_origin_z) {
                            world_dominions_sector_a.get(d.getWorldUid()).add(d);
                            world_dominions_sector_b.get(d.getWorldUid()).add(d);
                            world_dominions_sector_c.get(d.getWorldUid()).add(d);
                            world_dominions_sector_d.get(d.getWorldUid()).add(d);
                        } else if (d.getCuboid().x2() >= section_origin_x && d.getCuboid().z2() <= section_origin_z) {
                            world_dominions_sector_c.get(d.getWorldUid()).add(d);
                            world_dominions_sector_d.get(d.getWorldUid()).add(d);
                        } else if (d.getCuboid().z2() >= section_origin_z && d.getCuboid().x2() <= section_origin_x) {
                            world_dominions_sector_b.get(d.getWorldUid()).add(d);
                            world_dominions_sector_d.get(d.getWorldUid()).add(d);
                        } else {
                            world_dominions_sector_d.get(d.getWorldUid()).add(d);
                        }
                    }
                }
                // build dominion tree for each sector
                world_dominions_sector_a.forEach((key, value) ->
                        world_dominion_tree_sector_a.put(key, DominionNode.BuildNodeTree(-1, value))
                );
                world_dominions_sector_b.forEach((key, value) ->
                        world_dominion_tree_sector_b.put(key, DominionNode.BuildNodeTree(-1, value))
                );
                world_dominions_sector_c.forEach((key, value) ->
                        world_dominion_tree_sector_c.put(key, DominionNode.BuildNodeTree(-1, value))
                );
                world_dominions_sector_d.forEach((key, value) ->
                        world_dominion_tree_sector_d.put(key, DominionNode.BuildNodeTree(-1, value))
                );
            }
        }
    }

    public @Nullable GroupDTO getPlayerUsingGroupTitle(@NotNull UUID uuid) {
        if (!Configuration.groupTitle.enable) {
            return null;
        }
        if (map_player_using_group_title_id.containsKey(uuid)) {
            return getGroup(map_player_using_group_title_id.get(uuid));
        }
        return null;
    }

    public void updatePlayerUsingGroupTitle(UUID uuid, Integer groupId) {
        map_player_using_group_title_id.put(uuid, groupId);
    }

    private void checkPlayerStates(Player player, DominionDTO dominion) {
        flyOrNot(player, dominion);
        lightOrNot(player, dominion);
    }

    @EventHandler
    public void onPlayerMoveInDominion(PlayerMoveInDominionEvent event) {
        XLogger.debug("PlayerMoveInDominionEvent called.");
        MessageDisplay.show(event.getPlayer(), MessageDisplay.Place.valueOf(Configuration.pluginMessage.enterLeaveDisplayPlace.toUpperCase()),
                PlaceHolderApi.setPlaceholders(
                        event.getPlayer(),
                        event.getDominion().getJoinMessage()
                                .replace("{DOM}", event.getDominion().getName())
                                .replace("{OWNER}", getPlayerName(event.getDominion().getOwner()))
                )
        );
        // show border
        if (event.getDominion().getEnvironmentFlagValue().get(Flags.SHOW_BORDER)) {
            ParticleUtil.showBorder(event.getPlayer(), event.getDominion());
        }
    }

    @EventHandler
    public void onPlayerMoveOutDominion(PlayerMoveOutDominionEvent event) {
        XLogger.debug("PlayerMoveOutDominionEvent called.");
        MessageDisplay.show(event.getPlayer(), MessageDisplay.Place.valueOf(Configuration.pluginMessage.enterLeaveDisplayPlace.toUpperCase()),
                PlaceHolderApi.setPlaceholders(
                        event.getPlayer(),
                        event.getDominion().getLeaveMessage()
                                .replace("{DOM}", event.getDominion().getName())
                                .replace("{OWNER}", getPlayerName(event.getDominion().getOwner()))
                )
        );
        // show border
        if (event.getDominion().getEnvironmentFlagValue().get(Flags.SHOW_BORDER)) {
            ParticleUtil.showBorder(event.getPlayer(), event.getDominion());
        }
    }

    @EventHandler
    public void onPlayerCrossDominionBorderEvent(PlayerCrossDominionBorderEvent event) {
        XLogger.debug("PlayerCrossDominionBorderEvent called.");
        checkPlayerStates(event.getPlayer(), event.getTo());
    }
}
