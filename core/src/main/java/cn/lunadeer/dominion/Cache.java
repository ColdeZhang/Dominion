package cn.lunadeer.dominion;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.dtos.*;
import cn.lunadeer.dominion.utils.MessageDisplay;
import cn.lunadeer.dominion.utils.Particle;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.dominion.utils.map.MapRender;
import cn.lunadeer.minecraftpluginutils.AutoTimer;
import cn.lunadeer.minecraftpluginutils.Scheduler;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static cn.lunadeer.dominion.DominionNode.getLocInDominionNode;
import static cn.lunadeer.dominion.DominionNode.isInDominion;

public class Cache implements DominionAPI {

    public Cache() {
        player_current_dominion_id = new HashMap<>();
        loadDominions();
        loadMembers();
        loadGroups();
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
                dominion_children = new ConcurrentHashMap<>();

                List<DominionDTO> dominions = DominionDTO.selectAll();
                CompletableFuture<Void> res = dominion_trees.initAsync(dominions);
                count = dominions.size();

                for (DominionDTO d : dominions) {
                    id_dominions.put(d.getId(), d);
                    if (!dominion_children.containsKey(d.getParentDomId())) {
                        dominion_children.put(d.getParentDomId(), new ArrayList<>());
                    }
                    dominion_children.get(d.getParentDomId()).add(d.getId());
                }

                res.join(); // 等待树的构建完成
            } else {
                DominionDTO dominion = DominionDTO.select(idToLoad);
                if (dominion == null && id_dominions.containsKey(idToLoad)) {
                    id_dominions.remove(idToLoad);
                } else if (dominion != null) {
                    id_dominions.put(idToLoad, dominion);
                    count = 1;
                }
            }
            MapRender.render();
            recheckPlayerState = true;
            _last_update_dominion.set(System.currentTimeMillis());
            XLogger.debug("loadDominionsExecution cost: %d ms for %d dominions"
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
            if (player_to_update == null) {
                all_privileges = MemberDTO.selectAll();
                player_uuid_to_member = new ConcurrentHashMap<>();
            } else {
                all_privileges = MemberDTO.selectAll(player_to_update);
                if (!player_uuid_to_member.containsKey(player_to_update)) {
                    player_uuid_to_member.put(player_to_update, new ConcurrentHashMap<>());
                }
                player_uuid_to_member.get(player_to_update).clear();
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
            XLogger.debug("loadMembersExecution cost: %d ms for %d privileges"
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
                List<GroupDTO> groups = GroupDTO.selectAll();
                List<PlayerDTO> players = PlayerDTO.all();
                for (GroupDTO group : groups) {
                    id_groups.put(group.getId(), group);
                }
                for (PlayerDTO player : players) {
                    map_player_using_group_title_id.put(player.getUuid(), player.getUsingGroupTitleID());
                }
            } else {
                GroupDTO group = GroupDTO.select(groupId);
                if (group == null && id_groups.containsKey(groupId)) {
                    id_groups.remove(groupId);
                } else if (group != null) {
                    id_groups.put(groupId, group);
                }
            }
            recheckPlayerState = true;
            _last_update_group.set(System.currentTimeMillis());
            XLogger.debug("loadGroupsExecution cost: %d ms", System.currentTimeMillis() - start);
        });
    }

    @Override
    public DominionDTO getPlayerCurrentDominion(@NotNull Player player) {
        try (AutoTimer ignored = new AutoTimer(Dominion.config.TimerEnabled())) {
            Integer last_in_dom_id = player_current_dominion_id.get(player.getUniqueId());
            DominionDTO last_dominion = null;
            if (last_in_dom_id != null) {
                last_dominion = id_dominions.get(last_in_dom_id);
            }
            if (isInDominion(last_dominion, player.getLocation())) {
                if (dominion_children.get(last_in_dom_id) == null || dominion_children.get(last_in_dom_id).isEmpty()) {
                    // 如果玩家仍在领地内，且领地没有子领地，则直接返回
                    if (recheckPlayerState) {
                        lightOrNot(player, last_dominion);
                        flyOrNot(player, last_dominion);
                        recheckPlayerState = false;
                    }
                    return last_dominion;
                }
            }
            DominionDTO current_dominion = dominion_trees.getLocInDominionDTO(player.getLocation());
            int last_dom_id = last_dominion == null ? -1 : last_dominion.getId();
            int current_dom_id = current_dominion == null ? -1 : current_dominion.getId();
            if (last_dom_id == current_dom_id) {
                return last_dominion;
            }
            if (last_dom_id != -1) {
                MessageDisplay.show(player, Dominion.config.getMessageDisplayJoinLeave(), last_dominion.getLeaveMessage());
            }
            if (current_dom_id != -1) {
                MessageDisplay.show(player, Dominion.config.getMessageDisplayJoinLeave(), current_dominion.getJoinMessage());
            }

            lightOrNot(player, current_dominion);   // 发光检查
            flyOrNot(player, current_dominion);     // 飞行检查
            if (current_dominion == null) {
                player_current_dominion_id.put(player.getUniqueId(), null);
                return null;
            }
            player_current_dominion_id.put(player.getUniqueId(), current_dominion.getId());
            // show border
            if (current_dominion.getFlagValue(Flag.SHOW_BORDER)) {
                Particle.showBorder(player, current_dominion);
            }
            return current_dominion;
        }
    }

    @Override
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
        if (!Flag.GLOW.getEnable()) {
            return;
        }
        if (dominion == null) {
            player.setGlowing(false);
            return;
        }
        MemberDTO privilege = getMember(player, dominion);
        if (privilege != null) {
            if (privilege.getGroupId() == -1) {
                player.setGlowing(privilege.getFlagValue(Flag.GLOW));
            } else {
                GroupDTO group = getGroup(privilege.getGroupId());
                if (group != null) {
                    player.setGlowing(group.getFlagValue(Flag.GLOW));
                } else {
                    player.setGlowing(dominion.getFlagValue(Flag.GLOW));
                }
            }
        } else {
            player.setGlowing(dominion.getFlagValue(Flag.GLOW));
        }
    }

    private void flyOrNot(Player player, DominionDTO dominion) {
        for (String flyPN : Dominion.config.getFlyPermissionNodes()) {
            if (player.hasPermission(flyPN)) {
                return;
            }
        }
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (player.isOp() && Dominion.config.getLimitOpBypass()) {
            return;
        }
        if (!Flag.FLY.getEnable()) {
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
                player.setAllowFlight(privilege.getFlagValue(Flag.FLY));
            } else {
                GroupDTO group = getGroup(privilege.getGroupId());
                if (group != null) {
                    player.setAllowFlight(group.getFlagValue(Flag.FLY));
                } else {
                    player.setAllowFlight(dominion.getFlagValue(Flag.FLY));
                }
            }
        } else {
            player.setAllowFlight(dominion.getFlagValue(Flag.FLY));
        }
    }

    @Override
    public GroupDTO getGroup(@NotNull Integer id) {
        return id_groups.get(id);
    }

    @Override
    public MemberDTO getMember(@NotNull Player player, cn.lunadeer.dominion.api.dtos.@NotNull DominionDTO dominion) {
        if (!player_uuid_to_member.containsKey(player.getUniqueId())) return null;
        return player_uuid_to_member.get(player.getUniqueId()).get(dominion.getId());
    }

    @Override
    public MemberDTO getMember(@NotNull UUID player_uuid, cn.lunadeer.dominion.api.dtos.@NotNull DominionDTO dominion) {
        if (!player_uuid_to_member.containsKey(player_uuid)) return null;
        return player_uuid_to_member.get(player_uuid).get(dominion.getId());
    }

    public List<GroupDTO> getBelongGroupsOf(UUID plauer_uuid) {
        List<GroupDTO> groups = new ArrayList<>();
        if (!player_uuid_to_member.containsKey(plauer_uuid)) return groups;
        for (MemberDTO member : player_uuid_to_member.get(plauer_uuid).values()) {
            if (member.getGroupId() != -1) {
                GroupDTO group = getGroup(member.getGroupId());
                if (group != null) {
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    @Override
    public DominionDTO getDominion(@NotNull Integer id) {
        return id_dominions.get(id);
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

    public List<ResMigration.ResidenceNode> getResidenceData(UUID player_uuid) {
        if (residence_data == null) {
            residence_data = new HashMap<>();
            List<ResMigration.ResidenceNode> residences = ResMigration.extractFromResidence(Dominion.instance);
            for (ResMigration.ResidenceNode node : residences) {
                if (node == null) {
                    continue;
                }
                if (!residence_data.containsKey(node.owner)) {
                    XLogger.debug("residence_data put %s", node.owner);
                    residence_data.put(node.owner, new ArrayList<>());
                }
                residence_data.get(node.owner).add(node);
            }
            XLogger.debug("residence_data: %d", residence_data.size());
        }
        return residence_data.get(player_uuid);
    }

    @Override
    public @NotNull List<cn.lunadeer.dominion.api.dtos.DominionDTO> getAllDominions() {
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
    public final Map<UUID, LocalDateTime> NextTimeAllowTeleport = new java.util.HashMap<>();

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
            try (AutoTimer ignored = new AutoTimer(Dominion.config.TimerEnabled())) {
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
            try (AutoTimer ignored = new AutoTimer(Dominion.config.TimerEnabled())) {
                world_dominion_tree_sector_a = new ConcurrentHashMap<>();
                world_dominion_tree_sector_b = new ConcurrentHashMap<>();
                world_dominion_tree_sector_c = new ConcurrentHashMap<>();
                world_dominion_tree_sector_d = new ConcurrentHashMap<>();

                Map<UUID, List<DominionDTO>> world_dominions_sector_a = new HashMap<>();
                Map<UUID, List<DominionDTO>> world_dominions_sector_b = new HashMap<>();
                Map<UUID, List<DominionDTO>> world_dominions_sector_c = new HashMap<>();
                Map<UUID, List<DominionDTO>> world_dominions_sector_d = new HashMap<>();

                // 根据所有领地的最大最小坐标计算象限中心点

                int max_x = dominions.stream().mapToInt(DominionDTO::getX1).max().orElse(0);
                int min_x = dominions.stream().mapToInt(DominionDTO::getX2).min().orElse(0);
                int max_z = dominions.stream().mapToInt(DominionDTO::getZ1).max().orElse(0);
                int min_z = dominions.stream().mapToInt(DominionDTO::getZ2).min().orElse(0);
                section_origin_x = (max_x + min_x) / 2;
                section_origin_z = (max_z + min_z) / 2;
                XLogger.debug("Cache init section origin: %d, %d", section_origin_x, section_origin_z);


                for (DominionDTO d : dominions) {
                    // 对每个世界的领地进行四个象限的划分
                    if (!world_dominions_sector_a.containsKey(d.getWorldUid()) ||
                            !world_dominions_sector_b.containsKey(d.getWorldUid()) ||
                            !world_dominions_sector_c.containsKey(d.getWorldUid()) ||
                            !world_dominions_sector_d.containsKey(d.getWorldUid())) {
                        world_dominions_sector_a.put(d.getWorldUid(), new ArrayList<>());
                        world_dominions_sector_b.put(d.getWorldUid(), new ArrayList<>());
                        world_dominions_sector_c.put(d.getWorldUid(), new ArrayList<>());
                        world_dominions_sector_d.put(d.getWorldUid(), new ArrayList<>());
                    }
                    if (d.getX1() >= section_origin_x && d.getZ1() >= section_origin_z) {
                        world_dominions_sector_a.get(d.getWorldUid()).add(d);
                    } else if (d.getX1() <= section_origin_x && d.getZ1() >= section_origin_z) {
                        if (d.getX2() >= section_origin_x) {
                            world_dominions_sector_a.get(d.getWorldUid()).add(d);
                            world_dominions_sector_b.get(d.getWorldUid()).add(d);
                        } else {
                            world_dominions_sector_b.get(d.getWorldUid()).add(d);
                        }
                    } else if (d.getX1() >= section_origin_x && d.getZ1() <= section_origin_z) {
                        if (d.getZ2() >= section_origin_z) {
                            world_dominions_sector_a.get(d.getWorldUid()).add(d);
                            world_dominions_sector_c.get(d.getWorldUid()).add(d);
                        } else {
                            world_dominions_sector_c.get(d.getWorldUid()).add(d);
                        }
                    } else {
                        if (d.getX2() >= section_origin_x && d.getZ2() >= section_origin_z) {
                            world_dominions_sector_a.get(d.getWorldUid()).add(d);
                            world_dominions_sector_b.get(d.getWorldUid()).add(d);
                            world_dominions_sector_c.get(d.getWorldUid()).add(d);
                            world_dominions_sector_d.get(d.getWorldUid()).add(d);
                        } else if (d.getX2() >= section_origin_x && d.getZ2() <= section_origin_z) {
                            world_dominions_sector_c.get(d.getWorldUid()).add(d);
                            world_dominions_sector_d.get(d.getWorldUid()).add(d);
                        } else if (d.getZ2() >= section_origin_z && d.getX2() <= section_origin_x) {
                            world_dominions_sector_b.get(d.getWorldUid()).add(d);
                            world_dominions_sector_d.get(d.getWorldUid()).add(d);
                        } else {
                            world_dominions_sector_d.get(d.getWorldUid()).add(d);
                        }
                    }
                }
                for (Map.Entry<UUID, List<DominionDTO>> entry : world_dominions_sector_a.entrySet()) {
                    world_dominion_tree_sector_a.put(entry.getKey(), DominionNode.BuildNodeTree(-1, entry.getValue()));
                }
                for (Map.Entry<UUID, List<DominionDTO>> entry : world_dominions_sector_b.entrySet()) {
                    world_dominion_tree_sector_b.put(entry.getKey(), DominionNode.BuildNodeTree(-1, entry.getValue()));
                }
                for (Map.Entry<UUID, List<DominionDTO>> entry : world_dominions_sector_c.entrySet()) {
                    world_dominion_tree_sector_c.put(entry.getKey(), DominionNode.BuildNodeTree(-1, entry.getValue()));
                }
                for (Map.Entry<UUID, List<DominionDTO>> entry : world_dominions_sector_d.entrySet()) {
                    world_dominion_tree_sector_d.put(entry.getKey(), DominionNode.BuildNodeTree(-1, entry.getValue()));
                }
            }
        }
    }

    @Override
    public @Nullable GroupDTO getPlayerUsingGroupTitle(@NotNull UUID uuid) {
        if (!Dominion.config.getGroupTitleEnable()) {
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
