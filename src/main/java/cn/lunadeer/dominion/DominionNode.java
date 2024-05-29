package cn.lunadeer.dominion;

import cn.lunadeer.dominion.dtos.DominionDTO;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DominionNode {
    public DominionDTO dominion;
    public List<DominionNode> children = new ArrayList<>();

    public static List<DominionNode> BuildNodeTree(Integer rootId, List<DominionDTO> dominions) {
        List<DominionNode> dominionTree = new ArrayList<>();
        for (DominionDTO dominion : dominions) {
            if (Objects.equals(dominion.getParentDomId(), rootId)) {
                DominionNode node = new DominionNode();
                node.dominion = dominion;
                node.children = BuildNodeTree(dominion.getId(), dominions);
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

    public static DominionDTO getLocInDominionDTO(@NotNull List<DominionNode> nodes, @NotNull Location loc) {
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
