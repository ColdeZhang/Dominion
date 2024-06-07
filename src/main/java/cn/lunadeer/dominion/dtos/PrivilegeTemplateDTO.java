package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Dominion;

import java.sql.ResultSet;
import java.util.*;

public class PrivilegeTemplateDTO {

    private static List<PrivilegeTemplateDTO> query(String sql, Object... params) {
        List<PrivilegeTemplateDTO> templates = new ArrayList<>();
        try (ResultSet rs = Dominion.database.query(sql, params)) {
            if (rs == null) return templates;
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
            Dominion.database.handleDatabaseError("查询权限模版失败: ", e, sql);
        }
        return templates;
    }

    public static PrivilegeTemplateDTO create(UUID creator, String name) {
        String sql = "INSERT INTO privilege_template (creator, name) VALUES (?, ?) RETURNING *;";
        List<PrivilegeTemplateDTO> templates = query(sql, creator.toString(), name);
        if (templates.size() == 0) return null;
        return templates.get(0);
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
        StringBuilder sql = new StringBuilder("UPDATE privilege_template SET " +
                "name = ?, " +
                "admin = ?, ");
        for (Flag f : Flag.getPrivilegeFlagsEnabled()) {
            sql.append(f.getFlagName()).append(" = ").append(template.getFlagValue(f)).append(", ");
        }
        sql = new StringBuilder(sql.substring(0, sql.length() - 2) + " WHERE id = ? RETURNING *;");
        List<PrivilegeTemplateDTO> templates = query(sql.toString(), template.getName(), template.getAdmin(), template.getId());
        if (templates.size() == 0) return null;
        return templates.get(0);
    }

}
