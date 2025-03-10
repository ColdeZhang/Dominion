package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.utils.databse.DatabaseManager;
import cn.lunadeer.dominion.utils.databse.Field;
import cn.lunadeer.dominion.utils.databse.FieldType;
import cn.lunadeer.dominion.utils.databse.syntax.InsertRow;
import cn.lunadeer.dominion.utils.databse.syntax.UpdateRow;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DominionDTO implements cn.lunadeer.dominion.api.dtos.DominionDTO {
    private static List<DominionDTO> query(String sql, Object... args) throws SQLException {
        ResultSet rs = DatabaseManager.instance.query(sql, args);
        if (rs == null) return new ArrayList<>();
        return getDTOFromRS(rs);
    }

    private static List<DominionDTO> getDTOFromRS(ResultSet rs) throws SQLException {
        List<DominionDTO> dominions = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            UUID owner = UUID.fromString(rs.getString("owner"));
            String name = rs.getString("name");
            UUID world_uid = UUID.fromString(rs.getString("world_uid"));
            Integer parentDomId = rs.getInt("parent_dom_id");
            String tp_location = rs.getString("tp_location");
            Map<EnvFlag, Boolean> envFlags = new HashMap<>();
            for (EnvFlag f : Flags.getAllEnvFlagsEnable()) {
                envFlags.put(f, rs.getBoolean(f.getFlagName()));
            }
            Map<PriFlag, Boolean> preFlags = new HashMap<>();
            for (PriFlag f : Flags.getAllPriFlagsEnable()) {
                if (f.equals(Flags.ADMIN)) {
                    continue;
                }
                preFlags.put(f, rs.getBoolean(f.getFlagName()));
            }
            String color = rs.getString("color");
            Integer serverId = rs.getInt("server_id");
            DominionDTO dominion = new DominionDTO(id, owner, name, world_uid, new DominionCuboid(rs), parentDomId,
                    rs.getString("join_message"),
                    rs.getString("leave_message"),
                    envFlags,
                    preFlags,
                    tp_location,
                    color,
                    serverId
            );
            dominions.add(dominion);
        }
        return dominions;
    }

    public static List<DominionDTO> selectAll(Integer serverId) throws SQLException {
        String sql = "SELECT * FROM dominion WHERE id > 0 AND server_id = ?;";
        return query(sql, serverId);
    }

    public static DominionDTO rootDominion() {
        return new DominionDTO(-1,
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                "根领地", UUID.fromString("00000000-0000-0000-0000-000000000000"),
                new DominionCuboid(-2147483648, -2147483648, -2147483648, 2147483647, 2147483647, 2147483647),
                -1,
                "null", "null",
                new HashMap<>(), new HashMap<>(),
                "default", "#00BFFF", -1);
    }

    public static @Nullable DominionDTO select(Integer id) throws SQLException {
        if (id == -1) {
            return rootDominion();
        }
        String sql = "SELECT * FROM dominion WHERE id = ? AND id > 0;";
        List<DominionDTO> dominions = query(sql, id);
        if (dominions.isEmpty()) return null;
        return dominions.get(0);
    }

    public static @Nullable DominionDTO select(String name) throws SQLException {
        String sql = "SELECT * FROM dominion WHERE name = ? AND id > 0;";
        List<DominionDTO> dominions = query(sql, name);
        if (dominions.isEmpty()) return null;
        return dominions.get(0);
    }

    public static @NotNull DominionDTO insert(DominionDTO dominion) throws SQLException {
        InsertRow insert = new InsertRow().returningAll().table("dominion").onConflictDoNothing(new Field("id", null));
        insert.field(dominion.owner)
                .field(dominion.name)
                .field(dominion.world_uid)
                .field(dominion.cuboid.x1Field()).field(dominion.cuboid.y1Field()).field(dominion.cuboid.z1Field())
                .field(dominion.cuboid.x2Field()).field(dominion.cuboid.y2Field()).field(dominion.cuboid.z2Field())
                .field(dominion.parentDomId)
                .field(dominion.joinMessage).field(dominion.leaveMessage)
                .field(dominion.tp_location).field(dominion.serverId);
        for (Flag f : Flags.getAllFlagsEnable()) {
            insert.field(new Field(f.getFlagName(), f.getDefaultValue()));
        }
        ResultSet rs = insert.execute();
        List<DominionDTO> dominions = getDTOFromRS(rs);
        if (dominions.isEmpty()) {
            throw new SQLException("Failed to insert dominion.");
        }
        CacheManager.instance.getCache().getDominionCache().load(dominions.get(0).getId());
        return dominions.get(0);
    }

    public static void deleteById(Integer dominion) throws SQLException {
        String sql = "DELETE FROM dominion WHERE id = ?;";
        query(sql, dominion);
        CacheManager.instance.getCache().getDominionCache().delete(dominion);
    }

    // full constructor
    private DominionDTO(Integer id, UUID owner, String name, UUID world_uid,
                        DominionCuboid cuboid,
                        Integer parentDomId,
                        String joinMessage, String leaveMessage,
                        Map<EnvFlag, Boolean> envFlags,
                        Map<PriFlag, Boolean> preFlags,
                        String tp_location,
                        String color,
                        Integer serverId) {
        this.id.value = id;
        this.owner.value = owner.toString();
        this.name.value = name;
        this.world_uid.value = world_uid.toString();
        this.cuboid = cuboid;
        this.parentDomId.value = parentDomId;
        this.joinMessage.value = joinMessage;
        this.leaveMessage.value = leaveMessage;
        this.envFlags.putAll(envFlags);
        this.preFlags.putAll(preFlags);
        this.tp_location.value = tp_location;
        this.color.value = color;
        this.serverId.value = serverId;
    }

    // constructor for new dominion
    public DominionDTO(@NotNull UUID owner,
                       @NotNull String name,
                       @NotNull UUID world_uid,
                       @NotNull CuboidDTO cuboid,
                       @NotNull Integer parentDomId) {
        this.owner.value = owner.toString();
        this.name.value = name;
        this.world_uid.value = world_uid.toString();
        this.cuboid = new DominionCuboid(cuboid);
        this.parentDomId.value = parentDomId;
        this.joinMessage.value = Configuration.pluginMessage.defaultEnterMessage;
        this.leaveMessage.value = Configuration.pluginMessage.defaultLeaveMessage;
        this.serverId.value = Configuration.multiServer.serverId;
    }

    private static class DominionCuboid extends CuboidDTO {
        public DominionCuboid(int x1, int y1, int z1, int x2, int y2, int z2) {
            super(x1, y1, z1, x2, y2, z2);
        }

        public DominionCuboid(CuboidDTO superObj) {
            super(superObj.x1(), superObj.y1(), superObj.z1(), superObj.x2(), superObj.y2(), superObj.z2());
        }

        public DominionCuboid(ResultSet rs) throws SQLException {
            super(rs.getInt("x1"), rs.getInt("y1"), rs.getInt("z1"),
                    rs.getInt("x2"), rs.getInt("y2"), rs.getInt("z2"));
        }

        public Field x1Field() {
            return new Field("x1", x1());
        }

        public Field y1Field() {
            return new Field("y1", y1());
        }

        public Field z1Field() {
            return new Field("z1", z1());
        }

        public Field x2Field() {
            return new Field("x2", x2());
        }

        public Field y2Field() {
            return new Field("y2", y2());
        }

        public Field z2Field() {
            return new Field("z2", z2());
        }
    }

    private DominionCuboid cuboid;
    private final Field id = new Field("id", FieldType.INT);
    private final Field owner = new Field("owner", FieldType.STRING);
    private final Field name = new Field("name", FieldType.STRING);
    private final Field parentDomId = new Field("parent_dom_id", -1);
    private final Field joinMessage = new Field("join_message", "");
    private final Field leaveMessage = new Field("leave_message", "");
    private final Map<EnvFlag, Boolean> envFlags = new HashMap<>();
    private final Map<PriFlag, Boolean> preFlags = new HashMap<>();
    private final Field tp_location = new Field("tp_location", "default");
    private final Field color = new Field("color", "#00BFFF");
    private final Field world_uid = new Field("world_uid", FieldType.STRING);
    private final Field serverId = new Field("server_id", FieldType.INT);


    // getters and setters
    @Override
    public @NotNull Integer getId() {
        return (Integer) id.value;
    }

    /**
     * 设置领地ID，该方法不会更新数据库，仅用于构造对象
     *
     * @param id 领地ID
     * @return 领地
     */
    public @NotNull DominionDTO setId(Integer id) {
        this.id.value = id;
        return this;
    }

    @Override
    public @NotNull UUID getOwner() {
        return UUID.fromString((String) owner.value);
    }

    @Override
    public @NotNull PlayerDTO getOwnerDTO() {
        return Objects.requireNonNull(CacheManager.instance.getPlayer(getOwner()));
    }

    private @NotNull DominionDTO doUpdate(UpdateRow updateRow) throws SQLException {
        updateRow.returningAll(id)
                .table("dominion")
                .where("id = ?", id.value);
        ResultSet rs = updateRow.execute();
        List<DominionDTO> dominions = getDTOFromRS(rs);
        if (dominions.isEmpty()) {
            throw new SQLException("Failed to update dominion.");
        }
        CacheManager.instance.getCache().getDominionCache().load(getId());
        return dominions.get(0);
    }

    @Override
    public @NotNull DominionDTO setOwner(UUID owner) throws SQLException {
        this.owner.value = owner.toString();
        return doUpdate(new UpdateRow().field(this.owner));
    }

    @Override
    public @NotNull DominionDTO setOwner(Player owner) throws SQLException {
        this.owner.value = owner.getUniqueId().toString();
        return doUpdate(new UpdateRow().field(this.owner));
    }

    @Override
    public @NotNull String getName() {
        return (String) name.value;
    }

    @Override
    public @NotNull DominionDTO setName(String name) throws SQLException {
        this.name.value = name;
        return doUpdate(new UpdateRow().field(this.name));
    }

    @Override
    public @Nullable World getWorld() {
        return Dominion.instance.getServer().getWorld(getWorldUid());
    }

    @Override
    public @NotNull UUID getWorldUid() {
        return UUID.fromString((String) world_uid.value);
    }

    @Override
    public @NotNull CuboidDTO getCuboid() {
        return cuboid;
    }

    @Override
    public @NotNull DominionDTO setCuboid(@NotNull CuboidDTO cuboid) throws SQLException {
        this.cuboid = new DominionCuboid(cuboid);
        return doUpdate(new UpdateRow()
                .field(this.cuboid.x1Field())
                .field(this.cuboid.y1Field())
                .field(this.cuboid.z1Field())
                .field(this.cuboid.x2Field())
                .field(this.cuboid.y2Field())
                .field(this.cuboid.z2Field()));
    }

    @Override
    public @NotNull Integer getParentDomId() {
        return (Integer) parentDomId.value;
    }

    @Override
    public @NotNull String getJoinMessage() {
        return (String) joinMessage.value;
    }

    @Override
    public @NotNull DominionDTO setJoinMessage(String joinMessage) throws SQLException {
        this.joinMessage.value = joinMessage;
        return doUpdate(new UpdateRow().field(this.joinMessage));
    }

    @Override
    public @NotNull String getLeaveMessage() {
        return (String) leaveMessage.value;
    }

    @Override
    public @NotNull DominionDTO setLeaveMessage(String leaveMessage) throws SQLException {
        this.leaveMessage.value = leaveMessage;
        return doUpdate(new UpdateRow().field(this.leaveMessage));
    }

    @Override
    public @NotNull Map<EnvFlag, Boolean> getEnvironmentFlagValue() {
        return envFlags;
    }

    /**
     * 获取领地某个环境配置的值
     *
     * @param flag 权限
     * @return 权限值
     */
    @Override
    public boolean getEnvFlagValue(@NotNull EnvFlag flag) {
        return envFlags.getOrDefault(flag, false);
    }

    @Override
    public @NotNull Map<PriFlag, Boolean> getGuestPrivilegeFlagValue() {
        return preFlags;
    }

    /**
     * 获取领地某个访客权限的值
     *
     * @param flag 权限
     * @return 权限值
     */
    @Override
    public boolean getGuestFlagValue(@NotNull PriFlag flag) {
        if (preFlags.equals(Flags.ADMIN)) { // guest's admin flag is always false
            return false;
        }
        return preFlags.getOrDefault(flag, false);
    }

    @Override
    public @NotNull DominionDTO setEnvFlagValue(@NotNull EnvFlag flag, @NotNull Boolean value) throws SQLException {
        envFlags.put(flag, value);
        Field flagField = new Field(flag.getFlagName(), value);
        return doUpdate(new UpdateRow().field(flagField));
    }

    @Override
    public @NotNull DominionDTO setGuestFlagValue(@NotNull PriFlag flag, @NotNull Boolean value) throws SQLException {
        preFlags.put(flag, value);
        Field flagField = new Field(flag.getFlagName(), value);
        return doUpdate(new UpdateRow().field(flagField));
    }


    @Override
    public @NotNull Location getTpLocation() {
        if (Objects.equals(tp_location.value, "default")) {
            return new Location(getWorld(),
                    (double) (cuboid.x1() + cuboid.x2()) / 2,
                    (double) (cuboid.y1() + cuboid.y2()) / 2,
                    (double) (cuboid.z1() + cuboid.z2()) / 2);
        } else {
            // 0:0:0
            String[] loc = ((String) tp_location.value).split(":");
            World w = getWorld();
            if (loc.length == 3 && w != null) {
                return new Location(w, Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
            } else {
                return new Location(getWorld(),
                        (double) (cuboid.x1() + cuboid.x2()) / 2,
                        (double) (cuboid.y1() + cuboid.y2()) / 2,
                        (double) (cuboid.z1() + cuboid.z2()) / 2);
            }
        }
    }

    @Override
    public @NotNull DominionDTO setTpLocation(Location loc) throws SQLException {
        this.tp_location.value = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
        return doUpdate(new UpdateRow().field(tp_location));
    }

    public @NotNull DominionDTO setColor(@NotNull Color color) throws SQLException {
        this.color.value = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        return doUpdate(new UpdateRow().field(this.color));
    }

    @Override
    public List<GroupDTO> getGroups() {
        return Objects.requireNonNull(CacheManager.instance.getCache(getServerId())).getGroupCache().getDominionGroups(this);
    }

    @Override
    public List<MemberDTO> getMembers() {
        return Objects.requireNonNull(CacheManager.instance.getCache(getServerId())).getMemberCache().getDominionMembers(this);
    }

    @Override
    public Integer getServerId() {
        return (Integer) serverId.value;
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
    public @NotNull String getColor() {
        return (String) color.value;
    }

    @Override
    public int getColorHex() {
        return (getColorR() << 16) + (getColorG() << 8) + getColorB();
    }
}
