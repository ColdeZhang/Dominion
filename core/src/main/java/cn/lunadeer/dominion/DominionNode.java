package cn.lunadeer.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class DominionNode {
    private Integer dominion_id;
    private List<DominionNode> children = new ArrayList<>();

    public DominionDTO getDominion() {
        return Cache.instance.getDominion(dominion_id);
    }

    public List<DominionNode> getChildren() {
        return children;
    }

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
                node.dominion_id = dominion.getId();
                node.children = buildTree(dominion.getId(), parentToChildrenMap);
                dominionTree.add(node);
            }
        }

        return dominionTree;
    }

    public static DominionNode getLocInDominionNode(@NotNull List<DominionNode> nodes, @NotNull Location loc) {
        for (DominionNode node : nodes) {
            if (isInDominion(node.getDominion(), loc)) {
                if (node.children.isEmpty()) {
                    return node;
                } else {
                    DominionNode childDominion = getLocInDominionNode(node.children, loc);
                    return Objects.requireNonNullElse(childDominion, node);
                }
            }
        }
        return null;
    }

    public static boolean isInDominion(@Nullable DominionDTO dominion, @NotNull Location location) {
        if (dominion == null) return false;
        if (!Objects.equals(dominion.getWorldUid(), location.getWorld().getUID())) return false;
        return dominion.getCuboid().contain(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
