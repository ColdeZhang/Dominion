package cn.lunadeer.dominion;

import cn.lunadeer.dominion.controllers.PlayerController;
import cn.lunadeer.dominion.dtos.PlayerDTO;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.XLogger;

import java.util.List;

public class AutoClean {
    public static void run() {
        if (Dominion.config.getAutoCleanAfterDays() < 0) {
            return;
        }
        XLogger.info(Translation.Messages_AutoCleanStart);
        int auto_clean_after_days = Dominion.config.getAutoCleanAfterDays();
        List<PlayerDTO> players = PlayerController.allPlayers();
        for (PlayerDTO p : players) {
            if (p.getLastJoinAt() + (long) auto_clean_after_days * 24 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                PlayerDTO.delete(p);
                XLogger.info(Translation.Messages_AutoCleanPlayer, p.getLastKnownName());
            }
        }
        XLogger.info(Translation.Messages_AutoCleanEnd);
    }
}
