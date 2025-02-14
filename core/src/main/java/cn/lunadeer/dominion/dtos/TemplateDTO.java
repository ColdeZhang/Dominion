package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.Field;
import cn.lunadeer.dominion.utils.databse.syntax.InsertRow;
import cn.lunadeer.dominion.utils.databse.syntax.UpdateRow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TemplateDTO {

    private static List<TemplateDTO> query(String sql, Object... params) throws SQLException {
        ResultSet rs = DatabaseManager.instance.query(sql, params);
        return getDTOFromRS(rs);
    }

    private static List<TemplateDTO> getDTOFromRS(ResultSet rs) throws SQLException {
        List<TemplateDTO> templates = new ArrayList<>();
        if (rs == null) return templates;
        while (rs.next()) {
            Map<PriFlag, Boolean> flags = new HashMap<>();
            for (PriFlag f : Flags.getAllPriFlagsEnable()) {
                flags.put(f, rs.getBoolean(f.getFlagName()));
            }
            TemplateDTO template = new TemplateDTO(
                    rs.getInt("id"),
                    UUID.fromString(rs.getString("creator")),
                    rs.getString("name"),
                    flags
            );
            templates.add(template);
        }
        return templates;
    }

    public static TemplateDTO create(UUID creator, String name) throws SQLException {
        Field creatorField = new Field("creator", creator.toString());
        Field nameField = new Field("name", name);
        InsertRow insertRow = new InsertRow().table("privilege_template").onConflictDoNothing(new Field("id", null))
                .field(creatorField)
                .field(nameField)
                .returningAll();
        ResultSet rs = insertRow.execute();
        List<TemplateDTO> templates = getDTOFromRS(rs);
        if (templates.isEmpty()) {
            throw new SQLException("Failed to create template.");
        }
        return templates.get(0);
    }

    private TemplateDTO doUpdate(UpdateRow updateRow) throws SQLException {
        Field id = new Field("id", this.id);
        updateRow.returningAll(id)
                .table("privilege_template")
                .where("id = ?", id.value);
        ResultSet rs = updateRow.execute();
        List<TemplateDTO> templates = getDTOFromRS(rs);
        if (templates.isEmpty()) {
            throw new SQLException("Failed to update template.");
        }
        return templates.get(0);
    }

    public static TemplateDTO select(UUID creator, String name) throws SQLException {
        String sql = "SELECT * FROM privilege_template WHERE creator = ? AND name = ?;";
        List<TemplateDTO> templates = query(sql, creator.toString(), name);
        if (templates.isEmpty()) return null;
        return templates.get(0);
    }

    public static List<TemplateDTO> selectAll(UUID creator) throws SQLException {
        String sql = "SELECT * FROM privilege_template WHERE creator = ?;";
        return query(sql, creator.toString());
    }

    public static void delete(UUID creator, String name) throws SQLException {
        String sql = "DELETE FROM privilege_template WHERE creator = ? AND name = ?;";
        query(sql, creator.toString(), name);
    }

    private TemplateDTO(Integer id, UUID creator, String name, Map<PriFlag, Boolean> flags) {
        this.id = id;
        this.creator = creator;
        this.name = name;
        this.flags.putAll(flags);
    }

    private final Integer id;
    private final UUID creator;
    private final String name;

    private final Map<PriFlag, Boolean> flags = new HashMap<>();

    public Integer getId() {
        return id;
    }

    public UUID getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public Boolean getFlagValue(PriFlag flag) {
        if (!flags.containsKey(flag)) return flag.getDefaultValue();
        return flags.get(flag);
    }

    public TemplateDTO setFlagValue(PriFlag flag, Boolean value) throws SQLException {
        flags.put(flag, value);
        return doUpdate(new UpdateRow().field(new Field(flag.getFlagName(), value)));
    }

}
