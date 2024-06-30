package cn.lunadeer.dominion.dtos;

import cn.lunadeer.minecraftpluginutils.databse.DatabaseManager;
import cn.lunadeer.minecraftpluginutils.databse.Field;
import cn.lunadeer.minecraftpluginutils.databse.syntax.InsertRow;
import cn.lunadeer.minecraftpluginutils.databse.syntax.UpdateRow;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDTO {

    public static PlayerDTO get(Player player) {
        PlayerDTO re = select(player.getUniqueId());
        if (re == null) {
            re = insert(new PlayerDTO(player.getUniqueId(), player.getName(), System.currentTimeMillis()));
        }
        return re;
    }

    public static @Nullable PlayerDTO get(OfflinePlayer player) {
        if (player.getName() == null) {
            return null;
        }
        PlayerDTO re = select(player.getUniqueId());
        if (re == null) {
            re = insert(new PlayerDTO(player.getUniqueId(), player.getName(), System.currentTimeMillis()));
        }
        return re;
    }

    public static List<PlayerDTO> all() {
        String sql = "SELECT * FROM player_name WHERE id > 0;";
        return query(sql);
    }

    public PlayerDTO onJoin(String name) {
        this.setLastKnownName(name);
        return update(this);
    }

    private static List<PlayerDTO> query(String sql, Object... params) {
        List<PlayerDTO> players = new ArrayList<>();
        try (ResultSet rs = DatabaseManager.instance.query(sql, params)) {
            return getDTOFromRS(rs);
        } catch (SQLException e) {
            DatabaseManager.handleDatabaseError("查询玩家信息失败: ", e, sql);
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
            PlayerDTO player = new PlayerDTO(id, uuid, lastKnownName, lastJoinAt);
            players.add(player);
        }
        return players;
    }

    public static PlayerDTO select(UUID uuid) {
        String sql = "SELECT * FROM player_name WHERE uuid = ?;";
        List<PlayerDTO> players = query(sql, uuid.toString());
        if (players.size() == 0) return null;
        return players.get(0);
    }

    public static PlayerDTO select(String name) {
        String sql = "SELECT * FROM player_name WHERE last_known_name = ?;";
        List<PlayerDTO> players = query(sql, name);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    public static List<PlayerDTO> search(String name) {
        // 模糊搜索
        String sql = "SELECT * FROM player_name WHERE last_known_name LIKE ?;";
        return query(sql, "%" + name + "%");
    }

    public static void delete(PlayerDTO player) {
        String sql = "DELETE FROM player_name WHERE uuid = ?;";
        query(sql, player.getUuid());
    }

    private static PlayerDTO insert(PlayerDTO player) {
        Field uuid = new Field("uuid", player.getUuid().toString());
        Field lastKnownName = new Field("last_known_name", player.getLastKnownName());
        Field lastJoinAt = new Field("last_join_at", Timestamp.valueOf(LocalDateTime.now()));
        InsertRow insertRow = new InsertRow()
                .table("player_name")
                .field(uuid)
                .field(lastKnownName)
                .field(lastJoinAt)
                .returningAll()
                .onConflictOverwrite(new Field("id", null));
        try (ResultSet rs = insertRow.execute()) {
            List<PlayerDTO> players = getDTOFromRS(rs);
            if (players.size() == 0) return null;
            return players.get(0);
        } catch (SQLException e) {
            DatabaseManager.handleDatabaseError("插入玩家信息失败: ", e, insertRow.toString());
            return null;
        }
    }

    private static PlayerDTO update(PlayerDTO player) {
        Field lastKnownName = new Field("last_known_name", player.getLastKnownName());
        Field uuid = new Field("uuid", player.getUuid().toString());
        Field lastJoinAt = new Field("last_join_at", Timestamp.valueOf(LocalDateTime.now()));
        UpdateRow updateRow = new UpdateRow()
                .table("player_name")
                .field(lastKnownName)
                .field(lastJoinAt)
                .where("uuid = ?", uuid.value)
                .returningAll(uuid);
        try (ResultSet rs = updateRow.execute()) {
            List<PlayerDTO> players = getDTOFromRS(rs);
            if (players.size() == 0) return null;
            return players.get(0);
        } catch (SQLException e) {
            DatabaseManager.handleDatabaseError("更新玩家信息失败: ", e, updateRow.toString());
            return null;
        }
    }

    private PlayerDTO(Integer id, UUID uuid, String lastKnownName, Long lastJoinAt) {
        this.id = id;
        this.uuid = uuid;
        this.lastKnownName = lastKnownName;
        this.lastJoinAt = lastJoinAt;
    }

    private PlayerDTO(UUID uuid, String lastKnownName, Long lastJoinAt) {
        this(null, uuid, lastKnownName, lastJoinAt);
    }

    public Integer getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLastKnownName() {
        return lastKnownName;
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

    private Integer id;
    private UUID uuid;
    private String lastKnownName;
    private Long lastJoinAt;
}
