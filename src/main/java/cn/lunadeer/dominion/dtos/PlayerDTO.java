package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Dominion;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static List<PlayerDTO> all() {
        String sql = "SELECT * FROM player_name WHERE id > 0;";
        return query(sql);
    }

    public PlayerDTO onJoin() {
        return update(this);
    }

    private static List<PlayerDTO> query(String sql) {
        List<PlayerDTO> players = new ArrayList<>();
        try (ResultSet rs = Dominion.database.query(sql)) {
            if (rs == null) return players;
            while (rs.next()) {
                Integer id = rs.getInt("id");
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String lastKnownName = rs.getString("last_known_name");
                Long lastJoinAt = rs.getTimestamp("last_join_at").getTime();
                PlayerDTO player = new PlayerDTO(id, uuid, lastKnownName, lastJoinAt);
                players.add(player);
            }
        } catch (SQLException e) {
            Dominion.database.handleDatabaseError("查询玩家信息失败: ", e, sql);
        }
        return players;
    }

    public static PlayerDTO select(UUID uuid) {
        String sql = "SELECT * FROM player_name WHERE uuid = '" + uuid.toString() + "';";
        List<PlayerDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    public static PlayerDTO select(String name) {
        String sql = "SELECT * FROM player_name WHERE last_known_name = '" + name + "';";
        List<PlayerDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    public static List<PlayerDTO> search(String name) {
        // 模糊搜索
        String sql = "SELECT * FROM player_name WHERE last_known_name LIKE '%" + name + "%';";
        return query(sql);
    }

    public static void delete(PlayerDTO player) {
        String sql = "DELETE FROM player_name WHERE uuid = '" + player.getUuid().toString() + "';";
        query(sql);
    }

    private static PlayerDTO insert(PlayerDTO player) {
        String sql = "INSERT INTO player_name (uuid, last_known_name, last_join_at) " +
                "VALUES" +
                " ('" + player.getUuid().toString() + "', '" + player.getLastKnownName() + "', CURRENT_TIMESTAMP) " +
                "RETURNING *;";
        List<PlayerDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    private static PlayerDTO update(PlayerDTO player) {
        String sql = "UPDATE player_name SET " +
                "last_known_name = '" + player.getLastKnownName() + "', " +
                "last_join_at = CURRENT_TIMESTAMP " +
                "WHERE uuid = '" + player.getUuid().toString() + "' " +
                "RETURNING *;";
        List<PlayerDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
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
