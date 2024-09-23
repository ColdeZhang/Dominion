package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.api.dtos.Flag;
import cn.lunadeer.minecraftpluginutils.databse.DatabaseManager;
import cn.lunadeer.minecraftpluginutils.databse.Field;
import cn.lunadeer.minecraftpluginutils.databse.syntax.InsertRow;
import cn.lunadeer.minecraftpluginutils.databse.syntax.UpdateRow;

import java.sql.ResultSet;
import java.util.*;

public class PrivilegeTemplateDTO {

    private static List<PrivilegeTemplateDTO> query(String sql, Object... params) {
        List<PrivilegeTemplateDTO> templates = new ArrayList<>();
        try (ResultSet rs = DatabaseManager.instance.query(sql, params)) {
            return getDTOFromRS(rs);
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("PrivilegeTemplateDTO.query ", e, sql);
        }
        return templates;
    }

    private static List<PrivilegeTemplateDTO> getDTOFromRS(ResultSet rs) {
        List<PrivilegeTemplateDTO> templates = new ArrayList<>();
        if (rs == null) return templates;
        try {
            while (rs.next()) {
                Map<Flag, Boolean> flags = new HashMap<>();
                for (Flag f : cn.lunadeer.dominion.dtos.Flag.getPrivilegeFlagsEnabled()) {
                    flags.put(f, rs.getBoolean(f.getFlagName()));
                }
                PrivilegeTemplateDTO template = new PrivilegeTemplateDTO(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("creator")),
                        rs.getString("name"),
                        rs.getBoolean("admin"),
                        flags
                );
                templates.add(template);
            }
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("PrivilegeTemplateDTO.getDTOFromRS", e, null);
        }
        return templates;
    }

    public static PrivilegeTemplateDTO create(UUID creator, String name) {
        Field creatorField = new Field("creator", creator.toString());
        Field nameField = new Field("name", name);
        InsertRow insertRow = new InsertRow().table("privilege_template").onConflictDoNothing(new Field("id", null))
                .field(creatorField)
                .field(nameField)
                .returningAll();
        try (ResultSet rs = insertRow.execute()) {
            List<PrivilegeTemplateDTO> templates = getDTOFromRS(rs);
            if (templates.isEmpty()) return null;
            return templates.getFirst();
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("PrivilegeTemplateDTO.create ", e, null);
            return null;
        }
    }

    private PrivilegeTemplateDTO doUpdate(UpdateRow updateRow) {
        Field id = new Field("id", this.id);
        updateRow.returningAll(id)
                .table("privilege_template")
                .where("id = ?", id.value);
        try (ResultSet rs = updateRow.execute()) {
            List<PrivilegeTemplateDTO> templates = getDTOFromRS(rs);
            if (templates.isEmpty()) return null;
            return templates.getFirst();
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("PrivilegeTemplateDTO.doUpdate ", e, null);
            return null;
        }
    }

    public static PrivilegeTemplateDTO select(UUID creator, String name) {
        String sql = "SELECT * FROM privilege_template WHERE creator = ? AND name = ?;";
        List<PrivilegeTemplateDTO> templates = query(sql, creator.toString(), name);
        if (templates.isEmpty()) return null;
        return templates.getFirst();
    }

    public static List<PrivilegeTemplateDTO> selectAll(UUID creator) {
        String sql = "SELECT * FROM privilege_template WHERE creator = ?;";
        return query(sql, creator.toString());
    }

    public static void delete(UUID creator, String name) {
        String sql = "DELETE FROM privilege_template WHERE creator = ? AND name = ?;";
        query(sql, creator.toString(), name);
    }

    private PrivilegeTemplateDTO(Integer id, UUID creator, String name, Boolean admin, Map<Flag, Boolean> flags) {
        this.id = id;
        this.creator = creator;
        this.name = name;
        this.admin = admin;
        this.flags.putAll(flags);
    }

    private final Integer id;
    private final UUID creator;
    private final String name;
    private Boolean admin;

    private final Map<Flag, Boolean> flags = new HashMap<>();

    public Integer getId() {
        return id;
    }

    public UUID getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public Boolean getFlagValue(Flag flag) {
        if (!flags.containsKey(flag)) return flag.getDefaultValue();
        return flags.get(flag);
    }

    public PrivilegeTemplateDTO setFlagValue(Flag flag, Boolean value) {
        flags.put(flag, value);
        return doUpdate(new UpdateRow().field(new Field(flag.getFlagName(), value)));
    }

    public PrivilegeTemplateDTO setAdmin(Boolean admin) {
        this.admin = admin;
        return doUpdate(new UpdateRow().field(new Field("admin", admin)));
    }

}
