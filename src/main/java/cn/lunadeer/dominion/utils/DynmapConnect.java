package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.minecraftpluginutils.Scheduler;
import cn.lunadeer.minecraftpluginutils.XLogger;
import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.math.Shape;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DynmapConnect extends DynmapCommonAPIListener {

    public static DynmapConnect instance;

    private MarkerSet markerSet;

    @Override
    public void apiEnabled(DynmapCommonAPI dynmapCommonAPI) {
        MarkerAPI markerAPI = dynmapCommonAPI.getMarkerAPI();
        this.markerSet = markerAPI.getMarkerSet("dominion");
        if (this.markerSet == null) {
            this.markerSet = markerAPI.createMarkerSet("dominion", "Dominion领地", null, false);
        }
    }

    public void setDominionMarker(DominionDTO dominion) {
        String nameLabel = "<div>" + dominion.getName() + "</div>";
        double[] xx = {dominion.getX1(), dominion.getX2()};
        double[] zz = {dominion.getZ1(), dominion.getZ2()};
        AreaMarker marker = this.markerSet.createAreaMarker(
                dominion.getId().toString(),
                nameLabel,
                true,
                dominion.getWorld(),
                xx,
                zz,
                false
        );
        marker.setFillStyle(0.2, dominion.getColorHex());
        marker.setLineStyle(1, 0.8, dominion.getColorHex());
        XLogger.debug("Add dominion marker: " + dominion.getName());
    }

    public void setDominionMarkers(List<DominionDTO> dominions) {
        Scheduler.runTaskAsync(() -> {
            for (DominionDTO dominion : dominions) {
                this.setDominionMarker(dominion);
            }
        });
    }

    public void setMCAMarkers(Map<String, List<String>> mca_files) {
        Scheduler.runTaskAsync(() -> {
            for (Map.Entry<String, List<String>> entry : mca_files.entrySet()) {
                for (String file : entry.getValue()) {
                    String[] cords = file.split("\\.");
                    int world_x1 = Integer.parseInt(cords[1]) * 512;
                    int world_x2 = (Integer.parseInt(cords[1]) + 1) * 512;
                    int world_z1 = Integer.parseInt(cords[2]) * 512;
                    int world_z2 = (Integer.parseInt(cords[2]) + 1) * 512;
                    String nameLabel = "<div>" + file + "</div>";
                    double[] xx = {world_x1, world_x2};
                    double[] zz = {world_z1, world_z2};
                    AreaMarker marker = this.markerSet.createAreaMarker(
                            file,
                            nameLabel,
                            true,
                            entry.getKey(),
                            xx,
                            zz,
                            false
                    );
                    marker.setFillStyle(0.2, 0x00CC00);
                    marker.setLineStyle(1, 0.8, 0x00CC00);
                }
            }
        });
    }

}
