package cn.lunadeer.dominion.cache;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.utils.AutoTimer;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static cn.lunadeer.dominion.cache.DominionNode.getDominionNodeByLocation;

/**
 * The WorldSectored class manages the dominion nodes in different sectors of the world.
 */
public class DominionSectored {
    /*
        D | C
        --+--
        B | A
     */

    private ConcurrentHashMap<UUID, List<DominionNode>> world_dominion_tree_sector_a; // x >= 0, z >= 0
    private ConcurrentHashMap<UUID, List<DominionNode>> world_dominion_tree_sector_b; // x <= 0, z >= 0
    private ConcurrentHashMap<UUID, List<DominionNode>> world_dominion_tree_sector_c; // x >= 0, z <= 0
    private ConcurrentHashMap<UUID, List<DominionNode>> world_dominion_tree_sector_d; // x <= 0, z <= 0
    private Integer section_origin_x = 0;
    private Integer section_origin_z = 0;

    /**
     * Gets the DominionDTO for a given location.
     *
     * @param loc the location to check
     * @return the DominionDTO if found, otherwise null
     */
    public DominionDTO getDominionByLocation(@NotNull Location loc) {
        try (AutoTimer ignored = new AutoTimer(Configuration.timer)) {
            List<DominionNode> nodes = getNodes(loc);
            if (nodes == null) return null;
            if (nodes.isEmpty()) return null;
            DominionNode dominionNode = getDominionNodeByLocation(nodes, loc);
            return dominionNode == null ? null : dominionNode.getDominion();
        }
    }

    /**
     * Gets the list of DominionNodes for a given location.
     *
     * @param loc the location to check
     * @return the list of DominionNodes
     */
    public List<DominionNode> getNodes(@NotNull Location loc) {
        return getNodes(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ());
    }

    /**
     * Gets the list of DominionNodes for a given world and coordinates.
     *
     * @param world the world to check
     * @param x the x-coordinate
     * @param z the z-coordinate
     * @return the list of DominionNodes
     */
    public List<DominionNode> getNodes(World world, int x, int z) {
        return getNodes(world.getUID(), x, z);
    }

    /**
     * Gets the list of DominionNodes for a given world UUID and coordinates.
     *
     * @param world the world UUID to check
     * @param x the x-coordinate
     * @param z the z-coordinate
     * @return the list of DominionNodes
     */
    public List<DominionNode> getNodes(UUID world, int x, int z) {
        if (x >= section_origin_x && z >= section_origin_z) {
            if (world_dominion_tree_sector_a == null) return null;
            return world_dominion_tree_sector_a.get(world);
        }
        if (x <= section_origin_x && z >= section_origin_z) {
            if (world_dominion_tree_sector_b == null) return null;
            return world_dominion_tree_sector_b.get(world);
        }
        if (x >= section_origin_x) {
            if (world_dominion_tree_sector_c == null) return null;
            return world_dominion_tree_sector_c.get(world);
        }
        if (world_dominion_tree_sector_d == null) return null;
        return world_dominion_tree_sector_d.get(world);
    }

    /**
     * Initializes the dominion nodes asynchronously.
     *
     * @param dominions the list of DominionDTOs to initialize
     * @return a CompletableFuture representing the initialization task
     */
    public CompletableFuture<Void> initAsync(List<DominionDTO> dominions) {
        return CompletableFuture.runAsync(() -> init(dominions));
    }

    /**
     * Initializes the dominion nodes.
     *
     * @param dominions the list of DominionDTOs to initialize
     */
    private void init(List<DominionDTO> dominions) {
        try (AutoTimer ignored = new AutoTimer(Configuration.timer)) {
            world_dominion_tree_sector_a = new ConcurrentHashMap<>();
            world_dominion_tree_sector_b = new ConcurrentHashMap<>();
            world_dominion_tree_sector_c = new ConcurrentHashMap<>();
            world_dominion_tree_sector_d = new ConcurrentHashMap<>();

            Map<UUID, List<DominionDTO>> world_dominions_sector_a = new HashMap<>();
            Map<UUID, List<DominionDTO>> world_dominions_sector_b = new HashMap<>();
            Map<UUID, List<DominionDTO>> world_dominions_sector_c = new HashMap<>();
            Map<UUID, List<DominionDTO>> world_dominions_sector_d = new HashMap<>();

            // calculate the section origin point
            int max_x = dominions.stream().mapToInt(d -> d.getServerId() == Configuration.multiServer.serverId ? d.getCuboid().x2() : 0).max().orElse(0);
            int min_x = dominions.stream().mapToInt(d -> d.getServerId() == Configuration.multiServer.serverId ? d.getCuboid().x1() : 0).min().orElse(0);
            int max_z = dominions.stream().mapToInt(d -> d.getServerId() == Configuration.multiServer.serverId ? d.getCuboid().z2() : 0).max().orElse(0);
            int min_z = dominions.stream().mapToInt(d -> d.getServerId() == Configuration.multiServer.serverId ? d.getCuboid().z1() : 0).min().orElse(0);
            section_origin_x = (max_x + min_x) / 2;
            section_origin_z = (max_z + min_z) / 2;
            XLogger.debug("Cache init section origin: {0}, {1}", section_origin_x, section_origin_z);

            for (DominionDTO d : dominions) {
                // put dominions into different sectors
                if (!world_dominions_sector_a.containsKey(d.getWorldUid()) ||
                        !world_dominions_sector_b.containsKey(d.getWorldUid()) ||
                        !world_dominions_sector_c.containsKey(d.getWorldUid()) ||
                        !world_dominions_sector_d.containsKey(d.getWorldUid())) {
                    world_dominions_sector_a.put(d.getWorldUid(), new ArrayList<>());
                    world_dominions_sector_b.put(d.getWorldUid(), new ArrayList<>());
                    world_dominions_sector_c.put(d.getWorldUid(), new ArrayList<>());
                    world_dominions_sector_d.put(d.getWorldUid(), new ArrayList<>());
                }
                if (d.getCuboid().x1() >= section_origin_x && d.getCuboid().z1() >= section_origin_z) {
                    world_dominions_sector_a.get(d.getWorldUid()).add(d);
                } else if (d.getCuboid().x1() <= section_origin_x && d.getCuboid().z1() >= section_origin_z) {
                    if (d.getCuboid().x2() >= section_origin_x) {
                        world_dominions_sector_a.get(d.getWorldUid()).add(d);
                        world_dominions_sector_b.get(d.getWorldUid()).add(d);
                    } else {
                        world_dominions_sector_b.get(d.getWorldUid()).add(d);
                    }
                } else if (d.getCuboid().x1() >= section_origin_x && d.getCuboid().z1() <= section_origin_z) {
                    if (d.getCuboid().z2() >= section_origin_z) {
                        world_dominions_sector_a.get(d.getWorldUid()).add(d);
                        world_dominions_sector_c.get(d.getWorldUid()).add(d);
                    } else {
                        world_dominions_sector_c.get(d.getWorldUid()).add(d);
                    }
                } else {
                    if (d.getCuboid().x2() >= section_origin_x && d.getCuboid().z2() >= section_origin_z) {
                        world_dominions_sector_a.get(d.getWorldUid()).add(d);
                        world_dominions_sector_b.get(d.getWorldUid()).add(d);
                        world_dominions_sector_c.get(d.getWorldUid()).add(d);
                        world_dominions_sector_d.get(d.getWorldUid()).add(d);
                    } else if (d.getCuboid().x2() >= section_origin_x && d.getCuboid().z2() <= section_origin_z) {
                        world_dominions_sector_c.get(d.getWorldUid()).add(d);
                        world_dominions_sector_d.get(d.getWorldUid()).add(d);
                    } else if (d.getCuboid().z2() >= section_origin_z && d.getCuboid().x2() <= section_origin_x) {
                        world_dominions_sector_b.get(d.getWorldUid()).add(d);
                        world_dominions_sector_d.get(d.getWorldUid()).add(d);
                    } else {
                        world_dominions_sector_d.get(d.getWorldUid()).add(d);
                    }
                }
            }
            // build dominion tree for each sector
            world_dominions_sector_a.forEach((key, value) ->
                    world_dominion_tree_sector_a.put(key, DominionNode.BuildNodeTree(-1, value))
            );
            world_dominions_sector_b.forEach((key, value) ->
                    world_dominion_tree_sector_b.put(key, DominionNode.BuildNodeTree(-1, value))
            );
            world_dominions_sector_c.forEach((key, value) ->
                    world_dominion_tree_sector_c.put(key, DominionNode.BuildNodeTree(-1, value))
            );
            world_dominions_sector_d.forEach((key, value) ->
                    world_dominion_tree_sector_d.put(key, DominionNode.BuildNodeTree(-1, value))
            );
        }
    }
}