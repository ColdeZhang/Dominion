package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MemberCache extends Cache {

    private ConcurrentHashMap<Integer, MemberDTO> idMembers;            // Member ID -> MemberDTO
    private ConcurrentHashMap<Integer, List<Integer>> dominionMembersMap;  // Dominion ID -> Members ID
    private ConcurrentHashMap<UUID, Map<Integer, Integer>> playerDominionMemberMap;  // Player UUID -> (Dominion ID -> Member ID)

    public @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull Player player) {
        return getMember(dominion, player.getUniqueId());
    }

    public @Nullable MemberDTO getMember(@Nullable DominionDTO dominion, @NotNull UUID player_uuid) {
        if (dominion == null) return null;
        if (!playerDominionMemberMap.containsKey(player_uuid)) return null;
        Integer member_id = playerDominionMemberMap.get(player_uuid).get(dominion.getId());
        if (member_id == null) return null;
        return idMembers.get(member_id);
    }

    @Override
    void loadExecution() {

    }

    @Override
    void loadExecution(Integer idToLoad) {

    }

    @Override
    void updateExecution(Integer idToUpdate) {

    }

    @Override
    void deleteExecution(Integer idToDelete) {

    }
}
