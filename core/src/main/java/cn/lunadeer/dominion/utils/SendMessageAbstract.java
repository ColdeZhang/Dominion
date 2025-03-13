package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.utils.scheduler.Scheduler;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SendMessageAbstract {

    private BukkitAudiences adventure = null;
    private JavaPlugin plugin;

    public SendMessageAbstract(JavaPlugin plugin) {
        this.plugin = plugin;
        if (!Misc.isPaper()) {
            this.adventure = BukkitAudiences.create(plugin);
        }
    }

    public void sendMessage(Player player, Component msg) {
        if (this.adventure == null) {
            player.sendMessage(msg);
        } else {
            this.adventure.player(player).sendMessage(msg);
        }
    }

    public void sendMessage(CommandSender sender, Component msg) {
        if (this.adventure == null) {
            sender.sendMessage(msg);
        } else {
            this.adventure.sender(sender).sendMessage(msg);
        }
    }

    public void broadcast(Component msg) {
        if (this.adventure == null) {
            plugin.getServer().broadcast(msg);
        } else {
            adventure.all().sendMessage(msg);
        }
    }

    public void sendActionBar(Player player, Component msg) {
        if (this.adventure == null) {
            player.sendActionBar(msg);
        } else {
            this.adventure.player(player).sendActionBar(msg);
        }
    }

    public void sendTitle(Player player, Component title, Component subtitle) {
        Title titleObj = Title.title(title, subtitle);
        if (this.adventure == null) {
            player.showTitle(titleObj);
        } else {
            this.adventure.player(player).showTitle(titleObj);
        }
    }

    public void sendBossBar(Player player, Component msg) {
        BossBar bossBar = BossBar.bossBar(msg, 1.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        if (this.adventure == null) {
            player.showBossBar(bossBar);
        } else {
            this.adventure.player(player).showBossBar(bossBar);
        }
        Scheduler.runTaskLater(() -> {
            if (this.adventure == null) {
                player.hideBossBar(bossBar);
            } else {
                this.adventure.player(player).hideBossBar(bossBar);
            }
        }, 60);
    }
}
