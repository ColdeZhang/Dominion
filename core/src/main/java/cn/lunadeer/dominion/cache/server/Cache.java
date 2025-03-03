package cn.lunadeer.dominion.cache.server;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Cache {

    /**
     * Updates the cache with the given ID.
     *
     * @param idToUpdate the ID of the item to update in the cache
     */
    public abstract void update(Integer idToUpdate);

    /**
     * Loads the entire cache.
     */
    public abstract void load();

    /**
     * Deletes the cache entry with the given ID.
     *
     * @param idToDelete the ID of the item to delete from the cache
     */
    public abstract void delete(Integer idToDelete);

    /**
     * Loads the cache entry with the given ID.
     *
     * @param idToLoad the ID of the item to load into the cache
     */
    public abstract void load(Integer idToLoad);

    private final AtomicLong lastTask = new AtomicLong(0);
    private final AtomicBoolean taskScheduled = new AtomicBoolean(false);

    protected Long getLastTaskTimeStamp() {
        return lastTask.get();
    }

    protected void setLastTaskTimeStamp(Long timeStamp) {
        lastTask.set(timeStamp);
    }

    protected Boolean isTaskScheduled() {
        return taskScheduled.get();
    }

    protected void setTaskScheduled(Boolean isScheduled) {
        taskScheduled.set(isScheduled);
    }

}
