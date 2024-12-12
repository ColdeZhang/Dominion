package cn.lunadeer.dominion;

import cn.lunadeer.dominion.api.AbstractOperator;
import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.controllers.BukkitPlayerOperator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DominionInterface implements DominionAPI {

    public static DominionInterface instance;

    public DominionInterface() {
        instance = this;
    }

    @Override
    public DominionDTO getPlayerCurrentDominion(@NotNull Player player) {
        return Cache.instance.getPlayerCurrentDominion(player);
    }

    @Override
    public DominionDTO getDominionByLoc(@NotNull Location loc) {
        return Cache.instance.getDominionByLoc(loc);
    }

    @Override
    public GroupDTO getGroup(@NotNull Integer id) {
        return Cache.instance.getGroup(id);
    }

    @Override
    public MemberDTO getMember(@NotNull Player player, cn.lunadeer.dominion.api.dtos.@NotNull DominionDTO dominion) {
        return Cache.instance.getMember(player.getUniqueId(), dominion);
    }

    @Override
    public MemberDTO getMember(@NotNull UUID player_uuid, cn.lunadeer.dominion.api.dtos.@NotNull DominionDTO dominion) {
        return Cache.instance.getMember(player_uuid, dominion);
    }

    @Override
    public DominionDTO getDominion(@NotNull Integer id) {
        return Cache.instance.getDominion(id);
    }

    @Override
    public DominionDTO getDominion(@NotNull String name) {
        return cn.lunadeer.dominion.dtos.DominionDTO.select(name);
    }

    @Override
    public @NotNull List<cn.lunadeer.dominion.api.dtos.DominionDTO> getAllDominions() {
        return Cache.instance.getAllDominions();
    }

    @Override
    public @Nullable GroupDTO getPlayerUsingGroupTitle(@NotNull UUID uuid) {
        return Cache.instance.getPlayerUsingGroupTitle(uuid);
    }

    @Override
    public @NotNull AbstractOperator getPlayerOperator(@NotNull Player player) {
        return BukkitPlayerOperator.create(player);
    }

    @Override
    public @NotNull AbstractOperator getPluginOperator() {
        return null;// todo
    }

    @Override
    public @Nullable PlayerDTO getPlayerDTO(UUID uuid) {
        return cn.lunadeer.dominion.dtos.PlayerDTO.select(uuid);
    }

    @Override
    public @Nullable PlayerDTO getPlayerDTO(String name) {
        return cn.lunadeer.dominion.dtos.PlayerDTO.select(name);
    }

    @Override
    public List<DominionDTO> getPlayerDominions(@NotNull UUID playerUid) {
        return new ArrayList<>(cn.lunadeer.dominion.dtos.DominionDTO.selectByOwner(playerUid));
    }

}
