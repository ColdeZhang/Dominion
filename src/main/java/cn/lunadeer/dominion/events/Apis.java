package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.PlayerPrivilegeDTO;
import org.bukkit.entity.Player;

public class Apis {
    public static boolean hasPermission(Player player, DominionDTO dom) {
        if (player.isOp()) {
            return true;
        }
        if (dom == null) {
            return true;
        }
        if (dom.getOwner().equals(player.getUniqueId())) {
            return true;
        }
        PlayerPrivilegeDTO privilege = Cache.instance.getPlayerPrivilege(player, dom);
        if (privilege != null) {
            return privilege.getAdmin();
        }
        return false;
    }
}
