package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.Flag;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.databse.DatabaseManager;
import cn.lunadeer.minecraftpluginutils.databse.Field;
import cn.lunadeer.minecraftpluginutils.databse.FieldType;
import cn.lunadeer.minecraftpluginutils.databse.syntax.InsertRow;
import cn.lunadeer.minecraftpluginutils.databse.syntax.UpdateRow;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DominionDTO implements cn.lunadeer.dominion.api.dtos.DominionDTO {
    private static List<DominionDTO> query(String sql, Object... args) {
        List<DominionDTO> dominions = new ArrayList<>();
        try (ResultSet rs = DatabaseManager.instance.query(sql, args)) {
            return getDTOFromRS(rs);
        } catch (SQLException e) {
            DatabaseManager.handleDatabaseError("DominionDTO.query ", e, sql);
        }
        return dominions;
    }

    private static List<DominionDTO> getDTOFromRS(ResultSet rs) throws SQLException {
        List<DominionDTO> dominions = new ArrayList<>();
        if (rs == null) return dominions;
        while (rs.next()) {
            Integer id = rs.getInt("id");
            UUID owner = UUID.fromString(rs.getString("owner"));
            String name = rs.getString("name");
            UUID world_uid = UUID.fromString(rs.getString("world_uid"));
            Integer x1 = rs.getInt("x1");
            Integer y1 = rs.getInt("y1");
            Integer z1 = rs.getInt("z1");
            Integer x2 = rs.getInt("x2");
            Integer y2 = rs.getInt("y2");
            Integer z2 = rs.getInt("z2");
            Integer parentDomId = rs.getInt("parent_dom_id");
            String tp_location = rs.getString("tp_location");
            Map<Flag, Boolean> flags = new HashMap<>();
            for (Flag f : cn.lunadeer.dominion.dtos.Flag.getDominionFlagsEnabled()) {
                flags.put(f, rs.getBoolean(f.getFlagName()));
            }
            String color = rs.getString("color");

            DominionDTO dominion = new DominionDTO(id, owner, name, world_uid, x1, y1, z1, x2, y2, z2, parentDomId,
                    rs.getString("join_message"),
                    rs.getString("leave_message"),
                    flags,
                    tp_location,
                    color
            );
            dominions.add(dominion);
        }
        return dominions;
    }

    public static List<DominionDTO> selectAll() {
        String sql = "SELECT * FROM dominion WHERE id > 0;";
        return query(sql);
    }

    public static List<DominionDTO> search(String name) {
        String sql = "SELECT * FROM dominion WHERE name LIKE ? AND id > 0;";
        return query(sql, "%" + name + "%");
    }

    public static List<DominionDTO> selectByOwner(UUID owner) {
        String sql = "SELECT * FROM dominion WHERE owner = ? AND id > 0 ORDER BY id DESC;";
        return query(sql, owner.toString());
    }

    public static DominionDTO select(Integer id) {
        if (id == -1) {
            return new DominionDTO(-1,
                    UUID.fromString("00000000-0000-0000-0000-000000000000"),
                    "根领地", UUID.fromString("00000000-0000-0000-0000-000000000000"),
                    -2147483648, -2147483648, -2147483648,
                    2147483647, 2147483647, 2147483647, -1);
        }
        String sql = "SELECT * FROM dominion WHERE id = ? AND id > 0;";
        List<DominionDTO> dominions = query(sql, id);
        if (dominions.isEmpty()) return null;
        return dominions.get(0);
    }

    public static List<DominionDTO> selectByParentId(World world, Integer parentId) {
        return selectByParentId(world.getUID(), parentId);
    }

    public static List<DominionDTO> selectByParentId(UUID world_uid, Integer parentId) {
        String sql = "SELECT * FROM dominion WHERE world_uid = ? AND parent_dom_id = ? AND id > 0;";
        return query(sql, world_uid.toString(), parentId);
    }

    public static List<DominionDTO> selectByLocation(UUID world_uid, Integer x, Integer y, Integer z) {
        String sql = "SELECT * FROM dominion WHERE world_uid = ? AND " +
                "x1 <= ? AND x2 >= ? AND " +
                "y1 <= ? AND y2 >= ? AND " +
                "z1 <= ? AND z2 >= ? AND " + "id > 0;";
        return query(sql, world_uid.toString(), x, x, y, y, z, z);
    }

    public static DominionDTO select(String name) {
        String sql = "SELECT * FROM dominion WHERE name = ? AND id > 0;";
        List<DominionDTO> dominions = query(sql, name);
        if (dominions.isEmpty()) return null;
        return dominions.get(0);
    }

    public static DominionDTO insert(DominionDTO dominion) {
        InsertRow insert = new InsertRow().returningAll().table("dominion").onConflictDoNothing(new Field("id", null));
        insert.field(dominion.owner)
                .field(dominion.name)
                .field(dominion.world_uid)
                .field(dominion.x1).field(dominion.y1).field(dominion.z1)
                .field(dominion.x2).field(dominion.y2).field(dominion.z2)
                .field(dominion.parentDomId)
                .field(dominion.joinMessage).field(dominion.leaveMessage)
                .field(dominion.tp_location);
        for (Flag f : cn.lunadeer.dominion.dtos.Flag.getDominionFlagsEnabled()) {
            insert.field(new Field(f.getFlagName(), f.getDefaultValue()));
        }
        try (ResultSet rs = insert.execute()) {
            Cache.instance.loadDominions();
            List<DominionDTO> dominions = getDTOFromRS(rs);
            if (dominions.isEmpty()) return null;
            return dominions.get(0);
        } catch (SQLException e) {
            DatabaseManager.handleDatabaseError("DominionDTO.insert ", e, insert.toString());
            return null;
        }
    }

    public static void delete(DominionDTO dominion) {
        String sql = "DELETE FROM dominion WHERE id = ?;";
        query(sql, dominion.getId());
        Cache.instance.loadDominions();
    }

    private DominionDTO(Integer id, UUID owner, String name, UUID world_uid,
                        Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2,
                        Integer parentDomId,
                        String joinMessage, String leaveMessage,
                        Map<Flag, Boolean> flags,
                        String tp_location,
                        String color) {
        this.id.value = id;
        this.owner.value = owner.toString();
        this.name.value = name;
        this.world_uid.value = world_uid.toString();
        this.x1.value = x1;
        this.y1.value = y1;
        this.z1.value = z1;
        this.x2.value = x2;
        this.y2.value = y2;
        this.z2.value = z2;
        this.parentDomId.value = parentDomId;
        this.joinMessage.value = joinMessage;
        this.leaveMessage.value = leaveMessage;
        this.flags.putAll(flags);
        this.tp_location.value = tp_location;
        this.color.value = color;
    }


    private DominionDTO(Integer id, UUID owner, String name, UUID world_uid,
                        Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2,
                        Integer parentDomId) {
        this.id.value = id;
        this.owner.value = owner.toString();
        this.name.value = name;
        this.world_uid.value = world_uid.toString();
        this.x1.value = x1;
        this.y1.value = y1;
        this.z1.value = z1;
        this.x2.value = x2;
        this.y2.value = y2;
        this.z2.value = z2;
        this.parentDomId.value = parentDomId;
        this.joinMessage.value = Dominion.config.getDefaultJoinMessage();
        this.leaveMessage.value = Dominion.config.getDefaultLeaveMessage();
    }

    public DominionDTO(UUID owner, String name, @NotNull World world,
                       Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        this(null, owner, name, world.getUID(), x1, y1, z1, x2, y2, z2, -1);
    }

    public static DominionDTO create(UUID owner, String name, @NotNull World world,
                                     Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2, DominionDTO parent) {
        return new DominionDTO(null, owner, name, world.getUID(), x1, y1, z1, x2, y2, z2, parent == null ? -1 : parent.getId());
    }

    private final Field id = new Field("id", FieldType.INT);
    private final Field owner = new Field("owner", FieldType.STRING);
    private final Field name = new Field("name", FieldType.STRING);
    private final Field x1 = new Field("x1", FieldType.INT);
    private final Field y1 = new Field("y1", FieldType.INT);
    private final Field z1 = new Field("z1", FieldType.INT);
    private final Field x2 = new Field("x2", FieldType.INT);
    private final Field y2 = new Field("y2", FieldType.INT);
    private final Field z2 = new Field("z2", FieldType.INT);
    private final Field parentDomId = new Field("parent_dom_id", -1);
    private final Field joinMessage = new Field("join_message", "");
    private final Field leaveMessage = new Field("leave_message", "");
    private final Map<Flag, Boolean> flags = new HashMap<>();
    private final Field tp_location = new Field("tp_location", "default");
    private final Field color = new Field("color", "#00BFFF");
    private final Field world_uid = new Field("world_uid", FieldType.STRING);


    // getters and setters
    @Override
    public Integer getId() {
        return (Integer) id.value;
    }

    @Override
    public UUID getOwner() {
        return UUID.fromString((String) owner.value);
    }

    private DominionDTO doUpdate(UpdateRow updateRow) {
        updateRow.returningAll(id)
                .table("dominion")
                .where("id = ?", id.value);
        try (ResultSet rs = updateRow.execute()) {
            List<DominionDTO> dominions = getDTOFromRS(rs);
            if (dominions.isEmpty()) return null;
            Cache.instance.loadDominions((Integer) id.value);
            return dominions.get(0);
        } catch (SQLException e) {
            DatabaseManager.handleDatabaseError("DominionDTO.doUpdate ", e, updateRow.toString());
            return null;
        }
    }

    public DominionDTO setOwner(UUID owner) {
        this.owner.value = owner.toString();
        return doUpdate(new UpdateRow().field(this.owner));
    }

    @Override
    public String getName() {
        return (String) name.value;
    }

    public DominionDTO setName(String name) {
        this.name.value = name;
        return doUpdate(new UpdateRow().field(this.name));
    }

    @Override
    public @Nullable World getWorld() {
        return Dominion.instance.getServer().getWorld(getWorldUid());
    }

    @Override
    public UUID getWorldUid() {
        return UUID.fromString((String) world_uid.value);
    }

    @Override
    public Integer getX1() {
        return (Integer) x1.value;
    }

    public DominionDTO setX1(Integer x1) {
        this.x1.value = x1;
        return doUpdate(new UpdateRow().field(this.x1));
    }

    @Override
    public Integer getY1() {
        return (Integer) y1.value;
    }

    public DominionDTO setY1(Integer y1) {
        this.y1.value = y1;
        return doUpdate(new UpdateRow().field(this.y1));
    }

    @Override
    public Integer getZ1() {
        return (Integer) z1.value;
    }

    public DominionDTO setZ1(Integer z1) {
        this.z1.value = z1;
        return doUpdate(new UpdateRow().field(this.z1));
    }

    @Override
    public Integer getX2() {
        return (Integer) x2.value;
    }

    public DominionDTO setX2(Integer x2) {
        this.x2.value = x2;
        return doUpdate(new UpdateRow().field(this.x2));
    }

    @Override
    public Integer getY2() {
        return (Integer) y2.value;
    }

    public DominionDTO setY2(Integer y2) {
        this.y2.value = y2;
        return doUpdate(new UpdateRow().field(this.y2));
    }

    @Override
    public Integer getZ2() {
        return (Integer) z2.value;
    }

    public DominionDTO setZ2(Integer z2) {
        this.z2.value = z2;
        return doUpdate(new UpdateRow().field(this.z2));
    }

    @Override
    public Integer getSquare() {
        return getWidthX() * getWidthZ();
    }

    @Override
    public Integer getVolume() {
        return getSquare() * getHeight();
    }

    @Override
    public Integer getWidthX() {
        return getX2() - getX1();
    }

    @Override
    public Integer getHeight() {
        return getY2() - getY1();
    }

    @Override
    public Integer getWidthZ() {
        return getZ2() - getZ1();
    }

    @Override
    public Integer getParentDomId() {
        return (Integer) parentDomId.value;
    }

    @Override
    public String getJoinMessage() {
        return (String) joinMessage.value;
    }

    public DominionDTO setJoinMessage(String joinMessage) {
        this.joinMessage.value = joinMessage;
        return doUpdate(new UpdateRow().field(this.joinMessage));
    }

    @Override
    public String getLeaveMessage() {
        return (String) leaveMessage.value;
    }

    public DominionDTO setLeaveMessage(String leaveMessage) {
        this.leaveMessage.value = leaveMessage;
        return doUpdate(new UpdateRow().field(this.leaveMessage));
    }

    @Override
    public Boolean getFlagValue(Flag flag) {
        if (!flags.containsKey(flag)) return flag.getDefaultValue();
        return flags.get(flag);
    }

    public DominionDTO setFlagValue(Flag flag, Boolean value) {
        flags.put(flag, value);
        Field flagField = new Field(flag.getFlagName(), value);
        return doUpdate(new UpdateRow().field(flagField));
    }

    public DominionDTO setXYZ(Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        this.x1.value = x1;
        this.y1.value = y1;
        this.z1.value = z1;
        this.x2.value = x2;
        this.y2.value = y2;
        this.z2.value = z2;
        return doUpdate(new UpdateRow().field(this.x1).field(this.y1).field(this.z1).field(this.x2).field(this.y2).field(this.z2));
    }

    public DominionDTO setXYZ(int[] cords) {
        if (cords.length == 6) {
            return setXYZ(cords[0], cords[1], cords[2], cords[3], cords[4], cords[5]);
        } else {
            XLogger.warn("领地坐标数据异常: %s", (Object) cords);
            return null;
        }
    }


    @Override
    public Location getTpLocation() {
        if (Objects.equals(tp_location.value, "default")) {
            return null;
        } else {
            // 0:0:0
            String[] loc = ((String) tp_location.value).split(":");
            World w = getWorld();
            if (loc.length == 3 && w != null) {
                return new Location(w, Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
            } else {
                XLogger.warn("领地传送点数据异常: %s", tp_location);
                XLogger.debug("world: %s, loc.length: %d", getWorld(), loc.length);
                return null;
            }
        }
    }

    public DominionDTO setTpLocation(Location loc) {
        this.tp_location.value = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
        return doUpdate(new UpdateRow().field(tp_location));
    }

    @Override
    public Location getLocation1() {
        return new Location(getWorld(), getX1(), getY1(), getZ1());
    }

    @Override
    public Location getLocation2() {
        return new Location(getWorld(), getX2(), getY2(), getZ2());
    }

    public DominionDTO setColor(String color) {
        this.color.value = color;
        return doUpdate(new UpdateRow().field(this.color));
    }

    @Override
    public int getColorR() {
        return Integer.valueOf(getColor().substring(1, 3), 16);
    }

    @Override
    public int getColorG() {
        return Integer.valueOf(getColor().substring(3, 5), 16);
    }

    @Override
    public int getColorB() {
        return Integer.valueOf(getColor().substring(5, 7), 16);
    }

    @Override
    public String getColor() {
        return (String) color.value;
    }

    @Override
    public int getColorHex() {
        return (getColorR() << 16) + (getColorG() << 8) + getColorB();
    }
}
