package cn.lunadeer.dominion.cache;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.scheduler.Scheduler;
import cn.lunadeer.dominion.utils.webMap.MapRender;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Cache implements Listener {
    private final int serverId;

    private ConcurrentHashMap<Integer, DominionDTO> idDominions;    // Dominion ID -> DominionDTO
    private ConcurrentHashMap<String, Integer> dominionNameToId;    // Dominion name -> Dominion ID
    private ConcurrentHashMap<Integer, GroupDTO> idGroups;  // Group ID -> GroupDTO
    private ConcurrentHashMap<Integer, List<Integer>> dominionToChildrenMap;    // Dominion ID -> List of child Dominion IDs
    private ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, MemberDTO>> playerToMembersMap;    // Player UUID -> Map of Dominion ID -> MemberDTO

    private final DominionSectored dominionSectored = new DominionSectored();

    private final Map<UUID, Integer> playerCurrentDominionId = new HashMap<>(); // 玩家当前所在领地

    private final AtomicLong _last_update_dominion = new AtomicLong(0);
    private final AtomicBoolean _update_dominion_is_scheduled = new AtomicBoolean(false);

    private final AtomicLong _last_update_member = new AtomicLong(0);
    private final AtomicBoolean _update_member_is_scheduled = new AtomicBoolean(false);

    private final AtomicLong _last_update_group = new AtomicLong(0);
    private final AtomicBoolean _update_group_is_scheduled = new AtomicBoolean(false);

    private static final long UPDATE_INTERVAL = 1000 * 4;

    private boolean recheckPlayerState = false; // 是否需要重新检查玩家状态（发光、飞行）

    public Cache(int serverId) {
        this.serverId = serverId;
    }

    public Integer getServerId() {
        return serverId;
    }

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
                idDominions = new ConcurrentHashMap<>();
                dominionNameToId = new ConcurrentHashMap<>();
                dominionToChildrenMap = new ConcurrentHashMap<>();

                List<DominionDTO> dominions;
                try {
                    dominions = new ArrayList<>(cn.lunadeer.dominion.dtos.DominionDTO.selectAll());
                } catch (SQLException e) {
                    XLogger.error("loadDominionsExecution error: {0}", e.getMessage());
                    return;
                }
                CompletableFuture<Void> res = dominionSectored.initAsync(dominions);
                count = dominions.size();

                for (DominionDTO d : dominions) {
                    idDominions.put(d.getId(), d);
                    dominionNameToId.put(d.getName(), d.getId());
                    if (!dominionToChildrenMap.containsKey(d.getParentDomId())) {
                        dominionToChildrenMap.put(d.getParentDomId(), new ArrayList<>());
                    }
                    dominionToChildrenMap.get(d.getParentDomId()).add(d.getId());
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
                if (dominion == null && idDominions.containsKey(idToLoad)) {
                    idDominions.remove(idToLoad);
                } else if (dominion != null) {
                    idDominions.put(idToLoad, dominion);
                    count = 1;
                }
                // rebuild dominionNameToId and dominionToChildrenMap
                dominionNameToId = new ConcurrentHashMap<>();
                dominionToChildrenMap = new ConcurrentHashMap<>();
                for (DominionDTO d : idDominions.values()) {
                    dominionNameToId.put(d.getName(), d.getId());
                    if (!dominionToChildrenMap.containsKey(d.getParentDomId())) {
                        dominionToChildrenMap.put(d.getParentDomId(), new ArrayList<>());
                    }
                    dominionToChildrenMap.get(d.getParentDomId()).add(d.getId());
                }
            }
            MapRender.render();
            recheckPlayerState = true;
            _last_update_dominion.set(System.currentTimeMillis());
            XLogger.debug("loadDominionsExecution cost: {0} ms for {1} dominions"
                    , System.currentTimeMillis() - start, count);
        });
    }
}
