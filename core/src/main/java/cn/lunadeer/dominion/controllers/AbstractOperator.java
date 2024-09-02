package cn.lunadeer.dominion.controllers;

import cn.lunadeer.minecraftpluginutils.i18n.i18n;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AbstractOperator {

    public static class Result {
        public static final Integer SUCCESS = 0;
        public static final Integer WARNING = 1;
        public static final Integer FAILURE = 2;

        private Integer success;
        private List<String> messages;

        public Result(Integer success, i18n message, Object... args) {
            this.success = success;
            this.messages = new ArrayList<>();
            this.messages.add(message.trans());
        }

        public Result(Integer success, String message, Object... args) {
            this.success = success;
            this.messages = new ArrayList<>();
            this.messages.add(String.format(message, args));
        }

        public Result addMessage(String message, Object... args) {
            this.messages.add(String.format(message, args));
            return this;
        }

        public Result addMessage(i18n message, Object... args) {
            return addMessage(message.trans(), args);
        }

        public Integer getStatus() {
            return success;
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    public UUID getUniqueId();

    public boolean isOp();

    public void setResponse(Result result);

    public @Nullable Location getLocation();

    public @Nullable Player getPlayer();

    public @Nullable BlockFace getDirection();

    public CompletableFuture<Result> getResponse();
}
