package cn.lunadeer.dominion.doos;

import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.utils.databse.FIelds.Field;
import cn.lunadeer.dominion.utils.databse.FIelds.FieldBoolean;
import cn.lunadeer.dominion.utils.databse.FIelds.FieldInteger;
import cn.lunadeer.dominion.utils.databse.FIelds.FieldString;
import cn.lunadeer.dominion.utils.databse.syntax.Delete;
import cn.lunadeer.dominion.utils.databse.syntax.Insert;
import cn.lunadeer.dominion.utils.databse.syntax.Select;
import cn.lunadeer.dominion.utils.databse.syntax.Update;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TemplateDOO {

    private final FieldInteger id = new FieldInteger("id");
    private final FieldString creator = new FieldString("creator");
    private final FieldString name = new FieldString("name");

    private final Map<PriFlag, Boolean> flags = new HashMap<>();

    private static Field<?>[] fields() {
        Field<?>[] fields = new Field<?>[Flags.getAllPriFlagsEnable().size() + 3];
        fields[0] = new FieldInteger("id");
        fields[1] = new FieldString("creator");
        fields[2] = new FieldString("name");
        int i = 3;
        for (PriFlag f : Flags.getAllPriFlagsEnable()) {
            fields[i] = new FieldBoolean(f.getFlagName());
            i++;
        }
        return fields;
    }

    private static TemplateDOO parse(Map<String, Field<?>> map) {
        Map<PriFlag, Boolean> flags = new HashMap<>();
        for (PriFlag f : Flags.getAllPriFlagsEnable()) {
            flags.put(f, (Boolean) map.get(f.getFlagName()).getValue());
        }
        return new TemplateDOO((Integer) map.get("id").getValue(),
                UUID.fromString((String) map.get("creator").getValue()),
                (String) map.get("name").getValue(),
                flags);
    }

    public static TemplateDOO create(UUID creator, String name) throws SQLException {
        Map<String, Field<?>> result = Insert.insert()
                .into("privilege_template")
                .values(new FieldString("creator", creator.toString()),
                        new FieldString("name", name))
                .returning(fields())
                .execute();
        if (result.isEmpty()) {
            throw new SQLException("Failed to create template.");
        }
        return parse(result);
    }

    public static TemplateDOO select(UUID creator, String name) throws SQLException {
        List<Map<String, Field<?>>> result = Select.select(fields())
                .from("privilege_template")
                .where("creator = ? AND name = ?", creator.toString(), name)
                .execute();
        if (result.isEmpty()) return null;
        return parse(result.get(0));
    }

    public static List<TemplateDOO> selectAll(UUID creator) throws SQLException {
        List<Map<String, Field<?>>> result = Select.select(fields())
                .from("privilege_template")
                .where("creator = ?", creator.toString())
                .execute();
        return result.stream().map(TemplateDOO::parse).toList();
    }

    public static void delete(UUID creator, String name) throws SQLException {
        Delete.delete().from("privilege_template")
                .where("creator = ? AND name = ?", creator.toString(), name)
                .execute();
    }

    private TemplateDOO(Integer id, UUID creator, String name, Map<PriFlag, Boolean> flags) {
        this.id.setValue(id);
        this.creator.setValue(creator.toString());
        this.name.setValue(name);
        this.flags.putAll(flags);
    }

    public Integer getId() {
        return id.getValue();
    }

    public UUID getCreator() {
        return UUID.fromString(creator.getValue());
    }

    public String getName() {
        return name.getValue();
    }

    public Boolean getFlagValue(PriFlag flag) {
        if (!flags.containsKey(flag)) return flag.getDefaultValue();
        return flags.get(flag);
    }

    public TemplateDOO setFlagValue(PriFlag flag, Boolean value) throws SQLException {
        flags.put(flag, value);
        FieldBoolean field = new FieldBoolean(flag.getFlagName(), value);
        Update.update("privilege_template")
                .set(field)
                .where("id = ?", getId())
                .execute();
        return this;
    }

}
