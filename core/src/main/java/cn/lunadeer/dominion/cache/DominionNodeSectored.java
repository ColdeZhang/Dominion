package cn.lunadeer.dominion.cache;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.utils.AutoTimer;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static cn.lunadeer.dominion.cache.DominionNode.getDominionNodeByLocation;

/**
 * The WorldSectored class manages the dominion nodes in different sectors of the world.
 */
public class DominionNodeSectored {
    /*
        D | C
        --+--
        B | A
     */

    private ConcurrentHashMap<UUID, CopyOnWriteArrayList<DominionNode>> world_dominion_tree_sector_a; // x >= 0, z >= 0
    private ConcurrentHashMap<UUID, CopyOnWriteArrayList<DominionNode>> world_dominion_tree_sector_b; // x <= 0, z >= 0
    private ConcurrentHashMap<UUID, CopyOnWriteArrayList<DominionNode>> world_dominion_tree_sector_c; // x >= 0, z <= 0
    private ConcurrentHashMap<UUID, CopyOnWriteArrayList<DominionNode>> world_dominion_tree_sector_d; // x <= 0, z <= 0
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
            CopyOnWriteArrayList<DominionNode> nodes = getNodes(loc);
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
    public CopyOnWriteArrayList<DominionNode> getNodes(@NotNull Location loc) {
        return getNodes(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockZ());
    }

    /**
     * Gets the list of DominionNodes for a given world and coordinates.
     *
     * @param world the world to check
     * @param x     the x-coordinate
     * @param z     the z-coordinate
     * @return the list of DominionNodes
     */
    public CopyOnWriteArrayList<DominionNode> getNodes(World world, int x, int z) {
        return getNodes(world.getUID(), x, z);
    }

    /**
     * Gets the list of DominionNodes for a given world UUID and coordinates.
     *
     * @param world the world UUID to check
     * @param x     the x-coordinate
     * @param z     the z-coordinate
     * @return the list of DominionNodes
     */
    public CopyOnWriteArrayList<DominionNode> getNodes(UUID world, int x, int z) {
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
     * Initializes the dominion nodes.
     *
     * @param nodes the list of DominionDTOs to initialize
     */
    public void build(CopyOnWriteArrayList<DominionNode> nodes) {
        try (AutoTimer ignored = new AutoTimer(Configuration.timer)) {
            world_dominion_tree_sector_a = new ConcurrentHashMap<>();
            world_dominion_tree_sector_b = new ConcurrentHashMap<>();
            world_dominion_tree_sector_c = new ConcurrentHashMap<>();
            world_dominion_tree_sector_d = new ConcurrentHashMap<>();

            // calculate the section origin point
            int max_x = nodes.stream().mapToInt(n -> n.getDominion().getCuboid().x2()).max().orElse(0);
            int min_x = nodes.stream().mapToInt(n -> n.getDominion().getCuboid().x1()).min().orElse(0);
            int max_z = nodes.stream().mapToInt(n -> n.getDominion().getCuboid().z2()).max().orElse(0);
            int min_z = nodes.stream().mapToInt(n -> n.getDominion().getCuboid().z1()).min().orElse(0);
            section_origin_x = (max_x + min_x) / 2;
            section_origin_z = (max_z + min_z) / 2;
            XLogger.debug("Cache init section origin: {0}, {1}", section_origin_x, section_origin_z);

            for (DominionNode n : nodes) {
                DominionDTO d = n.getDominion();
                // put dominions into different sectors
                if (!world_dominion_tree_sector_a.containsKey(d.getWorldUid()) ||
                        !world_dominion_tree_sector_b.containsKey(d.getWorldUid()) ||
                        !world_dominion_tree_sector_c.containsKey(d.getWorldUid()) ||
                        !world_dominion_tree_sector_d.containsKey(d.getWorldUid())) {
                    world_dominion_tree_sector_a.put(d.getWorldUid(), new CopyOnWriteArrayList<>());
                    world_dominion_tree_sector_b.put(d.getWorldUid(), new CopyOnWriteArrayList<>());
                    world_dominion_tree_sector_c.put(d.getWorldUid(), new CopyOnWriteArrayList<>());
                    world_dominion_tree_sector_d.put(d.getWorldUid(), new CopyOnWriteArrayList<>());
                }
                if (d.getCuboid().x1() >= section_origin_x && d.getCuboid().z1() >= section_origin_z) {
                    world_dominion_tree_sector_a.get(d.getWorldUid()).add(n);
                } else if (d.getCuboid().x1() <= section_origin_x && d.getCuboid().z1() >= section_origin_z) {
                    if (d.getCuboid().x2() >= section_origin_x) {
                        world_dominion_tree_sector_a.get(d.getWorldUid()).add(n);
                        world_dominion_tree_sector_b.get(d.getWorldUid()).add(n);
                    } else {
                        world_dominion_tree_sector_b.get(d.getWorldUid()).add(n);
                    }
                } else if (d.getCuboid().x1() >= section_origin_x && d.getCuboid().z1() <= section_origin_z) {
                    if (d.getCuboid().z2() >= section_origin_z) {
                        world_dominion_tree_sector_a.get(d.getWorldUid()).add(n);
                        world_dominion_tree_sector_c.get(d.getWorldUid()).add(n);
                    } else {
                        world_dominion_tree_sector_c.get(d.getWorldUid()).add(n);
                    }
                } else {
                    if (d.getCuboid().x2() >= section_origin_x && d.getCuboid().z2() >= section_origin_z) {
                        world_dominion_tree_sector_a.get(d.getWorldUid()).add(n);
                        world_dominion_tree_sector_b.get(d.getWorldUid()).add(n);
                        world_dominion_tree_sector_c.get(d.getWorldUid()).add(n);
                        world_dominion_tree_sector_d.get(d.getWorldUid()).add(n);
                    } else if (d.getCuboid().x2() >= section_origin_x && d.getCuboid().z2() <= section_origin_z) {
                        world_dominion_tree_sector_c.get(d.getWorldUid()).add(n);
                        world_dominion_tree_sector_d.get(d.getWorldUid()).add(n);
                    } else if (d.getCuboid().z2() >= section_origin_z && d.getCuboid().x2() <= section_origin_x) {
                        world_dominion_tree_sector_b.get(d.getWorldUid()).add(n);
                        world_dominion_tree_sector_d.get(d.getWorldUid()).add(n);
                    } else {
                        world_dominion_tree_sector_d.get(d.getWorldUid()).add(n);
                    }
                }
            }
        }
    }
}