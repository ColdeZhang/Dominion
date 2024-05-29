package cn.lunadeer.dominion.controllers;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AbstractOperator {

    public static class Result {
        public static final boolean SUCCESS = true;
        public static final boolean FAILURE = false;

        private boolean success;
        private String message;

        public Result(boolean success, String message, Object... args) {
            this.success = success;
            this.message = String.format(message, args);
        }

        public boolean getStatus() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    public UUID getUniqueId();

    public boolean isOp();

    public void setResponse(Result result);

    public CompletableFuture<Result> getResponse();
}
