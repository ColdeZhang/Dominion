package cn.lunadeer.dominion.utils.map;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;

import java.util.List;
import java.util.Map;

public class MapRender {

    public static void render() {
        if (Dominion.config.getBlueMap()) {
            BlueMapConnect.render();
        }

        if (Dominion.config.getDynmap()) {
            DynmapConnect.instance.setDominionMarkers(Cache.instance.getAllDominions());
        }
    }

    public static void renderMCA(Map<String, List<String>> mca_files) {
        if (Dominion.config.getBlueMap()) {
            BlueMapConnect.renderMCA(mca_files);
        }

        if (Dominion.config.getDynmap()) {
            DynmapConnect.instance.setMCAMarkers(mca_files);
        }
    }

}
