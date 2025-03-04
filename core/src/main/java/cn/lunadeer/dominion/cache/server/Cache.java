package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.scheduler.Scheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static cn.lunadeer.dominion.cache.CacheManager.UPDATE_INTERVAL;

public abstract class Cache {

    public void update(Integer idToUpdate) {
        if (getLastTaskTimeStamp() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            Scheduler.runTaskAsync(() -> {
                resetLastTaskTimeStamp();
                updateExecution(idToUpdate);
            });
        } else {
            if (isTaskScheduled()) return;
            setTaskScheduled();
            Scheduler.runTaskLaterAsync(() -> {
                        try {
                            resetLastTaskTimeStamp();
                            loadExecution();
                        } catch (Exception e) {
                            XLogger.error(e);
                        } finally {
                            unsetTaskScheduled();
                        }
                    },
                    getTaskScheduledDelayTick());
        }
    }

    public void load() {
        if (getLastTaskTimeStamp() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            resetLastTaskTimeStamp();
            loadExecution();
        } else {
            if (isTaskScheduled()) return;
            setTaskScheduled();
            Scheduler.runTaskLaterAsync(() -> {
                        try {
                            resetLastTaskTimeStamp();
                            loadExecution();
                        } catch (Exception e) {
                            XLogger.error(e);
                        } finally {
                            unsetTaskScheduled();
                        }
                    },
                    getTaskScheduledDelayTick());
        }
    }

    public void delete(Integer idToDelete) {
        if (getLastTaskTimeStamp() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            resetLastTaskTimeStamp();
            deleteExecution(idToDelete);
        } else {
            if (isTaskScheduled()) return;
            setTaskScheduled();
            Scheduler.runTaskLaterAsync(() -> {
                        try {
                            resetLastTaskTimeStamp();
                            loadExecution();
                        } catch (Exception e) {
                            XLogger.error(e);
                        } finally {
                            unsetTaskScheduled();
                        }
                    },
                    getTaskScheduledDelayTick());
        }
    }

    public void load(Integer idToLoad) {
        if (getLastTaskTimeStamp() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            resetLastTaskTimeStamp();
            loadExecution(idToLoad);
        } else {
            if (isTaskScheduled()) return;
            setTaskScheduled();
            Scheduler.runTaskLaterAsync(() -> {
                        try {
                            resetLastTaskTimeStamp();
                            loadExecution();
                        } catch (Exception e) {
                            XLogger.error(e);
                        } finally {
                            unsetTaskScheduled();
                        }
                    },
                    getTaskScheduledDelayTick());
        }
    }

    abstract void loadExecution();

    abstract void loadExecution(Integer idToLoad);

    abstract void updateExecution(Integer idToUpdate);

    abstract void deleteExecution(Integer idToDelete);


    private final AtomicLong lastTask = new AtomicLong(0);
    private final AtomicBoolean taskScheduled = new AtomicBoolean(false);

    private Long getLastTaskTimeStamp() {
        return lastTask.get();
    }

    private void resetLastTaskTimeStamp() {
        lastTask.set(System.currentTimeMillis());
    }

    private Boolean isTaskScheduled() {
        return taskScheduled.get();
    }

    private void setTaskScheduled() {
        taskScheduled.set(true);
    }

    private void unsetTaskScheduled() {
        taskScheduled.set(false);
    }

    private long getTaskScheduledDelayTick() {
        return (UPDATE_INTERVAL - (System.currentTimeMillis() - getLastTaskTimeStamp())) / 1000 * 20L;
    }

}
