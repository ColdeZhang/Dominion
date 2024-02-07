package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.utils.Database;
import cn.lunadeer.dominion.utils.XLogger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerPrivilegeDTO {

    public static PlayerPrivilegeDTO insert(PlayerPrivilegeDTO player) {
        String sql = "INSERT INTO player_privilege (player_uuid, admin, dom_id, privilege_template_id) VALUES (" +
                "'" + player.getPlayerUUID() + "', " +
                player.getAdmin() + ", " +
                player.getDomID() + ", " +
                player.getPrivilegeTemplateID() + ")" +
                "RETURNING *;";
        List<PlayerPrivilegeDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    public static List<PlayerPrivilegeDTO> select(UUID playerUUID, Integer dom_id) {
        String sql = "SELECT * FROM player_privilege WHERE player_uuid = '" + playerUUID + "' " +
                "AND dom_id = " + dom_id + ";";
        return query(sql);
    }

    public static PlayerPrivilegeDTO select(Integer dom_id) {
        String sql = "SELECT * FROM player_privilege WHERE dom_id = " + dom_id + ";";
        List<PlayerPrivilegeDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    public static void delete(UUID player, Integer domID, Integer privilegeTemplateID) {
        String sql = "DELETE FROM player_privilege WHERE player_uuid = '" + player + "' " +
                "AND dom_id = " + domID + " " +
                "AND privilege_template_id = " + privilegeTemplateID + ";";
        query(sql);
    }

    public static void delete(UUID player) {
        String sql = "DELETE FROM player_privilege WHERE player_uuid = '" + player + "';";
        query(sql);
    }

    public static List<PlayerPrivilegeDTO> selectAll() {
        String sql = "SELECT * FROM player_privilege;";
        return query(sql);
    }

    private final Integer id;
    private final UUID playerUUID;
    private Boolean admin;
    private final Integer domID;
    private final Integer privilegeTemplateID;

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

    public Integer getPrivilegeTemplateID() {
        return privilegeTemplateID;
    }

    public PlayerPrivilegeDTO setAdmin(Boolean admin) {
        this.admin = admin;
        return update(this);
    }

    private PlayerPrivilegeDTO(Integer id, UUID playerUUID, Boolean admin, Integer domID, Integer privilegeTemplateID) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.admin = admin;
        this.domID = domID;
        this.privilegeTemplateID = privilegeTemplateID;
    }

    public PlayerPrivilegeDTO(UUID playerUUID, Boolean admin, Integer domID, Integer privilegeTemplateID) {
        this(null, playerUUID, admin, domID, privilegeTemplateID);
    }

    private static List<PlayerPrivilegeDTO> query(String sql) {
        List<PlayerPrivilegeDTO> players = new ArrayList<>();
        try (ResultSet rs = Database.query(sql)) {
            if (rs == null) return players;
            while (rs.next()) {
                Integer id = rs.getInt("id");
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                Boolean admin = rs.getBoolean("admin");
                Integer domID = rs.getInt("dom_id");
                Integer privilegeTemplateID = rs.getInt("privilege_template_id");
                PlayerPrivilegeDTO player = new PlayerPrivilegeDTO(id, uuid, admin, domID, privilegeTemplateID);
                players.add(player);
            }
        } catch (Exception e) {
            XLogger.err("Database query failed: " + e.getMessage());
            XLogger.err("SQL: " + sql);
        }
        return players;
    }

    private static PlayerPrivilegeDTO update(PlayerPrivilegeDTO player) {
        String sql = "UPDATE player_privilege SET " +
                "admin = " + player.getAdmin() + ", " +
                "dom_id = " + player.getDomID() + ", " +
                "privilege_template_id = " + player.getPrivilegeTemplateID() + " " +
                "WHERE id = " + player.getId() + ";";
        List<PlayerPrivilegeDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }
}
