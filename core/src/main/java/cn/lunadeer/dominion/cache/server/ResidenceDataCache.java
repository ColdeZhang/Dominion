package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.utils.ResMigration;
import cn.lunadeer.dominion.utils.XLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResidenceDataCache {

    private Map<UUID, CopyOnWriteArrayList<ResMigration.ResidenceNode>> residence_data = null;

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
                    residence_data.put(node.owner, new CopyOnWriteArrayList<>());
                }
                residence_data.get(node.owner).add(node);
            }
            XLogger.debug("residence_data: {0}", residence_data.size());
        }
    }

    public List<ResMigration.ResidenceNode> getResidenceData() {
        updateResidenceData();
        return residence_data.values().stream().reduce(new CopyOnWriteArrayList<>(), (a, b) -> {
            a.addAll(b);
            return a;
        });
    }

    public List<ResMigration.ResidenceNode> getResidenceData(UUID player_uuid) {
        updateResidenceData();
        return residence_data.get(player_uuid);
    }

}
