package cn.lunadeer.dominion;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.cache.CacheManager;
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
        return CacheManager.instance.getPlayerCurrentDominion(player);
    }

    @Override
    public DominionDTO getDominionByLoc(@NotNull Location loc) {
        return CacheManager.instance.getDominion(loc);
    }

    @Override
    public GroupDTO getGroup(@NotNull Integer id) {
        return CacheManager.instance.getGroup(id);
    }

    @Override
    public MemberDTO getMember(@NotNull Player player, @NotNull DominionDTO dominion) {
        return getMember(player.getUniqueId(), dominion);
    }

    @Override
    public MemberDTO getMember(@NotNull UUID player_uuid, @NotNull DominionDTO dominion) {
        return CacheManager.instance.getMember(dominion, player_uuid);
    }

    @Override
    public DominionDTO getDominion(@NotNull Integer id) {
        return CacheManager.instance.getDominion(id);
    }

    @Override
    public DominionDTO getDominion(@NotNull String name) {
        return CacheManager.instance.getDominion(name);
    }

    @Override
    public @NotNull List<DominionDTO> getAllDominions() {
        return CacheManager.instance.getAllDominions();
    }

    @Override
    public @Nullable GroupDTO getPlayerUsingGroupTitle(@NotNull UUID uuid) {
        Integer usingId = CacheManager.instance.getPlayerCache().getPlayerUsingTitleId(uuid);
        return CacheManager.instance.getGroup(usingId);
    }

    @Override
    public @Nullable PlayerDTO getPlayerDTO(UUID uuid) {
        return CacheManager.instance.getPlayer(uuid);
    }

    @Override
    public @Nullable PlayerDTO getPlayerDTO(String name) {
        return CacheManager.instance.getPlayer(name);
    }

    @Override
    public List<DominionDTO> getDominionsOf(@NotNull UUID playerUid) {
        return CacheManager.instance.getPlayerAdminDominionDTOs(playerUid);
    }

    @Override
    public List<DominionDTO> getChildrenDominionsOf(@NotNull DominionDTO parent) {
        return CacheManager.instance.getChildrenDominionOf(parent);
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
