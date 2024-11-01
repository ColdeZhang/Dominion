package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.minecraftpluginutils.ParticleRender;
import org.bukkit.entity.Player;

public class Particle {

    public static void showBorder(Player player, DominionDTO dominion) {
        ParticleRender.showBoxFace(player,
                dominion.getLocation1().getWorld(),
                dominion.getLocation1().getBlockX(),
                dominion.getLocation1().getBlockY(),
                dominion.getLocation1().getBlockZ(),
                dominion.getLocation2().getBlockX(),
                dominion.getLocation2().getBlockY(),
                dominion.getLocation2().getBlockZ()
        );
    }

}
