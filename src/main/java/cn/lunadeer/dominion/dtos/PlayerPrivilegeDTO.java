package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;

import java.sql.ResultSet;
import java.util.*;

public class PlayerPrivilegeDTO {

    public static PlayerPrivilegeDTO insert(PlayerPrivilegeDTO player) {
        String sql = "INSERT INTO player_privilege (player_uuid, admin, dom_id, ";

        for (Flag f : Flag.getAllPrivilegeFlags()) {
            sql += f.getFlagName() + ", ";
        }
        sql = sql.substring(0, sql.length() - 2);

        sql += ") VALUES ('" + player.getPlayerUUID() + "', " + player.getAdmin() + ", " + player.getDomID() + ", ";

        for (Flag f : Flag.getAllPrivilegeFlags()) {
            sql += player.getFlagValue(f) + ", ";
        }
        sql = sql.substring(0, sql.length() - 2);

        sql += ") RETURNING *;";
        List<PlayerPrivilegeDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    public static PlayerPrivilegeDTO select(UUID playerUUID, Integer dom_id) {
        String sql = "SELECT * FROM player_privilege WHERE player_uuid = ? AND dom_id = ?;";
        List<PlayerPrivilegeDTO> p = query(sql, playerUUID.toString(), dom_id);
        if (p.size() == 0) return null;
        return p.get(0);
    }

    public static List<PlayerPrivilegeDTO> select(Integer dom_id) {
        String sql = "SELECT * FROM player_privilege WHERE dom_id = ?;";
        return query(sql, dom_id);
    }

    public static void delete(UUID player, Integer domID) {
        String sql = "DELETE FROM player_privilege WHERE player_uuid = ? AND dom_id = ?;";
        query(sql, player.toString(), domID);
    }

    public static List<PlayerPrivilegeDTO> selectAll() {
        String sql = "SELECT * FROM player_privilege;";
        return query(sql);
    }

    public static List<PlayerPrivilegeDTO> selectAll(UUID player) {
        String sql = "SELECT * FROM player_privilege WHERE player_uuid = ?;";
        return query(sql, player.toString());
    }

    private final Integer id;
    private final UUID playerUUID;
    private Boolean admin;
    private final Integer domID;

    public Integer getId() {
        return id;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public Integer getDomID() {
        return domID;
    }

    private final Map<Flag, Boolean> flags = new HashMap<>();

    public Boolean getFlagValue(Flag flag) {
        if (!flags.containsKey(flag)) return flag.getDefaultValue();
        return flags.get(flag);
    }

    public PlayerPrivilegeDTO setFlagValue(Flag flag, Boolean value) {
        flags.put(flag, value);
        return update(this);
    }

    public PlayerPrivilegeDTO setAdmin(Boolean admin) {
        this.admin = admin;
        return update(this);
    }

    private PlayerPrivilegeDTO(Integer id, UUID playerUUID, Boolean admin, Integer domID, Map<Flag, Boolean> flags) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.admin = admin;
        this.domID = domID;
        this.flags.putAll(flags);
    }

    public PlayerPrivilegeDTO(UUID playerUUID, DominionDTO dom) {
        this.id = null;
        this.playerUUID = playerUUID;
        this.admin = false;
        this.domID = dom.getId();
        for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
            this.flags.put(f, dom.getFlagValue(f));
        }
    }

    private static List<PlayerPrivilegeDTO> query(String sql, Object... params) {
        List<PlayerPrivilegeDTO> players = new ArrayList<>();
        try (ResultSet rs = Dominion.database.query(sql, params)) {
            if (sql.contains("UPDATE") || sql.contains("DELETE") || sql.contains("INSERT")) {
                // 如果是更新操作，重新加载缓存
                Cache.instance.loadPlayerPrivileges();
            }
            if (rs == null) return players;
            while (rs.next()) {
                Map<Flag, Boolean> flags = new HashMap<>();
                for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
                    flags.put(f, rs.getBoolean(f.getFlagName()));
                }
                PlayerPrivilegeDTO player = new PlayerPrivilegeDTO(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getBoolean("admin"),
                        rs.getInt("dom_id"),
                        flags
                );
                players.add(player);
            }
        } catch (Exception e) {
            Dominion.database.handleDatabaseError("查询玩家权限失败: ", e, sql);
        }
        return players;
    }

    private static PlayerPrivilegeDTO update(PlayerPrivilegeDTO player) {
        String sql = "UPDATE player_privilege SET " +
                "admin = " + player.getAdmin() + ", " +
                "dom_id = " + player.getDomID() + ", ";
        for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
            sql += f.getFlagName() + " = " + player.getFlagValue(f) + ", ";
        }
        sql = sql.substring(0, sql.length() - 2);
        sql += "WHERE id = " + player.getId() + " " +
                "RETURNING *;";
        List<PlayerPrivilegeDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }
}
