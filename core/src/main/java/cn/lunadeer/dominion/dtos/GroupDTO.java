package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.minecraftpluginutils.databse.DatabaseManager;
import cn.lunadeer.minecraftpluginutils.databse.Field;
import cn.lunadeer.minecraftpluginutils.databse.FieldType;
import cn.lunadeer.minecraftpluginutils.databse.syntax.InsertRow;
import cn.lunadeer.minecraftpluginutils.databse.syntax.UpdateRow;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDTO {

    Field id = new Field("id", FieldType.INT);
    Field domID = new Field("dom_id", FieldType.INT);
    Field name = new Field("name", FieldType.STRING);
    Field admin = new Field("admin", FieldType.BOOLEAN);
    private final Map<Flag, Boolean> flags = new HashMap<>();

    public Integer getId() {
        return (Integer) id.value;
    }

    public Integer getDomID() {
        return (Integer) domID.value;
    }

    public String getName() {
        return (String) name.value;
    }

    public Boolean getAdmin() {
        return (Boolean) admin.value;
    }

    public Boolean getFlagValue(Flag flag) {
        if (!flags.containsKey(flag)) return flag.getDefaultValue();
        return flags.get(flag);
    }

    public GroupDTO setName(String name) {
        this.name.value = name;
        UpdateRow updateRow = new UpdateRow().field(this.name);
        return doUpdate(updateRow);
    }

    public GroupDTO setAdmin(Boolean admin) {
        this.admin.value = admin;
        UpdateRow updateRow = new UpdateRow().field(this.admin);
        return doUpdate(updateRow);
    }

    public GroupDTO setFlagValue(Flag flag, Boolean value) {
        flags.put(flag, value);
        Field f = new Field(flag.getFlagName(), value);
        UpdateRow updateRow = new UpdateRow().field(f);
        return doUpdate(updateRow);
    }

    public static GroupDTO create(String name, DominionDTO dominionDTO) {
        GroupDTO group = new GroupDTO(name, dominionDTO.getId());
        InsertRow insertRow = new InsertRow().returningAll().onConflictDoNothing(new Field("id", null));
        insertRow.table("dominion_group")
                .field(group.domID)
                .field(group.name)
                .field(group.admin);
        for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
            insertRow.field(new Field(f.getFlagName(), dominionDTO.getFlagValue(f)));
        }
        try (ResultSet rs = insertRow.execute()) {
            List<GroupDTO> groups = getDTOFromRS(rs);
            if (groups.size() == 0) return null;
            Cache.instance.loadGroups(groups.get(0).getId());
            return groups.get(0);
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("创建权限组失败: ", e, "");
            return null;
        }
    }

    public void delete() {
        delete(getId());
    }

    public static void delete(Integer id) {
        String sql = "DELETE FROM dominion_group WHERE id = ?;";
        DatabaseManager.instance.query(sql, id);
        Cache.instance.loadGroups(id);
        List<MemberDTO> players = MemberDTO.selectByGroupId(id);
        for (MemberDTO player : players) {
            player.setGroupId(-1);
        }
    }

    public static GroupDTO select(Integer id) {
        String sql = "SELECT * FROM dominion_group WHERE id = ?;";
        List<GroupDTO> groups = getDTOFromRS(DatabaseManager.instance.query(sql, id));
        if (groups.size() == 0) return null;
        return groups.get(0);
    }

    public static GroupDTO select(Integer domID, String name) {
        String sql = "SELECT * FROM dominion_group WHERE dom_id = ? AND name = ?;";
        List<GroupDTO> groups = getDTOFromRS(DatabaseManager.instance.query(sql, domID, name));
        if (groups.size() == 0) return null;
        return groups.get(0);
    }

    public static List<GroupDTO> selectAll() {
        String sql = "SELECT * FROM dominion_group;";
        return getDTOFromRS(DatabaseManager.instance.query(sql));
    }

    public static List<GroupDTO> selectByDominionId(Integer domID) {
        String sql = "SELECT * FROM dominion_group WHERE dom_id = ?;";
        return getDTOFromRS(DatabaseManager.instance.query(sql, domID));
    }

    private GroupDTO(String name, Integer domID) {
        this.domID.value = domID;
        this.name.value = name;
        this.admin.value = false;
        for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
            flags.put(f, f.getDefaultValue());
        }
    }

    private GroupDTO(Integer id, Integer domID, String name, Boolean admin, Map<Flag, Boolean> flags) {
        this.id.value = id;
        this.domID.value = domID;
        this.name.value = name;
        this.admin.value = admin;
        this.flags.putAll(flags);
    }

    private static List<GroupDTO> getDTOFromRS(ResultSet rs) {
        List<GroupDTO> list = new ArrayList<>();
        if (rs == null) return list;
        try {
            while (rs.next()) {
                Map<Flag, Boolean> flags = new HashMap<>();
                for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
                    flags.put(f, rs.getBoolean(f.getFlagName()));
                }
                GroupDTO group = new GroupDTO(
                        rs.getInt("id"),
                        rs.getInt("dom_id"),
                        rs.getString("name"),
                        rs.getBoolean("admin"),
                        flags
                );
                list.add(group);
            }
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("查询权限组失败: ", e, "");
        }
        return list;
    }

    private GroupDTO doUpdate(UpdateRow updateRow) {
        updateRow.returningAll(id)
                .table("dominion_group")
                .where("id = ?", id.value);
        try (ResultSet rs = updateRow.execute()) {
            List<GroupDTO> groups = getDTOFromRS(rs);
            if (groups.size() == 0) return null;
            Cache.instance.loadGroups((Integer) id.value);
            return groups.get(0);
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("更新权限组失败: ", e, "");
            return null;
        }
    }


}
