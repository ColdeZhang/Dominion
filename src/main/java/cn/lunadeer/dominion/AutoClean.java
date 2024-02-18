package cn.lunadeer.dominion;

import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.utils.XLogger;

import java.util.List;

public class AutoClean {
    public static void run() {
        if (!Dominion.config.getAutoCleanEnable()) {
            return;
        }
        XLogger.info("开始自动清理长时间未登录玩家领地数据");
        int auto_clean_after_days = Dominion.config.getAutoCleanAfterDays();
        List<PlayerDTO> players = PlayerController.allPlayers();
        for (PlayerDTO p : players) {
            if (p.getLastJoinAt() + (long) auto_clean_after_days * 24 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                PlayerDTO.delete(p);
                XLogger.info("已清理玩家 " + p.getLastKnownName() + " 的领地数据");
            }
        }
        XLogger.info("自动清理完成");
    }
}
