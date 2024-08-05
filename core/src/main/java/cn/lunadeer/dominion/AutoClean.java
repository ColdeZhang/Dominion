package cn.lunadeer.dominion;

import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.minecraftpluginutils.XLogger;

import java.util.List;

public class AutoClean {
    public static void run() {
        if (Dominion.config.getAutoCleanAfterDays() < 0) {
            return;
        }
        XLogger.info("开始自动清理长时间未登录玩家领地数据");
        int auto_clean_after_days = Dominion.config.getAutoCleanAfterDays();
        List<PlayerDTO> players = PlayerController.allPlayers();
        for (PlayerDTO p : players) {
            if (p.getLastJoinAt() + (long) auto_clean_after_days * 24 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                PlayerDTO.delete(p);
                XLogger.info("已清理玩家 %s 的领地数据", p.getLastKnownName());
            }
        }
        XLogger.info("自动清理完成");
    }
}
