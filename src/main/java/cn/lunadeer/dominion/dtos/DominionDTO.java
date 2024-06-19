package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.databse.DatabaseManager;
import cn.lunadeer.minecraftpluginutils.databse.Field;
import cn.lunadeer.minecraftpluginutils.databse.syntax.InsertRow;
import cn.lunadeer.minecraftpluginutils.databse.syntax.UpdateRow;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DominionDTO {
    private static List<DominionDTO> query(String sql, Object... args) {
        List<DominionDTO> dominions = new ArrayList<>();
        try (ResultSet rs = DatabaseManager.instance.query(sql, args)) {
            if (sql.contains("UPDATE") || sql.contains("DELETE") || sql.contains("INSERT")) {
                // 如果是更新操作，重新加载缓存
                Cache.instance.loadDominions();
            }
            return getDTOFromRS(rs);
        } catch (SQLException e) {
            DatabaseManager.handleDatabaseError("数据库操作失败: ", e, sql);
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
            String world = rs.getString("world");
            Integer x1 = rs.getInt("x1");
            Integer y1 = rs.getInt("y1");
            Integer z1 = rs.getInt("z1");
            Integer x2 = rs.getInt("x2");
            Integer y2 = rs.getInt("y2");
            Integer z2 = rs.getInt("z2");
            Integer parentDomId = rs.getInt("parent_dom_id");
            String tp_location = rs.getString("tp_location");
            Map<Flag, Boolean> flags = new HashMap<>();
            for (Flag f : Flag.getDominionFlagsEnabled()) {
                flags.put(f, rs.getBoolean(f.getFlagName()));
            }
            String color = rs.getString("color");

            DominionDTO dominion = new DominionDTO(id, owner, name, world, x1, y1, z1, x2, y2, z2, parentDomId,
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

    public static List<DominionDTO> selectAll(String world) {
        String sql = "SELECT * FROM dominion WHERE world = ? AND id > 0;";
        return query(sql, world);
    }

    public static List<DominionDTO> search(String name) {
        String sql = "SELECT * FROM dominion WHERE name LIKE ? AND id > 0;";
        return query(sql, "%" + name + "%");
    }

    public static List<DominionDTO> selectAll(UUID owner) {
        String sql = "SELECT * FROM dominion WHERE owner = ? AND id > 0;";
        return query(sql, owner.toString());
    }

    public static DominionDTO select(Integer id) {
        if (id == -1) {
            return new DominionDTO(-1,
                    UUID.fromString("00000000-0000-0000-0000-000000000000"),
                    "根领地", "all",
                    -2147483648, -2147483648, -2147483648,
                    2147483647, 2147483647, 2147483647, -1);
        }
        String sql = "SELECT * FROM dominion WHERE id = ? AND id > 0;";
        List<DominionDTO> dominions = query(sql, id);
        if (dominions.size() == 0) return null;
        return dominions.get(0);
    }

    public static List<DominionDTO> selectByParentId(String world, Integer parentId) {
        String sql = "SELECT * FROM dominion WHERE world = ? AND parent_dom_id = ? AND id > 0;";
        return query(sql, world, parentId);
    }

    public static List<DominionDTO> selectByLocation(String world, Integer x, Integer y, Integer z) {
        String sql = "SELECT * FROM dominion WHERE world = ? AND " +
                "x1 <= ? AND x2 >= ? AND " +
                "y1 <= ? AND y2 >= ? AND " +
                "z1 <= ? AND z2 >= ? AND " + "id > 0;";
        return query(sql, world, x, x, y, y, z, z);
    }

    public static DominionDTO select(String name) {
        String sql = "SELECT * FROM dominion WHERE name = ? AND id > 0;";
        List<DominionDTO> dominions = query(sql, name);
        if (dominions.size() == 0) return null;
        return dominions.get(0);
    }

    public static DominionDTO insert(DominionDTO dominion) {
        Field owner = new Field("owner", dominion.getOwner().toString());
        Field name = new Field("name", dominion.getName());
        Field world = new Field("world", dominion.getWorld());
        Field x1 = new Field("x1", dominion.getX1());
        Field y1 = new Field("y1", dominion.getY1());
        Field z1 = new Field("z1", dominion.getZ1());
        Field x2 = new Field("x2", dominion.getX2());
        Field y2 = new Field("y2", dominion.getY2());
        Field z2 = new Field("z2", dominion.getZ2());
        Field parentDomId = new Field("parent_dom_id", dominion.getParentDomId());
        Field joinMessage = new Field("join_message", "欢迎来到 ${DOM_NAME}！");
        Field leaveMessage = new Field("leave_message", "你正在离开 ${DOM_NAME}，欢迎下次光临～");
        Field tpLocation = new Field("tp_location", "default");
        InsertRow insert = new InsertRow().returningAll().table("dominion").onConflictDoNothing(new Field("id", null));
        insert.field(owner).field(name).field(world).field(x1).field(y1).field(z1).field(x2).field(y2).field(z2)
                .field(parentDomId).field(joinMessage).field(leaveMessage).field(tpLocation);
        for (Flag f : Flag.getDominionFlagsEnabled()) {
            insert.field(new Field(f.getFlagName(), f.getDefaultValue()));
        }
        try (ResultSet rs = insert.execute()) {
            Cache.instance.loadDominions();
            List<DominionDTO> dominions = getDTOFromRS(rs);
            if (dominions.size() == 0) return null;
            return dominions.get(0);
        } catch (SQLException e) {
            DatabaseManager.handleDatabaseError("数据库操作失败: ", e, insert.toString());
            return null;
        }
    }

    public static void delete(DominionDTO dominion) {
        String sql = "DELETE FROM dominion WHERE id = ?;";
        query(sql, dominion.getId());
    }

    private static DominionDTO update(DominionDTO dominion) {
        String tp_location;
        if (dominion.getTpLocation() == null) {
            tp_location = "default";
        } else {
            Location loc = dominion.getTpLocation();
            tp_location = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
        }
        Field owner = new Field("owner", dominion.getOwner().toString());
        Field name = new Field("name", dominion.getName());
        Field world = new Field("world", dominion.getWorld());
        Field x1 = new Field("x1", dominion.getX1());
        Field y1 = new Field("y1", dominion.getY1());
        Field z1 = new Field("z1", dominion.getZ1());
        Field x2 = new Field("x2", dominion.getX2());
        Field y2 = new Field("y2", dominion.getY2());
        Field z2 = new Field("z2", dominion.getZ2());
        Field parentDomId = new Field("parent_dom_id", dominion.getParentDomId());
        Field joinMessage = new Field("join_message", dominion.getJoinMessage());
        Field leaveMessage = new Field("leave_message", dominion.getLeaveMessage());
        Field tpLocation = new Field("tp_location", tp_location);
        Field color = new Field("color", dominion.getColor());
        Field id = new Field("id", dominion.getId());
        UpdateRow update = new UpdateRow().returningAll(id).table("dominion").where("id = ?", id.value);
        for (Flag f : Flag.getDominionFlagsEnabled()) {
            update.field(new Field(f.getFlagName(), dominion.getFlagValue(f)));
        }
        update.field(owner).field(name).field(world).field(x1).field(y1).field(z1).field(x2).field(y2).field(z2)
                .field(parentDomId).field(joinMessage).field(leaveMessage).field(tpLocation).field(color);
        try {
            List<DominionDTO> dominions = getDTOFromRS(update.execute());
            Cache.instance.loadDominions();
            if (dominions.size() == 0) return null;
            return dominions.get(0);
        } catch (SQLException e) {
            DatabaseManager.handleDatabaseError("数据库操作失败: ", e, update.toString());
            return null;
        }
    }

    private DominionDTO(Integer id, UUID owner, String name, String world,
                        Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2,
                        Integer parentDomId,
                        String joinMessage, String leaveMessage,
                        Map<Flag, Boolean> flags,
                        String tp_location,
                        String color) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.world = world;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.parentDomId = parentDomId;
        this.joinMessage = joinMessage;
        this.leaveMessage = leaveMessage;
        this.flags.putAll(flags);
        this.tp_location = tp_location;
        this.color = color;
    }


    private DominionDTO(Integer id, UUID owner, String name, String world,
                        Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2,
                        Integer parentDomId) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.world = world;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.parentDomId = parentDomId;
    }

    public DominionDTO(UUID owner, String name, String world,
                       Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        this(null, owner, name, world, x1, y1, z1, x2, y2, z2, -1);
    }

    private Integer id;
    private UUID owner;
    private String name;
    private final String world;
    private Integer x1;
    private Integer y1;
    private Integer z1;
    private Integer x2;
    private Integer y2;
    private Integer z2;
    private Integer parentDomId = -1;
    private String joinMessage = "欢迎";
    private String leaveMessage = "再见";
    private final Map<Flag, Boolean> flags = new HashMap<>();
    private String tp_location;
    private String color;

    // getters and setters
    public Integer getId() {
        return id;
    }

    public DominionDTO setId(Integer id) {
        this.id = id;
        return update(this);
    }

    public UUID getOwner() {
        return owner;
    }

    public DominionDTO setOwner(UUID owner) {
        this.owner = owner;
        return update(this);
    }

    public String getName() {
        return name;
    }

    public DominionDTO setName(String name) {
        this.name = name;
        return update(this);
    }

    public String getWorld() {
        return world;
    }

    public Integer getX1() {
        return x1;
    }

    public DominionDTO setX1(Integer x1) {
        this.x1 = x1;
        return update(this);
    }

    public Integer getY1() {
        return y1;
    }

    public DominionDTO setY1(Integer y1) {
        this.y1 = y1;
        return update(this);
    }

    public Integer getZ1() {
        return z1;
    }

    public DominionDTO setZ1(Integer z1) {
        this.z1 = z1;
        return update(this);
    }

    public Integer getX2() {
        return x2;
    }

    public DominionDTO setX2(Integer x2) {
        this.x2 = x2;
        return update(this);
    }

    public Integer getY2() {
        return y2;
    }

    public DominionDTO setY2(Integer y2) {
        this.y2 = y2;
        return update(this);
    }

    public Integer getZ2() {
        return z2;
    }

    public DominionDTO setZ2(Integer z2) {
        this.z2 = z2;
        return update(this);
    }

    public Integer getSquare() {
        return (x2 - x1 + 1) * (z2 - z1 + 1);
    }

    public Integer getVolume() {
        return getSquare() * (y2 - y1 + 1);
    }

    public Integer getWidthX() {
        return x2 - x1 + 1;
    }

    public Integer getHeight() {
        return y2 - y1 + 1;
    }

    public Integer getWidthZ() {
        return z2 - z1 + 1;
    }

    public Integer getParentDomId() {
        return parentDomId;
    }

    public DominionDTO setParentDomId(Integer parentDomId) {
        this.parentDomId = parentDomId;
        return update(this);
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public DominionDTO setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
        return update(this);
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public DominionDTO setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
        return update(this);
    }

    public Boolean getFlagValue(Flag flag) {
        if (!flags.containsKey(flag)) return flag.getDefaultValue();
        return flags.get(flag);
    }

    public DominionDTO setFlagValue(Flag flag, Boolean value) {
        flags.put(flag, value);
        return update(this);
    }

    public DominionDTO setXYZ(Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        return update(this);
    }

    public Location getTpLocation() {
        if (Objects.equals(tp_location, "default")) {
            return null;
        } else {
            // 0:0:0
            String[] loc = tp_location.split(":");
            World w = Dominion.instance.getServer().getWorld(world);
            if (loc.length == 3 && w != null) {
                return new Location(w, Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
            } else {
                XLogger.warn("领地传送点数据异常: %s", tp_location);
                XLogger.debug("world: %s, loc.length: %d", world, loc.length);
                return null;
            }
        }
    }

    public DominionDTO setTpLocation(Location loc) {
        this.tp_location = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
        return update(this);
    }

    public Location getLocation1() {
        return new Location(Dominion.instance.getServer().getWorld(world), x1, y1, z1);
    }

    public Location getLocation2() {
        return new Location(Dominion.instance.getServer().getWorld(world), x2, y2, z2);
    }

    public DominionDTO setColor(String color) {
        this.color = color;
        return update(this);
    }

    public int getColorR() {
        return Integer.valueOf(color.substring(1, 3), 16);
    }

    public int getColorG() {
        return Integer.valueOf(color.substring(3, 5), 16);
    }

    public int getColorB() {
        return Integer.valueOf(color.substring(5, 7), 16);
    }

    public String getColor() {
        return color;
    }
}
