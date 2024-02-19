package cn.lunadeer.dominion;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class Cache {

    public Cache() {
        player_current_dominion = new HashMap<>();
        loadDominions();
        loadPlayerPrivileges();
    }

    /**
     * 从数据库加载所有领地
     */
    public void loadDominions() {
        id_dominions = new HashMap<>();
        world_dominions = new HashMap<>();
        dominion_children = new HashMap<>();
        List<DominionDTO> dominions = DominionDTO.selectAll();
        for (DominionDTO d : dominions) {
            if (!dominion_children.containsKey(d.getId())) {
                dominion_children.put(d.getId(), new ArrayList<>());
            }
            id_dominions.put(d.getId(), d);
            if (!world_dominions.containsKey(d.getWorld())) {
                world_dominions.put(d.getWorld(), new ArrayList<>());
            }
            world_dominions.get(d.getWorld()).add(d.getId());
            if (d.getParentDomId() != -1) {
                if (!dominion_children.containsKey(d.getParentDomId())) {
                    dominion_children.put(d.getParentDomId(), new ArrayList<>());
                }
                dominion_children.get(d.getParentDomId()).add(d.getId());
            }
        }
        BlueMapConnect.render();
    }

    /**
     * 从数据库加载所有玩家特权
     */
    public void loadPlayerPrivileges() {
        List<PlayerPrivilegeDTO> all_privileges = PlayerPrivilegeDTO.selectAll();
        if (all_privileges == null) {
            XLogger.err("加载玩家特权失败");
            return;
        }
        player_uuid_to_privilege = new HashMap<>();
        for (PlayerPrivilegeDTO privilege : all_privileges) {
            UUID player_uuid = privilege.getPlayerUUID();
            if (!player_uuid_to_privilege.containsKey(player_uuid)) {
                player_uuid_to_privilege.put(player_uuid, new HashMap<>());
            }
            player_uuid_to_privilege.get(player_uuid).put(privilege.getDomID(), privilege);
        }
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
        Integer dominion_id = player_current_dominion.get(player.getUniqueId());
        DominionDTO dominion = id_dominions.get(dominion_id);
        if (dominion != null) {
            if (!isInDominion(dominion, player)) {
                // glow
                player.setGlowing(false);
                if (dominion.getParentDomId() == -1) {
                    Notification.info(player, "您已离开领地：" + dominion.getName());
                    Notification.info(player, dominion.getLeaveMessage());
                    player_current_dominion.put(player.getUniqueId(), null);
                    dominion = null;
                } else {
                    Notification.info(player, "您已离开子领地：" + dominion.getName());
                    Notification.info(player, dominion.getLeaveMessage());
                    dominion = id_dominions.get(dominion.getParentDomId());
                    update_player_current_dominion(player, dominion);
                }
            } else {
                // 如果在领地内则检查是否在子领地内
                List<Integer> children = dominion_children.get(dominion.getId());
                for (Integer child_id : children) {
                    DominionDTO child = id_dominions.get(child_id);
                    if (isInDominion(child, player)) {
                        dominion = child;
                        Notification.info(player, "您正在进入子领地：" + dominion.getName());
                        Notification.info(player, dominion.getJoinMessage());
                        update_player_current_dominion(player, dominion);
                        break;
                    }
                }
            }
        }
        if (dominion == null) {
            String world = player.getWorld().getName();
            List<Integer> dominions_id = world_dominions.get(world);
            if (dominions_id == null) return null;
            List<DominionDTO> in_dominions = new ArrayList<>();
            for (Integer id : dominions_id) {
                DominionDTO d = id_dominions.get(id);
                if (isInDominion(d, player)) {
                    in_dominions.add(d);
                }
            }
            if (in_dominions.size() == 0) return null;
            in_dominions.sort(Comparator.comparingInt(DominionDTO::getId));
            dominion = in_dominions.get(0);
            Notification.info(player, "您正在进入领地：" + dominion.getName());
            Notification.info(player, dominion.getJoinMessage());
            update_player_current_dominion(player, dominion);
        }
        return dominion;
    }

    private void update_player_current_dominion(Player player, DominionDTO dominion) {
        player_current_dominion.put(player.getUniqueId(), dominion.getId());
        // glow
        PlayerPrivilegeDTO privilege = getPlayerPrivilege(player, dominion);
        if (privilege != null) {
            if (privilege.getGlow()) {
                player.setGlowing(true);
            }
        } else {
            if (dominion.getGlow()) {
                player.setGlowing(true);
            }
        }
    }

    public DominionDTO getDominion(Location loc) {
        String world = loc.getWorld().getName();
        List<Integer> dominions_id = world_dominions.get(world);
        if (dominions_id == null) return null;
        List<DominionDTO> in_dominions = new ArrayList<>();
        for (Integer id : dominions_id) {
            DominionDTO d = id_dominions.get(id);
            if (isInDominion(d, loc.getX(), loc.getY(), loc.getZ())) {
                in_dominions.add(d);
            }
        }
        if (in_dominions.size() == 0) return null;
        in_dominions.sort(Comparator.comparingInt(DominionDTO::getId));
        return in_dominions.get(in_dominions.size() - 1);
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

    private static boolean isInDominion(@Nullable DominionDTO dominion, Player player) {
        if (dominion == null) return false;
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        return x >= dominion.getX1() && x <= dominion.getX2() &&
                y >= dominion.getY1() && y <= dominion.getY2() &&
                z >= dominion.getZ1() && z <= dominion.getZ2();
    }

    private static boolean isInDominion(@Nullable DominionDTO dominion, double x, double y, double z) {
        if (dominion == null) return false;
        return x >= dominion.getX1() && x <= dominion.getX2() &&
                y >= dominion.getY1() && y <= dominion.getY2() &&
                z >= dominion.getZ1() && z <= dominion.getZ2();
    }

    public Map<String, List<Integer>> getWorldDominions() {
        return world_dominions;
    }

    public DominionDTO getDominion(Integer id) {
        return id_dominions.get(id);
    }

    public static Cache instance;
    private Map<Integer, DominionDTO> id_dominions;
    private Map<String, List<Integer>> world_dominions;                         // 所有领地
    private Map<UUID, Map<Integer, PlayerPrivilegeDTO>> player_uuid_to_privilege;   // 玩家所有的特权
    private Map<UUID, Integer> player_current_dominion;                         // 玩家当前所在领地
    private Map<Integer, List<Integer>> dominion_children;
}
