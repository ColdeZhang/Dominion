package cn.lunadeer.dominion.utils.scheduler;

import org.bukkit.scheduler.BukkitTask;

public class SpigotTask implements CancellableTask {
    private final BukkitTask task;

    public SpigotTask(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}
