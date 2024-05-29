package cn.lunadeer.dominion.controllers;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BukkitPlayerOperator implements AbstractOperator{

    private final org.bukkit.entity.Player player;
    private final CompletableFuture<Result> response = new CompletableFuture<>();

    public BukkitPlayerOperator(org.bukkit.entity.Player player) {
        this.player = player;
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public boolean isOp() {
        return player.isOp();
    }

    @Override
    public void setResponse(Result result) {
        response.complete(result);
    }

    @Override
    public CompletableFuture<Result> getResponse() {
        return response;
    }
}
