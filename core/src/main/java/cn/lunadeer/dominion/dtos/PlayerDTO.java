package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.Field;
import cn.lunadeer.dominion.utils.databse.syntax.InsertRow;
import cn.lunadeer.dominion.utils.databse.syntax.UpdateRow;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDTO implements cn.lunadeer.dominion.api.dtos.PlayerDTO {

    public static List<cn.lunadeer.dominion.api.dtos.PlayerDTO> all() {
        String sql = "SELECT * FROM player_name WHERE id > 0;";
        return new ArrayList<>(query(sql));
    }

    private static List<PlayerDTO> query(String sql, Object... params) {
        List<PlayerDTO> players = new ArrayList<>();
        try (ResultSet rs = DatabaseManager.instance.query(sql, params)) {
            return getDTOFromRS(rs);
        } catch (SQLException e) {
            XLogger.error("PlayerDTO.query ", e);
        }
        return players;
    }

    private static List<PlayerDTO> getDTOFromRS(ResultSet rs) throws SQLException {
        List<PlayerDTO> players = new ArrayList<>();
        if (rs == null) return players;
        while (rs.next()) {
            Integer id = rs.getInt("id");
            UUID uuid = UUID.fromString(rs.getString("uuid"));
            String lastKnownName = rs.getString("last_known_name");
            Long lastJoinAt = rs.getTimestamp("last_join_at").getTime();
            Integer usingGroupTitleID = rs.getInt("using_group_title_id");
            PlayerDTO player = new PlayerDTO(id, uuid, lastKnownName, lastJoinAt, usingGroupTitleID);
            players.add(player);
        }
        return players;
    }

    public static PlayerDTO selectById(Integer id) {
        String sql = "SELECT * FROM player_name WHERE id = ?;";
        List<PlayerDTO> players = query(sql, id);
        if (players.isEmpty()) return null;
        return players.get(0);
    }

    public static void delete(PlayerDTO player) {
        String sql = "DELETE FROM player_name WHERE uuid = ?;";
        query(sql, player.getUuid().toString());
        CacheManager.instance.getPlayerCache().delete(player.getId());
    }

    public static PlayerDTO create(Player player) {
        return create(player.getUniqueId(), player.getName());
    }

    public static PlayerDTO create(UUID playerUid, String playerName) {
        Field uuid = new Field("uuid", playerUid.toString());
        Field lastKnownName = new Field("last_known_name", playerName);
        Field lastJoinAt = new Field("last_join_at", Timestamp.valueOf(LocalDateTime.now()));
        Field usingGroupTitleID = new Field("using_group_title_id", -1);
        InsertRow insertRow = new InsertRow()
                .table("player_name")
                .field(uuid)
                .field(lastKnownName)
                .field(lastJoinAt)
                .field(usingGroupTitleID)
                .returningAll()
                .onConflictOverwrite(new Field("id", null));
        try (ResultSet rs = insertRow.execute()) {
            List<PlayerDTO> players = getDTOFromRS(rs);
            if (players.isEmpty()) return null;
            CacheManager.instance.getPlayerCache().load(players.get(0).getId());
            return players.get(0);
        } catch (SQLException e) {
            XLogger.error("PlayerDTO.insert ", e);
            return null;
        }
    }

    private static PlayerDTO update(PlayerDTO player) {
        Field lastKnownName = new Field("last_known_name", player.getLastKnownName());
        Field uuid = new Field("uuid", player.getUuid().toString());
        Field lastJoinAt = new Field("last_join_at", Timestamp.valueOf(LocalDateTime.now()));
        Field usingGroupTitleID = new Field("using_group_title_id", player.getUsingGroupTitleID());
        UpdateRow updateRow = new UpdateRow()
                .table("player_name")
                .field(lastKnownName)
                .field(lastJoinAt)
                .field(usingGroupTitleID)
                .where("uuid = ?", uuid.value)
                .returningAll(uuid);
        try (ResultSet rs = updateRow.execute()) {
            List<PlayerDTO> players = getDTOFromRS(rs);
            if (players.isEmpty()) return null;
            CacheManager.instance.getPlayerCache().load(player.getId());
            return players.get(0);
        } catch (SQLException e) {
            XLogger.error("PlayerDTO.update ", e);
            return null;
        }
    }

    private PlayerDTO(Integer id, UUID uuid, String lastKnownName, Long lastJoinAt, Integer using_group_title_id) {
        this.id = id;
        this.uuid = uuid;
        this.lastKnownName = lastKnownName;
        this.lastJoinAt = lastJoinAt;
        this.using_group_title_id = using_group_title_id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getLastKnownName() {
        return lastKnownName;
    }

    @Override
    public cn.lunadeer.dominion.api.dtos.PlayerDTO updateLastKnownName(String name) {
        this.setLastKnownName(name);
        this.setLastJoinAt(System.currentTimeMillis());
        return update(this);
    }

    public Long getLastJoinAt() {
        return lastJoinAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }

    public void setLastJoinAt(Long lastJoinAt) {
        this.lastJoinAt = lastJoinAt;
    }

    @Override
    public Integer getUsingGroupTitleID() {
        return using_group_title_id;
    }

    public void setUsingGroupTitleID(Integer usingGroupTitleID) {
        this.using_group_title_id = usingGroupTitleID;
        update(this);
    }

    private Integer id;
    private UUID uuid;
    private String lastKnownName;
    private Long lastJoinAt;
    private Integer using_group_title_id;
}
