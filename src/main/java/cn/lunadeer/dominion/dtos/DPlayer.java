package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.utils.Database;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DPlayer {

    public static DPlayer get(Player player) {
        DPlayer re = select(player.getUniqueId());
        if (re == null) {
            re = insert(new DPlayer(player.getUniqueId(), player.getName(), System.currentTimeMillis()));
        }
        return re;
    }

    public DPlayer join() {
        return update(this);
    }

    public static List<DPlayer> search(String name) {
        return select(name);
    }

    private static List<DPlayer> query(String sql) {
        List<DPlayer> players = new ArrayList<>();
        try (ResultSet rs = Database.query(sql)) {
            if (rs == null) return players;
            while (rs.next()) {
                Integer id = rs.getInt("id");
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String lastKnownName = rs.getString("last_known_name");
                Long lastJoinAt = rs.getTimestamp("last_join_at").getTime();
                DPlayer player = new DPlayer(id, uuid, lastKnownName, lastJoinAt);
                players.add(player);
            }
        } catch (SQLException e) {
            XLogger.err("Database query failed: " + e.getMessage());
            XLogger.err("SQL: " + sql);
        }
        return players;
    }

    private static DPlayer select(UUID uuid) {
        String sql = "SELECT * FROM player_name WHERE uuid = '" + uuid.toString() + "'";
        List<DPlayer> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    private static List<DPlayer> select(String name) {
        // 模糊搜索
        String sql = "SELECT * FROM player_name WHERE last_known_name LIKE '%" + name + "%'";
        return query(sql);
    }

    private static DPlayer insert(DPlayer player) {
        String sql = "INSERT INTO player_name (uuid, last_known_name, last_join_at) " +
                "VALUES" +
                " ('" + player.getUuid().toString() + "', '" + player.getLastKnownName() + "', CURRENT_TIMESTAMP) " +
                "RETURNING *";
        List<DPlayer> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    private static DPlayer update(DPlayer player) {
        String sql = "UPDATE player_name SET " +
                "last_known_name = '" + player.getLastKnownName() + "', " +
                "last_join_at = CURRENT_TIMESTAMP " +
                "WHERE uuid = '" + player.getUuid().toString() + "' " +
                "RETURNING *";
        List<DPlayer> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    private DPlayer(Integer id, UUID uuid, String lastKnownName, Long lastJoinAt) {
        this.id = id;
        this.uuid = uuid;
        this.lastKnownName = lastKnownName;
        this.lastJoinAt = lastJoinAt;
    }

    private DPlayer(UUID uuid, String lastKnownName, Long lastJoinAt) {
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
