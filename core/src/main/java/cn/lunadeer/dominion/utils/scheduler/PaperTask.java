package cn.lunadeer.dominion.utils.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class PaperTask implements CancellableTask {
    private final ScheduledTask task;

    public PaperTask(ScheduledTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}
