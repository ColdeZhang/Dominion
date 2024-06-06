package cn.lunadeer.dominion;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.minecraftpluginutils.Notification;
import cn.lunadeer.minecraftpluginutils.ParticleRender;
import cn.lunadeer.minecraftpluginutils.Scheduler;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static cn.lunadeer.dominion.DominionNode.getLocInDominionDTO;

public class Cache {

    public Cache() {
        player_current_dominion_id = new HashMap<>();
        loadDominions();
        loadPlayerPrivileges();
    }

    /**
     * 从数据库加载所有领地
     */
    public void loadDominions() {
        if (_last_update_dominion.get() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            XLogger.debug("run loadDominionsExecution directly");
            loadDominionsExecution();
        } else {
            if (_update_dominion_is_scheduled.get()) return;
            XLogger.debug("schedule loadDominionsExecution");
            _update_dominion_is_scheduled.set(true);
            long delay_tick = (UPDATE_INTERVAL - (System.currentTimeMillis() - _last_update_dominion.get())) / 1000 * 20L;
            Scheduler.runTaskLaterAsync(() -> {
                        XLogger.debug("run loadDominionsExecution scheduled");
                        loadDominionsExecution();
                        _update_dominion_is_scheduled.set(false);
                    },
                    delay_tick);
        }
    }

    private void loadDominionsExecution() {
        id_dominions = new ConcurrentHashMap<>();
        world_dominion_tree = new ConcurrentHashMap<>();
        dominion_children = new ConcurrentHashMap<>();
        List<DominionDTO> dominions = DominionDTO.selectAll();
        Map<String, List<DominionDTO>> world_dominions = new HashMap<>();
        for (DominionDTO d : dominions) {
            if (!world_dominions.containsKey(d.getWorld())) {
                world_dominions.put(d.getWorld(), new ArrayList<>());
            }
            world_dominions.get(d.getWorld()).add(d);
            id_dominions.put(d.getId(), d);
            if (!dominion_children.containsKey(d.getParentDomId())) {
                dominion_children.put(d.getParentDomId(), new ArrayList<>());
            }
            dominion_children.get(d.getParentDomId()).add(d.getId());
        }
        for (Map.Entry<String, List<DominionDTO>> entry : world_dominions.entrySet()) {
            world_dominion_tree.put(entry.getKey(), DominionNode.BuildNodeTree(-1, entry.getValue()));
        }
        BlueMapConnect.render();
        _last_update_dominion.set(System.currentTimeMillis());
    }

    /**
     * 从数据库加载所有玩家特权
     */
    public void loadPlayerPrivileges() {
        if (_last_update_privilege.get() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            loadPlayerPrivilegesExecution();
        } else {
            if (_update_privilege_is_scheduled.get()) return;
            _update_privilege_is_scheduled.set(true);
            long delay_tick = (UPDATE_INTERVAL - (System.currentTimeMillis() - _last_update_dominion.get())) / 1000 * 20L;
            Scheduler.runTaskLaterAsync(() -> {
                        loadPlayerPrivilegesExecution();
                        _update_privilege_is_scheduled.set(false);
                    },
                    delay_tick);
        }
    }

    private void loadPlayerPrivilegesExecution() {
        List<PlayerPrivilegeDTO> all_privileges = PlayerPrivilegeDTO.selectAll();
        if (all_privileges == null) {
            XLogger.err("加载玩家特权失败");
            return;
        }
        player_uuid_to_privilege = new ConcurrentHashMap<>();
        for (PlayerPrivilegeDTO privilege : all_privileges) {
            UUID player_uuid = privilege.getPlayerUUID();
            if (!player_uuid_to_privilege.containsKey(player_uuid)) {
                player_uuid_to_privilege.put(player_uuid, new ConcurrentHashMap<>());
            }
            player_uuid_to_privilege.get(player_uuid).put(privilege.getDomID(), privilege);
        }
        _last_update_privilege.set(System.currentTimeMillis());
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
                return last_dominion;
            }
        }
        DominionDTO current_dominion = getLocInDominionDTO(world_dominion_tree.get(player.getWorld().getName()), player.getLocation());
        int last_dom_id = last_dominion == null ? -1 : last_dominion.getId();
        int current_dom_id = current_dominion == null ? -1 : current_dominion.getId();
        if (last_dom_id == current_dom_id) {
            return last_dominion;
        }
        if (last_dom_id != -1) {
//            if (last_dominion.getParentDomId() == -1)
//                Notification.info(player, "您已离开领地：%s", last_dominion.getName());
//             else
//                Notification.info(player, "您已离开子领地：%s", last_dominion.getName());
            String msg = last_dominion.getLeaveMessage();
            msg = msg.replace("${DOM_NAME}", last_dominion.getName());
            Notification.actionBar(player, msg);
        }
        if (current_dom_id != -1) {
//            if (current_dominion.getParentDomId() == -1)
//                Notification.info(player, "您正在进入领地：%s", current_dominion.getName());
//            else
//                Notification.info(player, "您正在进入子领地：%s", current_dominion.getName());
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
            ParticleRender.showBoxFace(Dominion.instance, player,
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
        PlayerPrivilegeDTO privilege = getPlayerPrivilege(player, dominion);
        if (privilege != null) {
            player.setGlowing(privilege.getFlagValue(Flag.GLOW));
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
        PlayerPrivilegeDTO privilege = getPlayerPrivilege(player, dominion);
        if (privilege != null) {
            player.setAllowFlight(privilege.getFlagValue(Flag.FLY));
        } else {
            player.setAllowFlight(dominion.getFlagValue(Flag.FLY));
        }
    }

    public DominionDTO getDominion(Location loc) {
        return getLocInDominionDTO(world_dominion_tree.get(loc.getWorld().getName()), loc);
    }

    public List<DominionNode> getDominionTreeByPlayer(String player_name) {
        List<DominionNode> dominionTree = new ArrayList<>();
        PlayerDTO player = PlayerDTO.select(player_name);
        if (player == null) return dominionTree;
        for (List<DominionNode> tree : world_dominion_tree.values()) {
            for (DominionNode node : tree) {
                if (node.dominion.getOwner().equals(player.getUuid())) {
                    dominionTree.add(node);
                }
            }
        }
        return dominionTree;
    }

    public List<DominionNode> getAllDominionTree() {
        List<DominionNode> dominionTree = new ArrayList<>();
        for (List<DominionNode> tree : world_dominion_tree.values()) {
            dominionTree.addAll(tree);
        }
        return dominionTree;
    }

    /**
     * 获取玩家在指定领地的特权
     * 如果玩家不存在特权，则返回null
     *
     * @param player   玩家
     * @param dominion 领地
     * @return 特权表
     */
    public PlayerPrivilegeDTO getPlayerPrivilege(Player player, DominionDTO dominion) {
        if (!player_uuid_to_privilege.containsKey(player.getUniqueId())) return null;
        return player_uuid_to_privilege.get(player.getUniqueId()).get(dominion.getId());
    }

    public PlayerPrivilegeDTO getPlayerPrivilege(UUID player_uuid, DominionDTO dominion) {
        if (!player_uuid_to_privilege.containsKey(player_uuid)) return null;
        return player_uuid_to_privilege.get(player_uuid).get(dominion.getId());
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

    public List<DominionDTO> getDominions() {
        return new ArrayList<>(id_dominions.values());
    }

    public static Cache instance;
    private ConcurrentHashMap<Integer, DominionDTO> id_dominions;
    private ConcurrentHashMap<String, List<DominionNode>> world_dominion_tree;
    private ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, PlayerPrivilegeDTO>> player_uuid_to_privilege;   // 玩家所有的特权
    private final Map<UUID, Integer> player_current_dominion_id;                         // 玩家当前所在领地
    private ConcurrentHashMap<Integer, List<Integer>> dominion_children;
    private final AtomicLong _last_update_dominion = new AtomicLong(0);
    private final AtomicBoolean _update_dominion_is_scheduled = new AtomicBoolean(false);
    private final AtomicLong _last_update_privilege = new AtomicLong(0);
    private final AtomicBoolean _update_privilege_is_scheduled = new AtomicBoolean(false);
    private static final long UPDATE_INTERVAL = 1000 * 4;

    public final Map<UUID, LocalDateTime> NextTimeAllowTeleport = new java.util.HashMap<>();
}
