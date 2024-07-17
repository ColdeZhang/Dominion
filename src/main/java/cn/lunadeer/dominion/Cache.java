package cn.lunadeer.dominion;

import cn.lunadeer.dominion.dtos.*;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.ParticleRender;
import cn.lunadeer.minecraftpluginutils.Scheduler;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static cn.lunadeer.dominion.DominionNode.getLocInDominionNode;

public class Cache {

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
            BlueMapConnect.render();
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
                for (GroupDTO group : groups) {
                    id_groups.put(group.getId(), group);
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

    /**
     * 获取玩家当前所在领地
     * 此方法会先判断缓存中是否有玩家当前所在领地，如果没有则遍历所有领地判断玩家所在位置
     * 如果玩家不在任何领地内，则返回null
     * 如果玩家在领地内，则返回领地信息
     *
     * @param player 玩家
     * @return 玩家当前所在领地
     */
    public DominionDTO getPlayerCurrentDominion(Player player) {
        Integer last_in_dom_id = player_current_dominion_id.get(player.getUniqueId());
        DominionDTO last_dominion = null;
        if (last_in_dom_id != null) {
            last_dominion = id_dominions.get(last_in_dom_id);
        }
        if (isInDominion(last_dominion, player)) {
            if (dominion_children.get(last_in_dom_id) == null || dominion_children.get(last_in_dom_id).size() == 0) {
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
            String msg = last_dominion.getLeaveMessage();
            msg = msg.replace("${DOM_NAME}", last_dominion.getName());
            Notification.actionBar(player, msg);
        }
        if (current_dom_id != -1) {
            String msg = current_dominion.getJoinMessage();
            msg = msg.replace("${DOM_NAME}", current_dominion.getName());
            Notification.actionBar(player, msg);
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
            ParticleRender.showBoxFace(player,
                    current_dominion.getLocation1(),
                    current_dominion.getLocation2());
        }
        return current_dominion;
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

    public DominionDTO getDominionByLoc(Location loc) {
        return dominion_trees.getLocInDominionDTO(loc);
    }

    public GroupDTO getGroup(Integer id) {
        return id_groups.get(id);
    }

    /**
     * 获取玩家在指定领地的特权
     * 如果玩家不存在特权，则返回null
     *
     * @param player   玩家
     * @param dominion 领地
     * @return 特权表
     */
    public MemberDTO getMember(Player player, DominionDTO dominion) {
        if (!player_uuid_to_member.containsKey(player.getUniqueId())) return null;
        return player_uuid_to_member.get(player.getUniqueId()).get(dominion.getId());
    }

    public MemberDTO getMember(UUID player_uuid, DominionDTO dominion) {
        if (!player_uuid_to_member.containsKey(player_uuid)) return null;
        return player_uuid_to_member.get(player_uuid).get(dominion.getId());
    }

    private static boolean isInDominion(@Nullable DominionDTO dominion, Player player) {
        if (dominion == null) return false;
        if (!Objects.equals(dominion.getWorld(), player.getWorld().getName())) return false;
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        return x >= dominion.getX1() && x <= dominion.getX2() &&
                y >= dominion.getY1() && y <= dominion.getY2() &&
                z >= dominion.getZ1() && z <= dominion.getZ2();
    }

    public DominionDTO getDominion(Integer id) {
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

    public List<DominionDTO> getDominions() {
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

    private static class WorldDominionTreeSectored {
    /*
        D | C
        --+--
        B | A
     */

        private ConcurrentHashMap<String, List<DominionNode>> world_dominion_tree_sector_a; // x >= 0, z >= 0
        private ConcurrentHashMap<String, List<DominionNode>> world_dominion_tree_sector_b; // x <= 0, z >= 0
        private ConcurrentHashMap<String, List<DominionNode>> world_dominion_tree_sector_c; // x >= 0, z <= 0
        private ConcurrentHashMap<String, List<DominionNode>> world_dominion_tree_sector_d; // x <= 0, z <= 0

        public DominionDTO getLocInDominionDTO(@NotNull Location loc) {
            List<DominionNode> nodes = getNodes(loc);
            if (nodes == null) return null;
            if (nodes.isEmpty()) return null;
            DominionNode dominionNode = getLocInDominionNode(nodes, loc);
            return dominionNode == null ? null : dominionNode.getDominion();
        }


        public List<DominionNode> getNodes(Location loc) {
            return getNodes(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockZ());
        }

        public List<DominionNode> getNodes(String world, int x, int z) {
            if (x >= 0 && z >= 0) {
                return world_dominion_tree_sector_a.get(world);
            }
            if (x <= 0 && z >= 0) {
                return world_dominion_tree_sector_b.get(world);
            }
            if (x >= 0) {
                return world_dominion_tree_sector_c.get(world);
            }
            return world_dominion_tree_sector_d.get(world);
        }

        public CompletableFuture<Void> initAsync(List<DominionDTO> dominions) {
            return CompletableFuture.runAsync(() -> init(dominions));
        }


        private void init(List<DominionDTO> dominions) {
            world_dominion_tree_sector_a = new ConcurrentHashMap<>();
            world_dominion_tree_sector_b = new ConcurrentHashMap<>();
            world_dominion_tree_sector_c = new ConcurrentHashMap<>();
            world_dominion_tree_sector_d = new ConcurrentHashMap<>();

            Map<String, List<DominionDTO>> world_dominions_sector_a = new HashMap<>();
            Map<String, List<DominionDTO>> world_dominions_sector_b = new HashMap<>();
            Map<String, List<DominionDTO>> world_dominions_sector_c = new HashMap<>();
            Map<String, List<DominionDTO>> world_dominions_sector_d = new HashMap<>();
            for (DominionDTO d : dominions) {
                // 对每个世界的领地进行四个象限的划分
                if (!world_dominions_sector_a.containsKey(d.getWorld()) ||
                        !world_dominions_sector_b.containsKey(d.getWorld()) ||
                        !world_dominions_sector_c.containsKey(d.getWorld()) ||
                        !world_dominions_sector_d.containsKey(d.getWorld())) {
                    world_dominions_sector_a.put(d.getWorld(), new ArrayList<>());
                    world_dominions_sector_b.put(d.getWorld(), new ArrayList<>());
                    world_dominions_sector_c.put(d.getWorld(), new ArrayList<>());
                    world_dominions_sector_d.put(d.getWorld(), new ArrayList<>());
                }
                if (d.getX1() >= 0 && d.getZ1() >= 0) {
                    world_dominions_sector_a.get(d.getWorld()).add(d);
                } else if (d.getX1() <= 0 && d.getZ1() >= 0) {
                    if (d.getX2() >= 0) {
                        world_dominions_sector_a.get(d.getWorld()).add(d);
                        world_dominions_sector_b.get(d.getWorld()).add(d);
                    } else {
                        world_dominions_sector_b.get(d.getWorld()).add(d);
                    }
                } else if (d.getX1() >= 0 && d.getZ1() <= 0) {
                    if (d.getZ2() >= 0) {
                        world_dominions_sector_a.get(d.getWorld()).add(d);
                        world_dominions_sector_c.get(d.getWorld()).add(d);
                    } else {
                        world_dominions_sector_c.get(d.getWorld()).add(d);
                    }
                } else {
                    if (d.getX2() >= 0 && d.getZ2() >= 0) {
                        world_dominions_sector_a.get(d.getWorld()).add(d);
                        world_dominions_sector_b.get(d.getWorld()).add(d);
                        world_dominions_sector_c.get(d.getWorld()).add(d);
                        world_dominions_sector_d.get(d.getWorld()).add(d);
                    } else if (d.getX2() >= 0 && d.getZ2() <= 0) {
                        world_dominions_sector_c.get(d.getWorld()).add(d);
                        world_dominions_sector_d.get(d.getWorld()).add(d);
                    } else if (d.getZ2() >= 0 && d.getX2() <= 0) {
                        world_dominions_sector_b.get(d.getWorld()).add(d);
                        world_dominions_sector_d.get(d.getWorld()).add(d);
                    } else {
                        world_dominions_sector_d.get(d.getWorld()).add(d);
                    }
                }
            }
            for (Map.Entry<String, List<DominionDTO>> entry : world_dominions_sector_a.entrySet()) {
                world_dominion_tree_sector_a.put(entry.getKey(), DominionNode.BuildNodeTree(-1, entry.getValue()));
            }
            for (Map.Entry<String, List<DominionDTO>> entry : world_dominions_sector_b.entrySet()) {
                world_dominion_tree_sector_b.put(entry.getKey(), DominionNode.BuildNodeTree(-1, entry.getValue()));
            }
            for (Map.Entry<String, List<DominionDTO>> entry : world_dominions_sector_c.entrySet()) {
                world_dominion_tree_sector_c.put(entry.getKey(), DominionNode.BuildNodeTree(-1, entry.getValue()));
            }
            for (Map.Entry<String, List<DominionDTO>> entry : world_dominions_sector_d.entrySet()) {
                world_dominion_tree_sector_d.put(entry.getKey(), DominionNode.BuildNodeTree(-1, entry.getValue()));
            }
        }
    }
}
