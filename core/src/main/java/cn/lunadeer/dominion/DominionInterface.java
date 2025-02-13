package cn.lunadeer.dominion;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.misc.Others;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class DominionInterface extends DominionAPI {

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
    public @Nullable List<GroupDTO> getGroups(@NotNull DominionDTO dominion) {
        return Cache.instance.getGroups(dominion.getId());
    }

    @Override
    public MemberDTO getMember(@NotNull Player player, @NotNull DominionDTO dominion) {
        return Cache.instance.getMember(player.getUniqueId(), dominion);
    }

    @Override
    public MemberDTO getMember(@NotNull UUID player_uuid, @NotNull DominionDTO dominion) {
        return Cache.instance.getMember(player_uuid, dominion);
    }

    @Override
    public @Nullable List<MemberDTO> getMembers(@NotNull DominionDTO dominion) {
        return Cache.instance.getMembers(dominion.getId());
    }

    @Override
    public DominionDTO getDominion(@NotNull Integer id) {
        return Cache.instance.getDominion(id);
    }

    @Override
    public DominionDTO getDominion(@NotNull String name) {
        return Cache.instance.getDominion(name);
    }

    @Override
    public @NotNull List<DominionDTO> getAllDominions() {
        return Cache.instance.getAllDominions();
    }

    @Override
    public @Nullable GroupDTO getPlayerUsingGroupTitle(@NotNull UUID uuid) {
        return Cache.instance.getPlayerUsingGroupTitle(uuid);
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
    public List<DominionDTO> getDominionsOf(@NotNull UUID playerUid) {
        return Cache.instance.getPlayerDominions(playerUid);
    }

    @Override
    public List<DominionDTO> getChildrenDominionsOf(@NotNull DominionDTO parent) {
        return Cache.instance.getDominionsByParentId(parent.getId());
    }

    @Override
    public boolean checkPrivilegeFlag(DominionDTO dom, PriFlag flag, Player player) {
        return Others.checkPrivilegeFlag(dom, flag, player, null);
    }

    @Override
    public boolean checkEnvironmentFlag(@Nullable DominionDTO dom, @NotNull EnvFlag flag) {
        return Others.checkEnvironmentFlag(dom, flag, null);
    }

}
