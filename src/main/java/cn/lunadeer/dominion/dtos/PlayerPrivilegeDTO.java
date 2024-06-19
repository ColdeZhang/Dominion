package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.minecraftpluginutils.databse.DatabaseManager;
import cn.lunadeer.minecraftpluginutils.databse.Field;
import cn.lunadeer.minecraftpluginutils.databse.syntax.InsertRow;
import cn.lunadeer.minecraftpluginutils.databse.syntax.UpdateRow;

import java.sql.ResultSet;
import java.util.*;

public class PlayerPrivilegeDTO {

    private static List<PlayerPrivilegeDTO> getDTOFromRS(ResultSet rs) {
        List<PlayerPrivilegeDTO> players = new ArrayList<>();
        if (rs == null) return players;
        try {
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
            DatabaseManager.handleDatabaseError("查询玩家权限失败: ", e, "");
        }
        return players;
    }

    private static PlayerPrivilegeDTO update(PlayerPrivilegeDTO player) {
        Field admin = new Field("admin", player.getAdmin());
        Field domID = new Field("dom_id", player.getDomID());
        Field id = new Field("id", player.getId());
        UpdateRow updateRow = new UpdateRow().returningAll(id)
                .table("player_privilege")
                .field(admin)
                .field(domID)
                .where("id = ?", id.value);
        for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
            updateRow.field(new Field(f.getFlagName(), player.getFlagValue(f)));
        }
        try (ResultSet rs = updateRow.execute()) {
            Cache.instance.loadPlayerPrivileges();
            List<PlayerPrivilegeDTO> players = getDTOFromRS(rs);
            if (players.size() == 0) return null;
            return players.get(0);
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("更新玩家权限失败: ", e, "");
            return null;
        }
    }

    public static PlayerPrivilegeDTO insert(PlayerPrivilegeDTO player) {
        Field playerUUID = new Field("player_uuid", player.getPlayerUUID().toString());
        Field admin = new Field("admin", player.getAdmin());
        Field domID = new Field("dom_id", player.getDomID());
        InsertRow insertRow = new InsertRow().returningAll().onConflictDoNothing(new Field("id", null))
                .table("player_privilege")
                .field(playerUUID)
                .field(admin)
                .field(domID);
        for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
            insertRow.field(new Field(f.getFlagName(), player.getFlagValue(f)));
        }
        try (ResultSet rs = insertRow.execute()) {
            Cache.instance.loadPlayerPrivileges();
            List<PlayerPrivilegeDTO> players = getDTOFromRS(rs);
            if (players.size() == 0) return null;
            return players.get(0);
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("插入玩家权限失败: ", e, "");
            return null;
        }
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

    public PlayerPrivilegeDTO applyTemplate(PrivilegeTemplateDTO template) {
        this.admin = template.getAdmin();
        for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
            this.flags.put(f, template.getFlagValue(f));
        }
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
        try (ResultSet rs = DatabaseManager.instance.query(sql, params)) {
            if (sql.contains("UPDATE") || sql.contains("DELETE") || sql.contains("INSERT")) {
                // 如果是更新操作，重新加载缓存
                Cache.instance.loadPlayerPrivileges();
            }
            return getDTOFromRS(rs);
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("查询玩家权限失败: ", e, sql);
        }
        return players;
    }

}
