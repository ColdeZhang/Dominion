package cn.lunadeer.dominion.utils.webMap;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.configuration.Configuration;

import java.util.List;
import java.util.Map;

public class MapRender {

    public static void render() {
        if (Configuration.webMapRenderer.blueMap) {
            BlueMapConnect.render();
        }

        if (Configuration.webMapRenderer.dynmap) {
            DynmapConnect.instance.setDominionMarkers(Cache.instance.getAllDominions());
        }
    }

    public static void renderMCA(Map<String, List<String>> mca_files) {
        if (Configuration.webMapRenderer.blueMap) {
            BlueMapConnect.renderMCA(mca_files);
        }

        if (Configuration.webMapRenderer.dynmap) {
            DynmapConnect.instance.setMCAMarkers(mca_files);
        }
    }

}
