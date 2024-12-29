package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PreFlag;
import cn.lunadeer.minecraftpluginutils.ColorParser;
import cn.lunadeer.minecraftpluginutils.databse.DatabaseManager;
import cn.lunadeer.minecraftpluginutils.databse.Field;
import cn.lunadeer.minecraftpluginutils.databse.FieldType;
import cn.lunadeer.minecraftpluginutils.databse.syntax.InsertRow;
import cn.lunadeer.minecraftpluginutils.databse.syntax.UpdateRow;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDTO implements cn.lunadeer.dominion.api.dtos.GroupDTO {

    Field id = new Field("id", FieldType.INT);
    Field domID = new Field("dom_id", FieldType.INT);
    Field name_raw = new Field("name", FieldType.STRING);
    Field name_color = new Field("name_colored", FieldType.STRING);

    private final Map<PreFlag, Boolean> flags = new HashMap<>();

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
                Dominion.config.getGroupTitlePrefix() +
                (String) name_color.value +
                "&#ffffff" +
                Dominion.config.getGroupTitleSuffix();
        return ColorParser.getComponentType(with_pre_suf);
    }

    @Override
    public @NotNull String getNameColoredBukkit() {
        String with_pre_suf = "&#ffffff" +
                Dominion.config.getGroupTitlePrefix() +
                (String) name_color.value +
                "&#ffffff" +
                Dominion.config.getGroupTitleSuffix();
        return ColorParser.getBukkitType(with_pre_suf);
    }

    @Override
    public @NotNull Boolean getAdmin() {
        return getFlagValue(Flags.ADMIN);
    }

    @Override
    public @NotNull Boolean getFlagValue(@NotNull PreFlag flag) {
        if (!flags.containsKey(flag)) return flag.getDefaultValue();
        return flags.get(flag);
    }

    @Override
    public @NotNull Map<PreFlag, Boolean> getFlagsValue() {
        return flags;
    }

    @Override
    public @Nullable GroupDTO setName(@NotNull String name) {
        this.name_color.value = name;
        this.name_raw.value = ColorParser.getPlainText(name);
        UpdateRow updateRow = new UpdateRow().field(this.name_raw).field(this.name_color);
        return doUpdate(updateRow);
    }

    @Override
    public @Nullable GroupDTO setAdmin(@NotNull Boolean admin) {
        return setFlagValue(Flags.ADMIN, admin);
    }

    @Override
    public GroupDTO setFlagValue(@NotNull PreFlag flag, @NotNull Boolean value) {
        flags.put(flag, value);
        Field f = new Field(flag.getFlagName(), value);
        UpdateRow updateRow = new UpdateRow().field(f);
        return doUpdate(updateRow);
    }

    @Override
    public List<MemberDTO> getMembers() {
        return new ArrayList<>(cn.lunadeer.dominion.dtos.MemberDTO.selectByDomGroupId(getDomID(), getId()));
    }

    public static GroupDTO create(String name, DominionDTO dominionDTO) {
        GroupDTO group = new GroupDTO(name, dominionDTO.getId());
        InsertRow insertRow = new InsertRow().returningAll().onConflictDoNothing(new Field("id", null));
        insertRow.table("dominion_group")
                .field(group.domID)
                .field(group.name_raw)
                .field(group.name_color);
        for (Map.Entry<PreFlag, Boolean> f : dominionDTO.getGuestPrivilegeFlagValue().entrySet()) {
            insertRow.field(new Field(f.getKey().getFlagName(), f.getValue()));
        }
        try (ResultSet rs = insertRow.execute()) {
            List<GroupDTO> groups = getDTOFromRS(rs);
            if (groups.isEmpty()) return null;
            Cache.instance.loadGroups(groups.get(0).getId());
            return groups.get(0);
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("GroupDTO.create ", e, "");
            return null;
        }
    }

    public static void deleteById(Integer id) {
        String sql = "DELETE FROM dominion_group WHERE id = ?;";
        DatabaseManager.instance.query(sql, id);
        Cache.instance.loadGroups(id);
        List<cn.lunadeer.dominion.dtos.MemberDTO> players = cn.lunadeer.dominion.dtos.MemberDTO.selectByGroupId(id);
        for (cn.lunadeer.dominion.dtos.MemberDTO player : players) {
            player.setGroupId(-1);
        }
    }

    public static GroupDTO select(Integer id) {
        String sql = "SELECT * FROM dominion_group WHERE id = ?;";
        List<GroupDTO> groups = getDTOFromRS(DatabaseManager.instance.query(sql, id));
        if (groups.isEmpty()) return null;
        return groups.get(0);
    }

    public static GroupDTO select(Integer domID, String name) {
        String sql = "SELECT * FROM dominion_group WHERE dom_id = ? AND name = ?;";
        List<GroupDTO> groups = getDTOFromRS(DatabaseManager.instance.query(sql, domID, name));
        if (groups.isEmpty()) return null;
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
        this.name_raw.value = ColorParser.getPlainText(name);
        this.name_color.value = name;
        for (PreFlag f : Flags.getAllPreFlagsEnable()) {
            flags.put(f, f.getDefaultValue());
        }
    }

    private GroupDTO(Integer id, Integer domID, String name, Map<PreFlag, Boolean> flags, String nameColored) {
        this.id.value = id;
        this.domID.value = domID;
        this.name_raw.value = name;
        this.flags.putAll(flags);
        this.name_color.value = nameColored;
    }

    private static List<GroupDTO> getDTOFromRS(ResultSet rs) {
        List<GroupDTO> list = new ArrayList<>();
        if (rs == null) return list;
        try {
            while (rs.next()) {
                Map<PreFlag, Boolean> flags = new HashMap<>();
                for (PreFlag f : Flags.getAllPreFlagsEnable()) {
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
            if (groups.isEmpty()) return null;
            Cache.instance.loadGroups((Integer) id.value);
            return groups.get(0);
        } catch (Exception e) {
            DatabaseManager.handleDatabaseError("更新权限组失败: ", e, "");
            return null;
        }
    }


}
