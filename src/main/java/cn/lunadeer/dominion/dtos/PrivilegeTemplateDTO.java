package cn.lunadeer.dominion.dtos;

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
            DatabaseManager.handleDatabaseError("查询权限模版失败: ", e, sql);
        }
        return templates;
    }

    private static List<PrivilegeTemplateDTO> getDTOFromRS(ResultSet rs) {
        List<PrivilegeTemplateDTO> templates = new ArrayList<>();
        if (rs == null) return templates;
        try {
            while (rs.next()) {
                Map<Flag, Boolean> flags = new HashMap<>();
                for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
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
            DatabaseManager.handleDatabaseError("查询权限模版失败: ", e, null);
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
            if (templates.size() == 0) return null;
            return templates.get(0);
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("创建权限模版失败: ", e, null);
            return null;
        }
    }

    public static PrivilegeTemplateDTO select(UUID creator, String name) {
        String sql = "SELECT * FROM privilege_template WHERE creator = ? AND name = ?;";
        List<PrivilegeTemplateDTO> templates = query(sql, creator.toString(), name);
        if (templates.size() == 0) return null;
        return templates.get(0);
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

    private Integer id;
    private UUID creator;
    private String name;
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
        return update(this);
    }

    public PrivilegeTemplateDTO setAdmin(Boolean admin) {
        this.admin = admin;
        return update(this);
    }

    private static PrivilegeTemplateDTO update(PrivilegeTemplateDTO template) {
        Field name = new Field("name", template.getName());
        Field admin = new Field("admin", template.getAdmin());
        Field id = new Field("id", template.getId());
        UpdateRow updateRow = new UpdateRow().table("privilege_template")
                .field(name)
                .field(admin)
                .returningAll(id)
                .where("id = ?", id.value);
        for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
            updateRow.field(new Field(f.getFlagName(), template.getFlagValue(f)));
        }
        try (ResultSet rs = updateRow.execute()) {
            List<PrivilegeTemplateDTO> templates = getDTOFromRS(rs);
            if (templates.size() == 0) return null;
            return templates.get(0);
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("更新权限模版失败: ", e, updateRow.toString());
            return null;
        }
    }

}
