package cn.lunadeer.dominion;

import cn.lunadeer.dominion.dtos.DominionDTO;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class DominionNode {
    public DominionDTO dominion;
    public List<DominionNode> children = new ArrayList<>();

    public static List<DominionNode> BuildNodeTree(Integer rootId, List<DominionDTO> dominions) {
        // 映射父节点ID到其子节点列表
        Map<Integer, List<DominionDTO>> parentToChildrenMap = new HashMap<>();
        for (DominionDTO dominion : dominions) {
            parentToChildrenMap
                    .computeIfAbsent(dominion.getParentDomId(), k -> new ArrayList<>())
                    .add(dominion);
        }

        // 递归构建节点树
        return buildTree(rootId, parentToChildrenMap);
    }

    private static List<DominionNode> buildTree(Integer rootId, Map<Integer, List<DominionDTO>> parentToChildrenMap) {
        List<DominionNode> dominionTree = new ArrayList<>();
        List<DominionDTO> children = parentToChildrenMap.get(rootId);

        if (children != null) {
            for (DominionDTO dominion : children) {
                DominionNode node = new DominionNode();
                node.dominion = dominion;
                node.children = buildTree(dominion.getId(), parentToChildrenMap);
                dominionTree.add(node);
            }
        }

        return dominionTree;
    }

    public static DominionNode getLocInDominionNode(@NotNull List<DominionNode> nodes, @NotNull Location loc) {
        for (DominionNode node : nodes) {
            if (isInDominion(node.dominion, loc)) {
                if (node.children.isEmpty()) {
                    return node;
                } else {
                    DominionNode childDominion = getLocInDominionNode(node.children, loc);
                    if (childDominion == null) {
                        return node;
                    } else {
                        return childDominion;
                    }
                }
            }
        }
        return null;
    }

    public static DominionDTO getLocInDominionDTO(@Nullable List<DominionNode> nodes, @NotNull Location loc) {
        if (nodes == null) return null;
        if (nodes.isEmpty()) return null;
        DominionNode dominionNode = getLocInDominionNode(nodes, loc);
        return dominionNode == null ? null : dominionNode.dominion;
    }

    public static boolean isInDominion(@Nullable DominionDTO dominion, Location location) {
        if (dominion == null) return false;
        if (!Objects.equals(dominion.getWorld(), location.getWorld().getName())) return false;
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        return x >= dominion.getX1() && x <= dominion.getX2() &&
                y >= dominion.getY1() && y <= dominion.getY2() &&
                z >= dominion.getZ1() && z <= dominion.getZ2();
    }
}
