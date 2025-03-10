package cn.lunadeer.dominion.cache;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.misc.DominionException;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static cn.lunadeer.dominion.misc.Others.isInDominion;

/**
 * The DominionNode class represents a node in the dominion tree structure.
 * <p>
 * DominionNode not store the dominion data, only the id of the dominion.
 */
public class DominionNode {
    private final Integer dominionId;
    private List<DominionNode> children = new ArrayList<>();

    public DominionNode(Integer dominionId) {
        this.dominionId = dominionId;
    }

    /**
     * Gets the DominionDTO associated with this node.
     * <p>
     * This method will fetch the DominionDTO from the cache.
     *
     * @return the DominionDTO associated with this node
     */
    public @NotNull DominionDTO getDominion() {
        DominionDTO dominion = CacheManager.instance.getDominion(dominionId);
        if (dominion == null) {
            throw new DominionException(Language.convertsText.unknownDominion, dominionId);
        }
        return dominion;
    }

    /**
     * Gets the list of child nodes.
     *
     * @return the list of child nodes
     */
    public List<DominionNode> getChildren() {
        return children;
    }

    /**
     * Builds a dominion node tree from a list of DominionDTOs.
     *
     * @param rootId    the root ID of the tree
     * @param dominions the list of DominionDTOs to build the tree from
     * @return the list of root DominionNodes
     */
    public static List<DominionNode> BuildNodeTree(Integer rootId, List<DominionDTO> dominions) {
        // Map parent node ID to its list of child nodes
        Map<Integer, List<DominionDTO>> parentToChildrenMap = new HashMap<>();
        for (DominionDTO dominion : dominions) {
            parentToChildrenMap
                    .computeIfAbsent(dominion.getParentDomId(), k -> new ArrayList<>())
                    .add(dominion);
        }

        // Recursively build the node tree
        return buildTree(rootId, parentToChildrenMap);
    }

    /**
     * Recursively builds a dominion node tree.
     *
     * @param rootId              the root ID of the tree
     * @param parentToChildrenMap the map of parent node IDs to their child nodes
     * @return the list of root DominionNodes
     */
    private static List<DominionNode> buildTree(Integer rootId, Map<Integer, List<DominionDTO>> parentToChildrenMap) {
        List<DominionNode> dominionTree = new ArrayList<>();
        List<DominionDTO> children = parentToChildrenMap.get(rootId);

        if (children != null) {
            for (DominionDTO dominion : children) {
                DominionNode node = new DominionNode(dominion.getId());
                node.children = buildTree(dominion.getId(), parentToChildrenMap);
                dominionTree.add(node);
            }
        }

        return dominionTree;
    }

    /**
     * Gets the DominionNode that contains the specified location.
     *
     * @param nodes the list of DominionNodes to search
     * @param loc   the location to check
     * @return the DominionNode that contains the location, or null if not found
     */
    public static DominionNode getDominionNodeByLocation(@NotNull List<DominionNode> nodes, @NotNull Location loc) {
        for (DominionNode node : nodes) {
            if (isInDominion(node.getDominion(), loc)) {
                if (node.children.isEmpty()) {
                    return node;
                } else {
                    DominionNode childDominion = getDominionNodeByLocation(node.children, loc);
                    return Objects.requireNonNullElse(childDominion, node);
                }
            }
        }
        return null;
    }
}