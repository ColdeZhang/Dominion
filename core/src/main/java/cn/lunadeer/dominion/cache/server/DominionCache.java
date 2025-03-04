package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.cache.DominionNodeSectored;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DominionCache extends Cache {
    private ConcurrentHashMap<Integer, DominionDTO> idDominions;            // Dominion ID -> DominionDTO
    private ConcurrentHashMap<Integer, List<Integer>> dominionChildrenMap;  // Dominion ID -> Children Dominion ID
    private ConcurrentHashMap<String, Integer> dominionNameToId;            // Dominion name -> Dominion ID

    // dominion nodes sectored by location, for fast location-based dominion lookup
    private final DominionNodeSectored dominionNodeSectored = new DominionNodeSectored();

    public @Nullable DominionDTO getDominion(Integer id) {
        return idDominions.get(id);
    }

    public @NotNull List<Integer> getChildrenId(Integer id) {
        if (dominionChildrenMap.containsKey(id)) {
            return dominionChildrenMap.get(id);
        } else {
            return new ArrayList<>();
        }
    }

    public @Nullable DominionDTO getDominion(@NotNull Location location) {
        return dominionNodeSectored.getDominionByLocation(location);
    }

    @Override
    void loadExecution() {

    }

    @Override
    void loadExecution(Integer idToLoad) {

    }

    @Override
    void updateExecution(Integer idToUpdate) {

    }

    @Override
    void deleteExecution(Integer idToDelete) {

    }


//    public void load() {
//        load(null);
//    }
//
//    public void load(Integer idToLoad) {
//        if (lastUpdate.get() + UPDATE_INTERVAL < System.currentTimeMillis()) {
//            XLogger.debug("run loadDominionsExecution directly");
//            loadDominionsExecution(idToLoad);
//        } else {
//            if (updateScheduled.get()) return;
//            XLogger.debug("schedule loadDominionsExecution");
//            updateScheduled.set(true);
//            long delay_tick = (UPDATE_INTERVAL - (System.currentTimeMillis() - lastUpdate.get())) / 1000 * 20L;
//            Scheduler.runTaskLaterAsync(() -> {
//                        XLogger.debug("run loadDominionsExecution scheduled");
//                        loadDominionsExecution(idToLoad);
//                        updateScheduled.set(false);
//                    },
//                    delay_tick);
//        }
//    }
//
//    private void loadDominionsExecution(Integer idToLoad) {
//        Scheduler.runTaskAsync(() -> {
//            long start = System.currentTimeMillis();
//            int count = 0;
//            if (idToLoad == null) {
//                idDominions = new ConcurrentHashMap<>();
//                dominionNameToId = new ConcurrentHashMap<>();
//                idDominionNodes = new ConcurrentHashMap<>();
//
//                List<DominionDTO> dominions;
//                try {
//                    dominions = new ArrayList<>(cn.lunadeer.dominion.dtos.DominionDTO.selectAll());
//                } catch (SQLException e) {
//                    XLogger.error("loadDominionsExecution error: {0}", e.getMessage());
//                    return;
//                }
//                List<DominionNode> tree = DominionNode.BuildNodeTree(-1, dominions);
//                CompletableFuture<Void> res = dominionSectored.initAsync(tree);
//                count = dominions.size();
//                for (DominionDTO d : dominions) {
//                    idDominions.put(d.getId(), d);
//                    dominionNameToId.put(d.getName(), d.getId());
//                }
//                for (DominionNode n : tree) {
//                    idDominionNodes.put(n.getDominion().getId(), n);
//                }
//                res.join(); // 等待树的构建完成
//
//            } else {
//                DominionDTO dominion;
//                try {
//                    dominion = cn.lunadeer.dominion.dtos.DominionDTO.select(idToLoad);
//                } catch (SQLException e) {
//                    XLogger.error("loadDominionsExecution error: {0}", e.getMessage());
//                    return;
//                }
//                if (dominion == null && idDominions.containsKey(idToLoad)) {
//                    idDominions.remove(idToLoad);
//                } else if (dominion != null) {
//                    idDominions.put(idToLoad, dominion);
//                    count = 1;
//                }
//                // rebuild dominionNameToId and dominionToChildrenMap
//                dominionNameToId = new ConcurrentHashMap<>();
//                idDominionNodes = new ConcurrentHashMap<>();
//                for (DominionDTO d : idDominions.values()) {
//                    dominionNameToId.put(d.getName(), d.getId());
//                }
//            }
//            MapRender.render();
//            recheckPlayerState = true;
//            lastUpdate.set(System.currentTimeMillis());
//            XLogger.debug("loadDominionsExecution cost: {0} ms for {1} dominions"
//                    , System.currentTimeMillis() - start, count);
//        });
//    }
}
