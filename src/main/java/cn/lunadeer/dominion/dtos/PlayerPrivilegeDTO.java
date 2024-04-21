package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Cache;
import cn.lunadeer.dominion.utils.Database;
import cn.lunadeer.dominion.utils.XLogger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerPrivilegeDTO {

    public static PlayerPrivilegeDTO insert(PlayerPrivilegeDTO player) {
        String sql = "INSERT INTO player_privilege (player_uuid, admin, dom_id, " +
                "anchor, animal_killing, anvil, " +
                "beacon, bed, brew, break, button, " +
                "cake, container, craft, comparer, " +
                "door, dye, " +
                "egg, enchant, ender_pearl, " +
                "feed, " +
                "glow, " +
                "harvest, honey, hook, hopper, " +
                "ignite, " +
                "lever, " +
                "monster_killing, move, " +
                "place, pressure, " +
                "riding, repeater, " +
                "shear, shoot, " +
                "teleport, trade, " +
                "vehicle_destroy, " +
                "vehicle_spawn" +
                ") VALUES (" +
                "'" + player.getPlayerUUID() + "', " + player.getAdmin() + ", " + player.getDomID() + ", " +
                player.getAnchor() + ", " + player.getAnimalKilling() + ", " + player.getAnvil() + ", " +
                player.getBeacon() + ", " + player.getBed() + ", " + player.getBrew() + ", " + player.getBreak() + ", " + player.getButton() + ", " +
                player.getCake() + ", " + player.getContainer() + ", " + player.getCraft() + ", " + player.getComparer() + ", " +
                player.getDoor() + ", " + player.getDye() + ", " +
                player.getEgg() + ", " + player.getEnchant() + ", " + player.getEnderPearl() + ", " +
                player.getFeed() + ", " +
                player.getGlow() + ", " +
                player.getHarvest() + ", " + player.getHoney() + ", " + player.getHook() + ", " + player.getHopper() + ", " +
                player.getIgnite() + ", " +
                player.getLever() + ", " +
                player.getMonsterKilling() + ", " + player.getMove() + ", " +
                player.getPlace() + ", " + player.getPressure() + ", " +
                player.getRiding() + ", " + player.getRepeater() + ", " +
                player.getShear() + ", " + player.getShoot() + ", " +
                player.getTeleport() + ", " + player.getTrade() + ", " +
                player.getVehicleDestroy() + ", " +
                player.getVehicleSpawn() + " " +
                ") RETURNING *;";
        List<PlayerPrivilegeDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    public static PlayerPrivilegeDTO select(UUID playerUUID, Integer dom_id) {
        String sql = "SELECT * FROM player_privilege WHERE player_uuid = '" + playerUUID + "' " +
                "AND dom_id = " + dom_id + ";";
        List<PlayerPrivilegeDTO> p = query(sql);
        if (p.size() == 0) return null;
        return p.get(0);
    }

    public static List<PlayerPrivilegeDTO> select(Integer dom_id) {
        String sql = "SELECT * FROM player_privilege WHERE dom_id = " + dom_id + ";";
        return query(sql);
    }

    public static void delete(UUID player, Integer domID) {
        String sql = "DELETE FROM player_privilege WHERE player_uuid = '" + player + "' " +
                "AND dom_id = " + domID + ";";
        query(sql);
    }

    public static List<PlayerPrivilegeDTO> selectAll() {
        String sql = "SELECT * FROM player_privilege;";
        return query(sql);
    }

    public static List<PlayerPrivilegeDTO> selectAll(UUID player) {
        String sql = "SELECT * FROM player_privilege WHERE player_uuid = '" + player + "';";
        return query(sql);
    }

    private final Integer id;
    private final UUID playerUUID;
    private Boolean admin;
    private final Integer domID;
    private Boolean anchor;
    private Boolean animalKilling;
    private Boolean anvil;
    private Boolean beacon;
    private Boolean bed;
    private Boolean brew;
    private Boolean breakBlock;
    private Boolean button;
    private Boolean cake;
    private Boolean container;
    private Boolean craft;
    private Boolean comparer;
    private Boolean door;
    private Boolean dye;
    private Boolean egg;
    private Boolean enchant;
    private Boolean enderPearl;
    private Boolean feed;
    private Boolean glow;
    private Boolean harvest;
    private Boolean honey;
    private Boolean hook;
    private Boolean hopper;
    private Boolean ignite;
    private Boolean lever;
    private Boolean monsterKilling;
    private Boolean move;
    private Boolean place;
    private Boolean pressure;
    private Boolean riding;
    private Boolean repeater;
    private Boolean shear;
    private Boolean shoot;
    private Boolean teleport;
    private Boolean trade;
    private Boolean vehicleDestroy;
    private Boolean vehicleSpawn;

    public Integer getId() {
        return id;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public Integer getDomID() {
        return domID;
    }

    public Boolean getAnchor() {
        return anchor;
    }

    public Boolean getAnimalKilling() {
        return animalKilling;
    }

    public Boolean getAnvil() {
        return anvil;
    }

    public Boolean getBeacon() {
        return beacon;
    }

    public Boolean getBed() {
        return bed;
    }

    public Boolean getBrew() {
        return brew;
    }

    public Boolean getBreak() {
        return breakBlock;
    }

    public Boolean getButton() {
        return button;
    }

    public Boolean getCake() {
        return cake;
    }

    public Boolean getContainer() {
        return container;
    }

    public Boolean getCraft() {
        return craft;
    }

    public Boolean getComparer() {
        return comparer;
    }

    public Boolean getDoor() {
        return door;
    }

    public Boolean getDye() {
        return dye;
    }

    public Boolean getEgg() {
        return egg;
    }

    public Boolean getEnchant() {
        return enchant;
    }

    public Boolean getEnderPearl() {
        return enderPearl;
    }

    public Boolean getFeed() {
        return feed;
    }

    public Boolean getGlow() {
        return glow;
    }

    public Boolean getHarvest() {
        return harvest;
    }

    public Boolean getHoney() {
        return honey;
    }

    public Boolean getHook() {
        return hook;
    }

    public Boolean getHopper() {
        return hopper;
    }

    public Boolean getIgnite() {
        return ignite;
    }

    public Boolean getLever() {
        return lever;
    }

    public Boolean getMonsterKilling() {
        return monsterKilling;
    }

    public Boolean getMove() {
        return move;
    }

    public Boolean getPlace() {
        return place;
    }

    public Boolean getPressure() {
        return pressure;
    }

    public Boolean getRiding() {
        return riding;
    }

    public Boolean getRepeater() {
        return repeater;
    }

    public Boolean getShear() {
        return shear;
    }

    public Boolean getShoot() {
        return shoot;
    }

    public Boolean getTeleport() {
        return teleport;
    }

    public Boolean getTrade() {
        return trade;
    }

    public Boolean getVehicleDestroy() {
        return vehicleDestroy;
    }

    public Boolean getVehicleSpawn() {
        return vehicleSpawn;
    }

    public PlayerPrivilegeDTO setAnchor(Boolean anchor) {
        this.anchor = anchor;
        return update(this);
    }

    public PlayerPrivilegeDTO setAnimalKilling(Boolean animalKilling) {
        this.animalKilling = animalKilling;
        return update(this);
    }

    public PlayerPrivilegeDTO setAnvil(Boolean anvil) {
        this.anvil = anvil;
        return update(this);
    }

    public PlayerPrivilegeDTO setBeacon(Boolean beacon) {
        this.beacon = beacon;
        return update(this);
    }

    public PlayerPrivilegeDTO setBed(Boolean bed) {
        this.bed = bed;
        return update(this);
    }

    public PlayerPrivilegeDTO setBrew(Boolean brew) {
        this.brew = brew;
        return update(this);
    }

    public PlayerPrivilegeDTO setBreak(Boolean breakBlock) {
        this.breakBlock = breakBlock;
        return update(this);
    }

    public PlayerPrivilegeDTO setButton(Boolean button) {
        this.button = button;
        return update(this);
    }

    public PlayerPrivilegeDTO setCake(Boolean cake) {
        this.cake = cake;
        return update(this);
    }

    public PlayerPrivilegeDTO setContainer(Boolean container) {
        this.container = container;
        return update(this);
    }

    public PlayerPrivilegeDTO setCraft(Boolean craft) {
        this.craft = craft;
        return update(this);
    }

    public PlayerPrivilegeDTO setComparer(Boolean comparer) {
        this.comparer = comparer;
        return update(this);
    }

    public PlayerPrivilegeDTO setDoor(Boolean door) {
        this.door = door;
        return update(this);
    }

    public PlayerPrivilegeDTO setDye(Boolean dye) {
        this.dye = dye;
        return update(this);
    }

    public PlayerPrivilegeDTO setEgg(Boolean egg) {
        this.egg = egg;
        return update(this);
    }

    public PlayerPrivilegeDTO setEnchant(Boolean enchant) {
        this.enchant = enchant;
        return update(this);
    }

    public PlayerPrivilegeDTO setEnderPearl(Boolean enderPearl) {
        this.enderPearl = enderPearl;
        return update(this);
    }

    public PlayerPrivilegeDTO setFeed(Boolean feed) {
        this.feed = feed;
        return update(this);
    }

    public PlayerPrivilegeDTO setGlow(Boolean glow) {
        this.glow = glow;
        return update(this);
    }

    public PlayerPrivilegeDTO setHoney(Boolean honey) {
        this.honey = honey;
        return update(this);
    }

    public PlayerPrivilegeDTO setHook(Boolean hook) {
        this.hook = hook;
        return update(this);
    }

    public PlayerPrivilegeDTO setHopper(Boolean hopper) {
        this.hopper = hopper;
        return update(this);
    }

    public PlayerPrivilegeDTO setIgnite(Boolean ignite) {
        this.ignite = ignite;
        return update(this);
    }

    public PlayerPrivilegeDTO setLever(Boolean lever) {
        this.lever = lever;
        return update(this);
    }

    public PlayerPrivilegeDTO setMonsterKilling(Boolean monsterKilling) {
        this.monsterKilling = monsterKilling;
        return update(this);
    }

    public PlayerPrivilegeDTO setMove(Boolean move) {
        this.move = move;
        return update(this);
    }

    public PlayerPrivilegeDTO setPlace(Boolean place) {
        this.place = place;
        return update(this);
    }

    public PlayerPrivilegeDTO setPressure(Boolean pressure) {
        this.pressure = pressure;
        return update(this);
    }

    public PlayerPrivilegeDTO setRiding(Boolean riding) {
        this.riding = riding;
        return update(this);
    }

    public PlayerPrivilegeDTO setRepeater(Boolean repeater) {
        this.repeater = repeater;
        return update(this);
    }

    public PlayerPrivilegeDTO setShear(Boolean shear) {
        this.shear = shear;
        return update(this);
    }

    public PlayerPrivilegeDTO setShoot(Boolean shoot) {
        this.shoot = shoot;
        return update(this);
    }

    public PlayerPrivilegeDTO setTeleport(Boolean teleport) {
        this.teleport = teleport;
        return update(this);
    }

    public PlayerPrivilegeDTO setTrade(Boolean trade) {
        this.trade = trade;
        return update(this);
    }

    public PlayerPrivilegeDTO setVehicleDestroy(Boolean vehicleDestroy) {
        this.vehicleDestroy = vehicleDestroy;
        return update(this);
    }

    public PlayerPrivilegeDTO setVehicleSpawn(Boolean vehicleSpawn) {
        this.vehicleSpawn = vehicleSpawn;
        return update(this);
    }

    public PlayerPrivilegeDTO setHarvest(Boolean harvest) {
        this.harvest = harvest;
        return update(this);
    }

    public PlayerPrivilegeDTO setAdmin(Boolean admin) {
        this.admin = admin;
        return update(this);
    }

    private PlayerPrivilegeDTO(Integer id, UUID playerUUID, Boolean admin, Integer domID,
                               Boolean anchor, Boolean animalKilling, Boolean anvil,
                               Boolean beacon, Boolean bed, Boolean brew, Boolean breakBlock, Boolean button,
                               Boolean cake, Boolean container, Boolean craft, Boolean comparer,
                               Boolean door, Boolean dye,
                               Boolean egg, Boolean enchant, Boolean enderPearl,
                               Boolean feed,
                               Boolean glow,
                               Boolean harvest, Boolean honey, Boolean hook, Boolean hopper,
                               Boolean ignite,
                               Boolean lever,
                               Boolean monsterKilling, Boolean move,
                               Boolean place, Boolean pressure,
                               Boolean riding, Boolean repeater,
                               Boolean shear, Boolean shoot,
                               Boolean teleport, Boolean trade,
                               Boolean vehicleDestroy,
                               Boolean vehicleSpawn) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.admin = admin;
        this.domID = domID;
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
        this.comparer = comparer;
        this.door = door;
        this.dye = dye;
        this.egg = egg;
        this.enchant = enchant;
        this.enderPearl = enderPearl;
        this.feed = feed;
        this.glow = glow;
        this.harvest = harvest;
        this.honey = honey;
        this.hook = hook;
        this.hopper = hopper;
        this.ignite = ignite;
        this.lever = lever;
        this.monsterKilling = monsterKilling;
        this.move = move;
        this.place = place;
        this.pressure = pressure;
        this.riding = riding;
        this.repeater = repeater;
        this.shear = shear;
        this.shoot = shoot;
        this.teleport = teleport;
        this.trade = trade;
        this.vehicleDestroy = vehicleDestroy;
        this.vehicleSpawn = vehicleSpawn;
    }

    public PlayerPrivilegeDTO(UUID playerUUID, Integer domID,
                              Boolean anchor, Boolean animalKilling, Boolean anvil,
                              Boolean beacon, Boolean bed, Boolean brew, Boolean breakBlock, Boolean button,
                              Boolean cake, Boolean container, Boolean craft, Boolean comparer,
                              Boolean door, Boolean dye,
                              Boolean egg, Boolean enchant, Boolean enderPearl,
                              Boolean feed,
                              Boolean glow,
                              Boolean harvest, Boolean honey, Boolean hook, Boolean hopper,
                              Boolean ignite,
                              Boolean lever,
                              Boolean monsterKilling, Boolean move,
                              Boolean place, Boolean pressure,
                              Boolean riding, Boolean repeater,
                              Boolean shear, Boolean shoot,
                              Boolean teleport, Boolean trade,
                              Boolean vehicleDestroy,
                              Boolean vehicleSpawn
    ) {
        this(null, playerUUID, false, domID,
                anchor, animalKilling, anvil,
                beacon, bed, brew, breakBlock, button,
                cake, container, craft, comparer,
                door, dye,
                egg, enchant, enderPearl,
                feed,
                glow,
                harvest, honey, hook, hopper,
                ignite,
                lever,
                monsterKilling, move,
                place, pressure,
                riding, repeater,
                shear, shoot,
                teleport, trade,
                vehicleDestroy,
                vehicleSpawn);
    }

    private static List<PlayerPrivilegeDTO> query(String sql) {
        List<PlayerPrivilegeDTO> players = new ArrayList<>();
        try (ResultSet rs = Database.query(sql)) {
            if (sql.contains("UPDATE") || sql.contains("DELETE") || sql.contains("INSERT")) {
                // 如果是更新操作，重新加载缓存
                Cache.instance.loadPlayerPrivileges();
            }
            if (rs == null) return players;
            while (rs.next()) {
                PlayerPrivilegeDTO player = new PlayerPrivilegeDTO(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getBoolean("admin"),
                        rs.getInt("dom_id"),
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
                        rs.getBoolean("comparer"),
                        rs.getBoolean("door"),
                        rs.getBoolean("dye"),
                        rs.getBoolean("egg"),
                        rs.getBoolean("enchant"),
                        rs.getBoolean("ender_pearl"),
                        rs.getBoolean("feed"),
                        rs.getBoolean("glow"),
                        rs.getBoolean("harvest"),
                        rs.getBoolean("honey"),
                        rs.getBoolean("hook"),
                        rs.getBoolean("hopper"),
                        rs.getBoolean("ignite"),
                        rs.getBoolean("lever"),
                        rs.getBoolean("monster_killing"),
                        rs.getBoolean("move"),
                        rs.getBoolean("place"),
                        rs.getBoolean("pressure"),
                        rs.getBoolean("riding"),
                        rs.getBoolean("repeater"),
                        rs.getBoolean("shear"),
                        rs.getBoolean("shoot"),
                        rs.getBoolean("teleport"),
                        rs.getBoolean("trade"),
                        rs.getBoolean("vehicle_destroy"),
                        rs.getBoolean("vehicle_spawn")
                );
                players.add(player);
            }
        } catch (Exception e) {
            XLogger.err("Database query failed: " + e.getMessage());
            XLogger.err("SQL: " + sql);
        }
        return players;
    }

    private static PlayerPrivilegeDTO update(PlayerPrivilegeDTO player) {
        String sql = "UPDATE player_privilege SET " +
                "admin = " + player.getAdmin() + ", " +
                "dom_id = " + player.getDomID() + ", " +
                "anchor = " + player.getAnchor() + ", " +
                "animal_killing = " + player.getAnimalKilling() + ", " +
                "anvil = " + player.getAnvil() + ", " +
                "beacon = " + player.getBeacon() + ", " +
                "bed = " + player.getBed() + ", " +
                "brew = " + player.getBrew() + ", " +
                "break = " + player.getBreak() + ", " +
                "button = " + player.getButton() + ", " +
                "cake = " + player.getCake() + ", " +
                "container = " + player.getContainer() + ", " +
                "craft = " + player.getCraft() + ", " +
                "comparer = " + player.getComparer() + ", " +
                "door = " + player.getDoor() + ", " +
                "dye = " + player.getDye() + ", " +
                "egg = " + player.getEgg() + ", " +
                "enchant = " + player.getEnchant() + ", " +
                "ender_pearl = " + player.getEnderPearl() + ", " +
                "feed = " + player.getFeed() + ", " +
                "glow = " + player.getGlow() + ", " +
                "harvest = " + player.getHarvest() + ", " +
                "honey = " + player.getHoney() + ", " +
                "hook = " + player.getHook() + ", " +
                "hopper = " + player.getHopper() + ", " +
                "ignite = " + player.getIgnite() + ", " +
                "lever = " + player.getLever() + ", " +
                "monster_killing = " + player.getMonsterKilling() + ", " +
                "move = " + player.getMove() + ", " +
                "place = " + player.getPlace() + ", " +
                "pressure = " + player.getPressure() + ", " +
                "riding = " + player.getRiding() + ", " +
                "repeater = " + player.getRepeater() + ", " +
                "shear = " + player.getShear() + ", " +
                "shoot = " + player.getShoot() + ", " +
                "teleport = " + player.getTeleport() + ", " +
                "trade = " + player.getTrade() + ", " +
                "vehicle_destroy = " + player.getVehicleDestroy() + ", " +
                "vehicle_spawn = " + player.getVehicleSpawn() + " " +
                "WHERE id = " + player.getId() + " " +
                "RETURNING *;";
        List<PlayerPrivilegeDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }
}
