package cn.lunadeer.dominion;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
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
        world_dominions = new HashMap<>();
        List<DominionDTO> dominions = DominionDTO.selectAll();
        for (DominionDTO d : dominions) {
            if (!world_dominions.containsKey(d.getWorld())) {
                world_dominions.put(d.getWorld(), new ArrayList<>());
            }
            world_dominions.get(d.getWorld()).add(d);
        }
    }

    /**
     * 从数据库加载所有玩家特权
     */
    public void loadPlayerPrivileges() {
        player_current_dominion = new HashMap<>();
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
        DominionDTO dominion = player_current_dominion.get(player.getUniqueId());
        if (dominion != null) {
            if (!isInDominion(dominion, player)) {
                Notification.info(player, "您已离开领地：" + dominion.getName());
                Notification.info(player, dominion.getLeaveMessage());
                player_current_dominion.put(player.getUniqueId(), null);
                dominion = null;
            }
        }
        if (dominion == null) {
            String world = player.getWorld().getName();
            List<DominionDTO> dominions = world_dominions.get(world);
            if (dominions == null) return null;
            List<DominionDTO> in_dominions = new ArrayList<>();
            for (DominionDTO d : dominions) {
                if (isInDominion(d, player)) {
                    in_dominions.add(d);
                }
            }
            if (in_dominions.size() == 0) return null;
            in_dominions.sort(Comparator.comparingInt(DominionDTO::getId));
            dominion = in_dominions.get(in_dominions.size() - 1);
            player_current_dominion.put(player.getUniqueId(), dominion);
            Notification.info(player, "您正在进入领地：" + dominion.getName());
            Notification.info(player, dominion.getJoinMessage());
        }
        return dominion;
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

    public static Cache instance;
    private Map<String, List<DominionDTO>> world_dominions;                         // 所有领地
    private Map<UUID, Map<Integer, PlayerPrivilegeDTO>> player_uuid_to_privilege;   // 玩家所有的特权
    private Map<UUID, DominionDTO> player_current_dominion;                         // 玩家当前所在领地
}
