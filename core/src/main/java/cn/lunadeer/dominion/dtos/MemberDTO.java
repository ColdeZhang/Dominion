package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.Field;
import cn.lunadeer.dominion.utils.databse.FieldType;
import cn.lunadeer.dominion.utils.databse.syntax.InsertRow;
import cn.lunadeer.dominion.utils.databse.syntax.UpdateRow;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MemberDTO implements cn.lunadeer.dominion.api.dtos.MemberDTO {

    private static List<MemberDTO> query(String sql, Object... params) throws SQLException {
        ResultSet rs = DatabaseManager.instance.query(sql, params);
        return getDTOFromRS(rs);
    }

    private static List<MemberDTO> getDTOFromRS(ResultSet rs) throws SQLException {
        List<MemberDTO> players = new ArrayList<>();
        if (rs == null) return players;
        while (rs.next()) {
            Map<PriFlag, Boolean> flags = new HashMap<>();
            for (PriFlag f : Flags.getAllPriFlagsEnable()) {
                flags.put(f, rs.getBoolean(f.getFlagName()));
            }
            MemberDTO player = new MemberDTO(
                    rs.getInt("id"),
                    UUID.fromString(rs.getString("player_uuid")),
                    rs.getInt("dom_id"),
                    flags,
                    rs.getInt("group_id")
            );
            players.add(player);
        }
        return players;
    }

    private MemberDTO doUpdate(UpdateRow updateRow) throws SQLException {
        updateRow.returningAll(id)
                .table("dominion_member")
                .where("id = ?", id.value);
        ResultSet rs = updateRow.execute();
        List<MemberDTO> players = getDTOFromRS(rs);
        if (players.isEmpty()) {
            throw new SQLException("Update member failed");
        }
        Cache.instance.loadMembers(getPlayerUUID());
        return players.get(0);
    }

    public static MemberDTO insert(MemberDTO player) throws SQLException {
        InsertRow insertRow = new InsertRow().returningAll().onConflictDoNothing(new Field("id", null))
                .table("dominion_member")
                .field(player.playerUUID)
                .field(player.domID);
        for (PriFlag f : Flags.getAllPriFlagsEnable()) {
            insertRow.field(new Field(f.getFlagName(), player.getFlagValue(f)));
        }
        ResultSet rs = insertRow.execute();
        Cache.instance.loadMembers(player.getPlayerUUID());
        List<MemberDTO> players = getDTOFromRS(rs);
        if (players.isEmpty()) {
            throw new SQLException("Insert member failed");
        }
        return players.get(0);
    }

    public static MemberDTO select(UUID playerUUID, Integer dom_id) throws SQLException {
        String sql = "SELECT * FROM dominion_member WHERE player_uuid = ? AND dom_id = ?;";
        List<MemberDTO> p = query(sql, playerUUID.toString(), dom_id);
        if (p.isEmpty()) return null;
        return p.get(0);
    }

    public static List<MemberDTO> selectByDominionId(Integer dom_id) throws SQLException {
        String sql = "SELECT * FROM dominion_member WHERE dom_id = ?;";
        return query(sql, dom_id);
    }

    public static void delete(UUID player, Integer domID) throws SQLException {
        String sql = "DELETE FROM dominion_member WHERE player_uuid = ? AND dom_id = ?;";
        query(sql, player.toString(), domID);
        Cache.instance.loadMembers(player);
    }

    public static List<MemberDTO> selectAll() throws SQLException {
        String sql = "SELECT * FROM dominion_member;";
        return query(sql);
    }

    public static List<MemberDTO> selectAll(UUID player) throws SQLException {
        String sql = "SELECT * FROM dominion_member WHERE player_uuid = ?;";
        return query(sql, player.toString());
    }

    public static List<MemberDTO> selectByGroupId(Integer groupId) throws SQLException {
        String sql = "SELECT * FROM dominion_member WHERE group_id = ?;";
        return query(sql, groupId);
    }

    public static List<MemberDTO> selectByDomGroupId(Integer domId, Integer groupId) throws SQLException {
        String sql = "SELECT * FROM dominion_member WHERE group_id = ? AND dom_id = ?;";
        return query(sql, groupId, domId);
    }

    Field id = new Field("id", FieldType.INT);
    Field playerUUID = new Field("player_uuid", FieldType.STRING);
    Field domID = new Field("dom_id", FieldType.INT);
    Field groupId = new Field("group_id", FieldType.INT);

    @Override
    public Integer getId() {
        return (Integer) id.value;
    }

    @Override
    public UUID getPlayerUUID() {
        return UUID.fromString((String) playerUUID.value);
    }

    @Override
    public Integer getDomID() {
        return (Integer) domID.value;
    }

    @Override
    public Integer getGroupId() {
        return (Integer) groupId.value;
    }

    private final Map<PriFlag, Boolean> flags = new HashMap<>();

    @Override
    public @NotNull Boolean getFlagValue(PriFlag flag) {
        if (!flags.containsKey(flag)) return flag.getDefaultValue();
        return flags.get(flag);
    }

    @Override
    public @NotNull Map<PriFlag, Boolean> getFlagsValue() {
        return flags;
    }

    @Override
    public MemberDTO setFlagValue(@NotNull PriFlag flag, @NotNull Boolean value) throws SQLException {
        flags.put(flag, value);
        Field f = new Field(flag.getFlagName(), value);
        UpdateRow updateRow = new UpdateRow().field(f);
        return doUpdate(updateRow);
    }

    /**
     * 获取成员对象
     *
     * @return 成员对象
     */
    @Override
    public @NotNull PlayerDTO getPlayer() {
        PlayerDTO player = cn.lunadeer.dominion.dtos.PlayerDTO.select(getPlayerUUID());
        if (player == null) {
            throw new RuntimeException("Player not found");
        }
        return player;
    }

    public MemberDTO setGroupId(Integer groupId) throws SQLException {
        this.groupId.value = groupId;
        UpdateRow updateRow = new UpdateRow().field(this.groupId);
        return doUpdate(updateRow);
    }

    public MemberDTO applyTemplate(TemplateDTO template) throws SQLException {
        UpdateRow updateRow = new UpdateRow();
        for (PriFlag f : Flags.getAllPriFlagsEnable()) {
            this.flags.put(f, template.getFlagValue(f));
            updateRow.field(new Field(f.getFlagName(), template.getFlagValue(f)));
        }
        return doUpdate(updateRow);
    }

    private MemberDTO(Integer id, UUID playerUUID, Integer domID, Map<PriFlag, Boolean> flags, Integer groupId) {
        this.id.value = id;
        this.playerUUID.value = playerUUID.toString();
        this.domID.value = domID;
        this.groupId.value = groupId;
        this.flags.putAll(flags);
    }

    public MemberDTO(UUID playerUUID, DominionDTO dom) {
        this.id.value = null;
        this.playerUUID.value = playerUUID.toString();
        this.domID.value = dom.getId();
        this.flags.putAll(dom.getGuestPrivilegeFlagValue());
    }

}
