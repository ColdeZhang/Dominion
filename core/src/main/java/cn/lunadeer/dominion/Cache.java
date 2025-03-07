package cn.lunadeer.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
