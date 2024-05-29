package cn.lunadeer.dominion.controllers;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AbstractOperator {

    public static class Result {
        public static final Integer SUCCESS = 0;
        public static final Integer WARNING = 1;
        public static final Integer FAILURE = 2;

        private Integer success;
        private String message;

        public Result(Integer success, String message, Object... args) {
            this.success = success;
            this.message = String.format(message, args);
        }

        public Result setMessage(String message, Object... args) {
            this.message = String.format(message, args);
            return this;
        }

        public Result appendMessage(String message, Object... args) {
            this.message += " ";
            this.message += String.format(message, args);
            return this;
        }

        public Integer getStatus() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    public UUID getUniqueId();

    public boolean isOp();

    public void setResponse(Result result);

    public @Nullable Location getLocation();

    public Player getPlayer();

    public BlockFace getDirection();

    public CompletableFuture<Result> getResponse();
}
