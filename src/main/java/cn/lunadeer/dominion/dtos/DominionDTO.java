package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.utils.Database;
import cn.lunadeer.dominion.utils.XLogger;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DominionDTO {

    private static List<DominionDTO> query(String sql) {
        List<DominionDTO> dominions = new ArrayList<>();
        try (ResultSet rs = Database.query(sql)) {
            if (sql.contains("UPDATE") || sql.contains("DELETE") || sql.contains("INSERT")) {
                // 如果是更新操作，重新加载缓存
                Cache.instance.loadDominions();
            }
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
                DominionDTO dominion = new DominionDTO(id, owner, name, world, x1, y1, z1, x2, y2, z2, parentDomId,
                        rs.getString("join_message"),
                        rs.getString("leave_message"),
                        rs.getBoolean("anchor"),
                        rs.getBoolean("animal_killing"),
                        rs.getBoolean("anvil"),
                        rs.getBoolean("beacon"),
                        rs.getBoolean("bed"),
                        rs.getBoolean("brew"),
                        rs.getBoolean("break"),
                        rs.getBoolean("button"),
                        rs.getBoolean("cake"),
                        rs.getBoolean("container"),
                        rs.getBoolean("craft"),
                        rs.getBoolean("creeper_explode"),
                        rs.getBoolean("comparer"),
                        rs.getBoolean("door"),
                        rs.getBoolean("dye"),
                        rs.getBoolean("egg"),
                        rs.getBoolean("enchant"),
                        rs.getBoolean("ender_man"),
                        rs.getBoolean("ender_pearl"),
                        rs.getBoolean("feed"),
                        rs.getBoolean("fire_spread"),
                        rs.getBoolean("flow_in_protection"),
                        rs.getBoolean("glow"),
                        rs.getBoolean("harvest"),
                        rs.getBoolean("honey"),
                        rs.getBoolean("hook"),
                        rs.getBoolean("hopper"),
                        rs.getBoolean("ignite"),
                        rs.getBoolean("lever"),
                        rs.getBoolean("mob_drop_item"),
                        rs.getBoolean("monster_killing"),
                        rs.getBoolean("move"),
                        rs.getBoolean("place"),
                        rs.getBoolean("pressure"),
                        rs.getBoolean("riding"),
                        rs.getBoolean("repeater"),
                        rs.getBoolean("shear"),
                        rs.getBoolean("shoot"),
                        rs.getBoolean("show_border"),
                        rs.getBoolean("teleport"),
                        rs.getBoolean("tnt_explode"),
                        rs.getBoolean("trade"),
                        rs.getBoolean("trample"),
                        rs.getBoolean("vehicle_destroy"),
                        rs.getBoolean("vehicle_spawn"),
                        rs.getBoolean("wither_spawn"),
                        tp_location
                );
                dominions.add(dominion);
            }
        } catch (SQLException e) {
            XLogger.err("Database query failed: " + e.getMessage());
            XLogger.err("SQL: " + sql);
        }
        return dominions;
    }

    public static List<DominionDTO> selectAll() {
        String sql = "SELECT * FROM dominion WHERE id > 0;";
        return query(sql);
    }

    public static List<DominionDTO> selectAll(String world) {
        String sql = "SELECT * FROM dominion WHERE world = '" + world + "' AND id > 0;";
        return query(sql);
    }

    public static List<DominionDTO> search(String name) {
        String sql = "SELECT * FROM dominion WHERE name LIKE '%" + name + "%' AND id > 0;";
        return query(sql);
    }

    public static List<DominionDTO> selectAll(UUID owner) {
        String sql = "SELECT * FROM dominion WHERE owner = '" + owner.toString() + "' AND id > 0";
        return query(sql);
    }

    public static DominionDTO select(Integer id) {
        if (id == -1) {
            return new DominionDTO(-1,
                    UUID.fromString("00000000-0000-0000-0000-000000000000"),
                    "根领地", "all",
                    -2147483648, -2147483648, -2147483648,
                    2147483647, 2147483647, 2147483647, -1);
        }
        String sql = "SELECT * FROM dominion WHERE id = " + id + " AND id > 0";
        List<DominionDTO> dominions = query(sql);
        if (dominions.size() == 0) return null;
        return dominions.get(0);
    }

    public static List<DominionDTO> selectByParentId(String world, Integer parentId) {
        String sql = "SELECT * FROM dominion WHERE world = '" + world + "' AND parent_dom_id = " + parentId + " AND id > 0;";
        return query(sql);
    }

    public static List<DominionDTO> selectByLocation(String world, Integer x, Integer y, Integer z) {
        String sql = "SELECT * FROM dominion WHERE world = '" + world + "' AND " +
                "x1 <= " + x + " AND x2 >= " + x + " AND " +
                "y1 <= " + y + " AND y2 >= " + y + " AND " +
                "z1 <= " + z + " AND z2 >= " + z + " AND " + "id > 0;";
        return query(sql);
    }

    public static DominionDTO select(String name) {
        String sql = "SELECT * FROM dominion WHERE name = '" + name + "' AND id > 0;";
        List<DominionDTO> dominions = query(sql);
        if (dominions.size() == 0) return null;
        return dominions.get(0);
    }

    public static DominionDTO insert(DominionDTO dominion) {
        String sql = "INSERT INTO dominion (" +
                "owner, name, world, x1, y1, z1, x2, y2, z2" +
                ") VALUES (" +
                "'" + dominion.getOwner().toString() + "', " +
                "'" + dominion.getName() + "', " +
                "'" + dominion.getWorld() + "', " +
                dominion.getX1() + ", " +
                dominion.getY1() + ", " +
                dominion.getZ1() + ", " +
                dominion.getX2() + ", " +
                dominion.getY2() + ", " +
                dominion.getZ2() +
                ") RETURNING *;";
        List<DominionDTO> dominions = query(sql);
        if (dominions.size() == 0) return null;
        return dominions.get(0);
    }

    public static void delete(DominionDTO dominion) {
        String sql = "DELETE FROM dominion WHERE id = " + dominion.getId() + ";";
        query(sql);
    }

    private static DominionDTO update(DominionDTO dominion) {
        String tp_location;
        if (dominion.getTpLocation() == null) {
            tp_location = "default";
        } else {
            Location loc = dominion.getTpLocation();
            tp_location = loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
        }
        String sql = "UPDATE dominion SET " +
                "owner = '" + dominion.getOwner().toString() + "', " +
                "name = '" + dominion.getName() + "', " +
                "world = '" + dominion.getWorld() + "', " +
                "x1 = " + dominion.getX1() + ", " +
                "y1 = " + dominion.getY1() + ", " +
                "z1 = " + dominion.getZ1() + ", " +
                "x2 = " + dominion.getX2() + ", " +
                "y2 = " + dominion.getY2() + ", " +
                "z2 = " + dominion.getZ2() + ", " +
                "parent_dom_id = " + dominion.getParentDomId() + ", " +
                "join_message = '" + dominion.getJoinMessage() + "', " +
                "leave_message = '" + dominion.getLeaveMessage() + "', " +
                "anchor = " + dominion.getAnchor() + ", " +
                "animal_killing = " + dominion.getAnimalKilling() + ", " +
                "anvil = " + dominion.getAnvil() + ", " +
                "beacon = " + dominion.getBeacon() + ", " +
                "bed = " + dominion.getBed() + ", " +
                "brew = " + dominion.getBrew() + ", " +
                "break = " + dominion.getBreak() + ", " +
                "button = " + dominion.getButton() + ", " +
                "cake = " + dominion.getCake() + ", " +
                "container = " + dominion.getContainer() + ", " +
                "craft = " + dominion.getCraft() + ", " +
                "creeper_explode = " + dominion.getCreeperExplode() + ", " +        // dom only
                "comparer = " + dominion.getComparer() + ", " +
                "door = " + dominion.getDoor() + ", " +
                "dye = " + dominion.getDye() + ", " +
                "egg = " + dominion.getEgg() + ", " +
                "enchant = " + dominion.getEnchant() + ", " +
                "ender_man = " + dominion.getEnderMan() + ", " +                    // dom only
                "ender_pearl = " + dominion.getEnderPearl() + ", " +
                "feed = " + dominion.getFeed() + ", " +
                "fire_spread = " + dominion.getFireSpread() + ", " +                // dom only
                "flow_in_protection = " + dominion.getFlowInProtection() + ", " +   // dom only
                "glow = " + dominion.getGlow() + ", " +
                "harvest = " + dominion.getHarvest() + ", " +
                "honey = " + dominion.getHoney() + ", " +
                "hook = " + dominion.getHook() + ", " +
                "hopper = " + dominion.getHopper() + ", " +
                "ignite = " + dominion.getIgnite() + ", " +
                "lever = " + dominion.getLever() + ", " +
                "mob_drop_item = " + dominion.getMobDropItem() + ", " +            // dom only
                "monster_killing = " + dominion.getMonsterKilling() + ", " +
                "move = " + dominion.getMove() + ", " +
                "place = " + dominion.getPlace() + ", " +
                "pressure = " + dominion.getPressure() + ", " +
                "riding = " + dominion.getRiding() + ", " +
                "repeater = " + dominion.getRepeater() + ", " +
                "shear = " + dominion.getShear() + ", " +
                "shoot = " + dominion.getShoot() + ", " +
                "show_border = " + dominion.getShowBorder() + ", " +                // dom only
                "teleport = " + dominion.getTeleport() + ", " +
                "tnt_explode = " + dominion.getTntExplode() + ", " +                // dom only
                "trade = " + dominion.getTrade() + ", " +
                "trample = " + dominion.getTrample() + ", " +                       // dom only
                "vehicle_destroy = " + dominion.getVehicleDestroy() + ", " +
                "vehicle_spawn = " + dominion.getVehicleSpawn() + ", " +
                "wither_spawn = " + dominion.getWitherSpawn() + ", " +               // dom only
                "tp_location = '" + tp_location + "' " +
                " WHERE id = " + dominion.getId() +
                " RETURNING *;";
        List<DominionDTO> dominions = query(sql);
        if (dominions.size() == 0) return null;
        return dominions.get(0);
    }

    private DominionDTO(Integer id, UUID owner, String name, String world,
                        Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2,
                        Integer parentDomId,
                        String joinMessage, String leaveMessage,
                        Boolean anchor, Boolean animalKilling, Boolean anvil,
                        Boolean beacon, Boolean bed, Boolean brew, Boolean breakBlock, Boolean button,
                        Boolean cake, Boolean container, Boolean craft, Boolean creeperExplode, Boolean comparer,
                        Boolean door, Boolean dye,
                        Boolean egg, Boolean enchant, Boolean enderMan, Boolean enderPearl,
                        Boolean feed, Boolean fireSpread, Boolean flowInProtection,
                        Boolean glow,
                        Boolean harvest, Boolean honey, Boolean hook, Boolean hopper,
                        Boolean ignite,
                        Boolean lever,
                        Boolean mobDropItem, Boolean monsterKilling, Boolean move,
                        Boolean place, Boolean pressure,
                        Boolean riding, Boolean repeater,
                        Boolean shear, Boolean shoot, Boolean showBorder,
                        Boolean teleport, Boolean tntExplode, Boolean trade, Boolean trample,
                        Boolean vehicleDestroy,
                        Boolean vehicleSpawn,
                        Boolean witherSpawn,
                        String tp_location) {
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
        this.anchor = anchor;
        this.animalKilling = animalKilling;
        this.anvil = anvil;
        this.beacon = beacon;
        this.bed = bed;
        this.brew = brew;
        this.breakBlock = breakBlock;
        this.button = button;
        this.cake = cake;
        this.container = container;
        this.craft = craft;
        this.creeperExplode = creeperExplode;
        this.comparer = comparer;
        this.door = door;
        this.dye = dye;
        this.egg = egg;
        this.enchant = enchant;
        this.enderMan = enderMan;
        this.enderPearl = enderPearl;
        this.feed = feed;
        this.fireSpread = fireSpread;
        this.flowInProtection = flowInProtection;
        this.glow = glow;
        this.harvest = harvest;
        this.honey = honey;
        this.hook = hook;
        this.hopper = hopper;
        this.ignite = ignite;
        this.lever = lever;
        this.mobDropItem = mobDropItem;
        this.monsterKilling = monsterKilling;
        this.move = move;
        this.place = place;
        this.pressure = pressure;
        this.riding = riding;
        this.repeater = repeater;
        this.shear = shear;
        this.shoot = shoot;
        this.showBorder = showBorder;
        this.teleport = teleport;
        this.tntExplode = tntExplode;
        this.trade = trade;
        this.trample = trample;
        this.vehicleDestroy = vehicleDestroy;
        this.vehicleSpawn = vehicleSpawn;
        this.witherSpawn = witherSpawn;
        if (Objects.equals(tp_location, "default")) {
            this.tp_location = null;
        } else {
            // 0:0:0
            String[] loc = tp_location.split(":");
            World w = Dominion.instance.getServer().getWorld(world);
            if (loc.length == 3 && w != null) {
                this.tp_location = new Location(w, Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]));
            } else {
                XLogger.warn("领地传送点数据异常: " + tp_location);
                this.tp_location = null;
            }
        }
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
    private Boolean anchor = false;
    private Boolean animalKilling = false;
    private Boolean anvil = false;
    private Boolean beacon = false;
    private Boolean bed = false;
    private Boolean brew = false;
    private Boolean breakBlock = false;
    private Boolean button = false;
    private Boolean cake = false;
    private Boolean container = false;
    private Boolean craft = false;
    private Boolean creeperExplode = false;
    private Boolean comparer = false;
    private Boolean door = false;
    private Boolean dye = false;
    private Boolean egg = false;
    private Boolean enchant = false;
    private Boolean enderMan = false;
    private Boolean enderPearl = false;
    private Boolean feed = false;
    private Boolean fireSpread = false;
    private Boolean flowInProtection = false;
    private Boolean glow = false;
    private Boolean honey = false;
    private Boolean hook = false;
    private Boolean hopper = false;
    private Boolean ignite = false;
    private Boolean lever = false;
    private Boolean mobDropItem = true;
    private Boolean monsterKilling = false;
    private Boolean move = true;
    private Boolean place = false;
    private Boolean pressure = false;
    private Boolean riding = false;
    private Boolean repeater = false;
    private Boolean shear = false;
    private Boolean shoot = false;
    private Boolean showBorder = true;
    private Boolean teleport = false;
    private Boolean tntExplode = false;
    private Boolean trade = false;
    private Boolean trample = false;
    private Boolean vehicleDestroy = false;
    private Boolean vehicleSpawn = false;
    private Boolean witherSpawn = false;
    private Boolean harvest = false;
    private Location tp_location = null;

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
        return (x2 - x1) * (z2 - z1);
    }

    public Integer getVolume() {
        return getSquare() * (y2 - y1);
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

    public Boolean getAnchor() {
        return anchor;
    }

    public DominionDTO setAnchor(Boolean anchor) {
        this.anchor = anchor;
        return update(this);
    }

    public Boolean getAnimalKilling() {
        return animalKilling;
    }

    public DominionDTO setAnimalKilling(Boolean animalKilling) {
        this.animalKilling = animalKilling;
        return update(this);
    }

    public Boolean getAnvil() {
        return anvil;
    }

    public DominionDTO setAnvil(Boolean anvil) {
        this.anvil = anvil;
        return update(this);
    }

    public Boolean getBeacon() {
        return beacon;
    }

    public DominionDTO setBeacon(Boolean beacon) {
        this.beacon = beacon;
        return update(this);
    }

    public Boolean getBed() {
        return bed;
    }

    public DominionDTO setBed(Boolean bed) {
        this.bed = bed;
        return update(this);
    }

    public Boolean getBrew() {
        return brew;
    }

    public DominionDTO setBrew(Boolean brew) {
        this.brew = brew;
        return update(this);
    }

    public Boolean getBreak() {
        return breakBlock;
    }

    public DominionDTO setBreak(Boolean breakBlock) {
        this.breakBlock = breakBlock;
        return update(this);
    }

    public Boolean getButton() {
        return button;
    }

    public DominionDTO setButton(Boolean button) {
        this.button = button;
        return update(this);
    }

    public Boolean getCake() {
        return cake;
    }

    public DominionDTO setCake(Boolean cake) {
        this.cake = cake;
        return update(this);
    }

    public Boolean getContainer() {
        return container;
    }

    public DominionDTO setContainer(Boolean container) {
        this.container = container;
        return update(this);
    }

    public Boolean getCraft() {
        return craft;
    }

    public DominionDTO setCraft(Boolean craft) {
        this.craft = craft;
        return update(this);
    }

    public Boolean getCreeperExplode() {
        return creeperExplode;
    }

    public DominionDTO setCreeperExplode(Boolean creeperExplode) {
        this.creeperExplode = creeperExplode;
        return update(this);
    }

    public Boolean getComparer() {
        return comparer;
    }

    public DominionDTO setComparer(Boolean comparer) {
        this.comparer = comparer;
        return update(this);
    }

    public Boolean getDoor() {
        return door;
    }

    public DominionDTO setDoor(Boolean door) {
        this.door = door;
        return update(this);
    }

    public Boolean getDye() {
        return dye;
    }

    public DominionDTO setDye(Boolean dye) {
        this.dye = dye;
        return update(this);
    }

    public Boolean getEgg() {
        return egg;
    }

    public DominionDTO setEgg(Boolean egg) {
        this.egg = egg;
        return update(this);
    }

    public Boolean getEnchant() {
        return enchant;
    }

    public DominionDTO setEnchant(Boolean enchant) {
        this.enchant = enchant;
        return update(this);
    }

    public Boolean getEnderMan() {
        return enderMan;
    }

    public DominionDTO setEnderMan(Boolean enderMan) {
        this.enderMan = enderMan;
        return update(this);
    }

    public Boolean getEnderPearl() {
        return enderPearl;
    }

    public DominionDTO setEnderPearl(Boolean enderPearl) {
        this.enderPearl = enderPearl;
        return update(this);
    }

    public Boolean getFeed() {
        return feed;
    }

    public DominionDTO setFeed(Boolean feed) {
        this.feed = feed;
        return update(this);
    }

    public Boolean getFireSpread() {
        return fireSpread;
    }

    public DominionDTO setFireSpread(Boolean fireSpread) {
        this.fireSpread = fireSpread;
        return update(this);
    }

    public Boolean getFlowInProtection() {
        return flowInProtection;
    }

    public DominionDTO setFlowInProtection(Boolean flowInProtection) {
        this.flowInProtection = flowInProtection;
        return update(this);
    }

    public Boolean getGlow() {
        return glow;
    }

    public DominionDTO setGlow(Boolean glow) {
        this.glow = glow;
        return update(this);
    }

    public Boolean getHoney() {
        return honey;
    }

    public DominionDTO setHoney(Boolean honey) {
        this.honey = honey;
        return update(this);
    }

    public Boolean getHook() {
        return hook;
    }

    public DominionDTO setHook(Boolean hook) {
        this.hook = hook;
        return update(this);
    }

    public Boolean getHopper() {
        return hopper;
    }

    public DominionDTO setHopper(Boolean hopper) {
        this.hopper = hopper;
        return update(this);
    }

    public Boolean getIgnite() {
        return ignite;
    }

    public DominionDTO setIgnite(Boolean ignite) {
        this.ignite = ignite;
        return update(this);
    }

    public Boolean getLever() {
        return lever;
    }

    public DominionDTO setLever(Boolean lever) {
        this.lever = lever;
        return update(this);
    }

    public Boolean getMobDropItem() {
        return mobDropItem;
    }

    public DominionDTO setMobDropItem(Boolean mobDropItem) {
        this.mobDropItem = mobDropItem;
        return update(this);
    }

    public Boolean getMonsterKilling() {
        return monsterKilling;
    }

    public DominionDTO setMonsterKilling(Boolean monsterKilling) {
        this.monsterKilling = monsterKilling;
        return update(this);
    }

    public Boolean getMove() {
        return move;
    }

    public DominionDTO setMove(Boolean move) {
        this.move = move;
        return update(this);
    }

    public Boolean getPlace() {
        return place;
    }

    public DominionDTO setPlace(Boolean place) {
        this.place = place;
        return update(this);
    }

    public Boolean getPressure() {
        return pressure;
    }

    public DominionDTO setPressure(Boolean pressure) {
        this.pressure = pressure;
        return update(this);
    }

    public Boolean getRiding() {
        return riding;
    }

    public DominionDTO setRiding(Boolean riding) {
        this.riding = riding;
        return update(this);
    }

    public Boolean getRepeater() {
        return repeater;
    }

    public DominionDTO setRepeater(Boolean repeater) {
        this.repeater = repeater;
        return update(this);
    }

    public Boolean getShear() {
        return shear;
    }

    public DominionDTO setShear(Boolean shear) {
        this.shear = shear;
        return update(this);
    }

    public Boolean getShoot() {
        return shoot;
    }

    public DominionDTO setShoot(Boolean shoot) {
        this.shoot = shoot;
        return update(this);
    }

    public Boolean getShowBorder() {
        return showBorder;
    }

    public DominionDTO setShowBorder(Boolean showBorder) {
        this.showBorder = showBorder;
        return update(this);
    }

    public Boolean getTeleport() {
        return teleport;
    }

    public DominionDTO setTeleport(Boolean teleport) {
        this.teleport = teleport;
        return update(this);
    }

    public Boolean getTntExplode() {
        return tntExplode;
    }

    public DominionDTO setTntExplode(Boolean tntExplode) {
        this.tntExplode = tntExplode;
        return update(this);
    }

    public Boolean getTrade() {
        return trade;
    }

    public DominionDTO setTrade(Boolean trade) {
        this.trade = trade;
        return update(this);
    }

    public Boolean getTrample() {
        return trample;
    }

    public DominionDTO setTrample(Boolean trample) {
        this.trample = trample;
        return update(this);
    }

    public Boolean getVehicleDestroy() {
        return vehicleDestroy;
    }

    public DominionDTO setVehicleDestroy(Boolean vehicleDestroy) {
        this.vehicleDestroy = vehicleDestroy;
        return update(this);
    }

    public Boolean getVehicleSpawn() {
        return vehicleSpawn;
    }

    public DominionDTO setVehicleSpawn(Boolean vehicleSpawn) {
        this.vehicleSpawn = vehicleSpawn;
        return update(this);
    }

    public Boolean getWitherSpawn() {
        return witherSpawn;
    }

    public DominionDTO setWitherSpawn(Boolean witherSpawn) {
        this.witherSpawn = witherSpawn;
        return update(this);
    }

    public Boolean getHarvest() {
        return harvest;
    }

    public DominionDTO setHarvest(Boolean harvest) {
        this.harvest = harvest;
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
        return tp_location;
    }

    public DominionDTO setTpLocation(Location loc) {
        this.tp_location = loc;
        return update(this);
    }
}
