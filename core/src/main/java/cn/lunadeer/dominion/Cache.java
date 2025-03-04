package cn.lunadeer.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

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
            if (dominion.getServerId() != Configuration.multiServer.serverId) {
                continue;
            }
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
    private boolean recheckPlayerState = false;     // 是否需要重新检查玩家状态（发光、飞行）
    private final Map<UUID, String> player_name_cache = new HashMap<>();

    private Map<UUID, List<ResMigration.ResidenceNode>> residence_data = null;

    private final Map<UUID, Integer> map_player_using_group_title_id = new HashMap<>();


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
}
