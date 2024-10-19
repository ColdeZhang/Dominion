package cn.lunadeer.dominion;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.dtos.DominionDTO;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.dominion.dtos.GroupDTO;
import cn.lunadeer.dominion.dtos.MemberDTO;
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
    public @NotNull List<cn.lunadeer.dominion.api.dtos.DominionDTO> getAllDominions() {
        return Cache.instance.getAllDominions();
    }

    @Override
    public @Nullable GroupDTO getPlayerUsingGroupTitle(@NotNull UUID uuid) {
        return Cache.instance.getPlayerUsingGroupTitle(uuid);
    }

    @Override
    public @NotNull List<cn.lunadeer.dominion.api.dtos.Flag> getEnvironmentFlagsEnabled() {
        return new ArrayList<>(Flag.getEnvironmentFlagsEnabled());
    }

    @Override
    public @NotNull List<cn.lunadeer.dominion.api.dtos.Flag> getPrivilegeFlagsEnabled() {
        return new ArrayList<>(Flag.getPrivilegeFlagsEnabled());
    }

    @Override
    public cn.lunadeer.dominion.api.dtos.@Nullable Flag getFlagByName(@NotNull String flagName) {
        return Flag.getFlag(flagName);
    }

}
