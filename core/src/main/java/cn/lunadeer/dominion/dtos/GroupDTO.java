package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.utils.ColorParser;
import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.Field;
import cn.lunadeer.dominion.utils.databse.FieldType;
import cn.lunadeer.dominion.utils.databse.syntax.InsertRow;
import cn.lunadeer.dominion.utils.databse.syntax.UpdateRow;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GroupDTO implements cn.lunadeer.dominion.api.dtos.GroupDTO {

    Field id = new Field("id", FieldType.INT);
    Field domID = new Field("dom_id", FieldType.INT);
    Field name_raw = new Field("name", FieldType.STRING);
    Field name_color = new Field("name_colored", FieldType.STRING);

    private final Map<PriFlag, Boolean> flags = new HashMap<>();

    @Override
    public @NotNull Integer getId() {
        return (Integer) id.value;
    }

    @Override
    public @NotNull Integer getDomID() {
        return (Integer) domID.value;
    }

    @Override
    public @NotNull String getNameRaw() {
        return (String) name_color.value;
    }

    @Override
    public @NotNull String getNamePlain() {
        return (String) name_raw.value;
    }

    @Override
    public @NotNull Component getNameColoredComponent() {
        String with_pre_suf = "&#ffffff" +
                Configuration.groupTitle.prefix +
                (String) name_color.value +
                "&#ffffff" +
                Configuration.groupTitle.suffix;
        return ColorParser.getComponentType(with_pre_suf);
    }

    @Override
    public @NotNull String getNameColoredBukkit() {
        String with_pre_suf = "&#ffffff" +
                Configuration.groupTitle.prefix +
                (String) name_color.value +
                "&#ffffff" +
                Configuration.groupTitle.suffix;
        return ColorParser.getBukkitType(with_pre_suf);
    }

    @Override
    public @NotNull Boolean getFlagValue(@NotNull PriFlag flag) {
        if (!flags.containsKey(flag)) return flag.getDefaultValue();
        return flags.get(flag);
    }

    @Override
    public @NotNull Map<PriFlag, Boolean> getFlagsValue() {
        return flags;
    }

    @Override
    public @NotNull GroupDTO setName(@NotNull String name) throws SQLException {
        this.name_color.value = name;
        this.name_raw.value = ColorParser.getPlainText(name);
        UpdateRow updateRow = new UpdateRow().field(this.name_raw).field(this.name_color);
        return doUpdate(updateRow);
    }

    @Override
    public GroupDTO setFlagValue(@NotNull PriFlag flag, @NotNull Boolean value) throws SQLException {
        flags.put(flag, value);
        Field f = new Field(flag.getFlagName(), value);
        UpdateRow updateRow = new UpdateRow().field(f);
        return doUpdate(updateRow);
    }

    @Override
    public List<MemberDTO> getMembers() {
        DominionDTO dominion = CacheManager.instance.getDominion(getDomID());
        if (dominion == null) return new ArrayList<>();
        List<MemberDTO> members = dominion.getMembers();
        List<MemberDTO> result = new ArrayList<>();
        for (MemberDTO member : members) {
            if (Objects.equals(member.getGroupId(), getId())) {
                result.add(member);
            }
        }
        return result;
    }

    public static GroupDTO create(String name, DominionDTO dominionDTO) throws SQLException {
        GroupDTO group = new GroupDTO(name, dominionDTO.getId());
        InsertRow insertRow = new InsertRow().returningAll().onConflictDoNothing(new Field("id", null));
        insertRow.table("dominion_group")
                .field(group.domID)
                .field(group.name_raw)
                .field(group.name_color);
        for (Map.Entry<PriFlag, Boolean> f : dominionDTO.getGuestPrivilegeFlagValue().entrySet()) {
            insertRow.field(new Field(f.getKey().getFlagName(), f.getValue()));
        }
        ResultSet rs = insertRow.execute();
        List<GroupDTO> groups = getDTOFromRS(rs);
        if (groups.isEmpty()) {
            throw new SQLException("Failed to create group.");
        }
        CacheManager.instance.getCache().getGroupCache().load(groups.get(0).getId());
        return groups.get(0);
    }

    public static void deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM dominion_group WHERE id = ?;";
        DatabaseManager.instance.query(sql, id);
        CacheManager.instance.getCache().getGroupCache().delete(id);
        List<cn.lunadeer.dominion.dtos.MemberDTO> players = cn.lunadeer.dominion.dtos.MemberDTO.selectByGroupId(id);
        for (cn.lunadeer.dominion.dtos.MemberDTO player : players) {
            player.setGroupId(-1);
        }
    }

    public static GroupDTO select(Integer id) throws SQLException {
        String sql = "SELECT * FROM dominion_group WHERE id = ?;";
        List<GroupDTO> groups = getDTOFromRS(DatabaseManager.instance.query(sql, id));
        if (groups.isEmpty()) return null;
        return groups.get(0);
    }

    public static List<GroupDTO> selectByDominionId(Integer domID) throws SQLException {
        String sql = "SELECT * FROM dominion_group WHERE dom_id = ?;";
        return getDTOFromRS(DatabaseManager.instance.query(sql, domID));
    }

    private GroupDTO(String name, Integer domID) {
        this.domID.value = domID;
        this.name_raw.value = ColorParser.getPlainText(name);
        this.name_color.value = name;
        for (PriFlag f : Flags.getAllPriFlagsEnable()) {
            flags.put(f, f.getDefaultValue());
        }
    }

    private GroupDTO(Integer id, Integer domID, String name, Map<PriFlag, Boolean> flags, String nameColored) {
        this.id.value = id;
        this.domID.value = domID;
        this.name_raw.value = name;
        this.flags.putAll(flags);
        this.name_color.value = nameColored;
    }

    private static List<GroupDTO> getDTOFromRS(ResultSet rs) throws SQLException {
        List<GroupDTO> list = new ArrayList<>();
        if (rs == null) return list;
        while (rs.next()) {
            Map<PriFlag, Boolean> flags = new HashMap<>();
            for (PriFlag f : Flags.getAllPriFlagsEnable()) {
                flags.put(f, rs.getBoolean(f.getFlagName()));
            }
            GroupDTO group = new GroupDTO(
                    rs.getInt("id"),
                    rs.getInt("dom_id"),
                    rs.getString("name"),
                    flags,
                    rs.getString("name_colored")
            );
            list.add(group);
        }
        return list;
    }

    private GroupDTO doUpdate(UpdateRow updateRow) throws SQLException {
        updateRow.returningAll(id)
                .table("dominion_group")
                .where("id = ?", id.value);
        ResultSet rs = updateRow.execute();
        List<GroupDTO> groups = getDTOFromRS(rs);
        if (groups.isEmpty()) {
            throw new SQLException("Failed to update group.");
        }
        CacheManager.instance.getCache().getGroupCache().load(getId());
        return groups.get(0);
    }


}
