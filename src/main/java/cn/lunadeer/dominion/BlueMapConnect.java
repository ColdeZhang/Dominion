package cn.lunadeer.dominion;

import cn.lunadeer.dominion.dtos.DominionDTO;
import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BlueMapConnect {
    public static void render() {
        if (!Dominion.config.getBlueMap()) {
            return;
        }
        BlueMapAPI.getInstance().ifPresent(api -> {
            for (Map.Entry<String, List<DominionDTO>> world_dominions : Cache.instance.getWorldDominions().entrySet()) {
                api.getWorld(world_dominions.getKey()).ifPresent(world -> {
                    MarkerSet markerSet = MarkerSet.builder()
                            .label("Dominion")
                            .build();

                    for (DominionDTO dominion : world_dominions.getValue()) {
                        Collection<Vector2d> vectors = new ArrayList<>();
                        vectors.add(new Vector2d(dominion.getX1(), dominion.getZ1()));
                        vectors.add(new Vector2d(dominion.getX2(), dominion.getZ1()));
                        vectors.add(new Vector2d(dominion.getX2(), dominion.getZ2()));
                        vectors.add(new Vector2d(dominion.getX1(), dominion.getZ2()));
                        Shape shape = new Shape(vectors);
                        double x = vectors.iterator().next().getX();
                        double z = vectors.iterator().next().getY();
                        double y = dominion.getY1();

                        Color line = new Color(0, 191, 255, 0.8F);
                        Color fill = new Color(0, 191, 255, 0.2F);
                        if (dominion.getParentDomId() != -1) {  // for children dominion
                            line = new Color(240, 230, 140, 0.8F);
                            fill = new Color(240, 230, 140, 0.2F);
                        }
                        ExtrudeMarker marker = ExtrudeMarker.builder()
                                .label(dominion.getName())
                                .position(x, y, z)
                                .shape(shape, dominion.getY1(), dominion.getY2())
                                .lineColor(line)
                                .fillColor(fill)
                                .build();
                        markerSet.getMarkers()
                                .put(dominion.getName(), marker);
                    }

                    for (BlueMapMap map : world.getMaps()) {
                        map.getMarkerSets().put(world_dominions.getKey() + "-" + markerSet.getLabel(), markerSet);
                    }
                });
            }
        });
    }
}
