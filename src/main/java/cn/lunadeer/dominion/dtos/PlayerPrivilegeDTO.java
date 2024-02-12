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
        String sql = "INSERT INTO player_privilege (player_uuid, admin, dom_id) VALUES (" +
                "'" + player.getPlayerUUID() + "', " +
                player.getAdmin() + ", " +
                player.getDomID() +
                ") RETURNING *;";
        List<PlayerPrivilegeDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }

    public static PlayerPrivilegeDTO select(UUID playerUUID, Integer dom_id) {
        String sql = "SELECT * FROM player_privilege WHERE player_uuid = '" + playerUUID + "' " +
                "AND dom_id = " + dom_id + ";";
        List<PlayerPrivilegeDTO> p =  query(sql);
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

    public static List<PlayerPrivilegeDTO> selectAll(){
        String sql = "SELECT * FROM player_privilege;";
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
    private Boolean button;
    private Boolean cake;
    private Boolean container;
    private Boolean craft;
    private Boolean diode;
    private Boolean door;
    private Boolean dye;
    private Boolean egg;
    private Boolean enchant;
    private Boolean enderPearl;
    private Boolean feed;
    private Boolean glow;
    private Boolean honey;
    private Boolean hook;
    private Boolean ignite;
    private Boolean mobKilling;
    private Boolean move;
    private Boolean place;
    private Boolean pressure;
    private Boolean riding;
    private Boolean shear;
    private Boolean shoot;
    private Boolean trade;
    private Boolean vehicleDestroy;
    private Boolean harvest;

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

    public Boolean getDiode() {
        return diode;
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

    public Boolean getHoney() {
        return honey;
    }

    public Boolean getHook() {
        return hook;
    }

    public Boolean getIgnite() {
        return ignite;
    }

    public Boolean getMobKilling() {
        return mobKilling;
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

    public Boolean getShear() {
        return shear;
    }

    public Boolean getShoot() {
        return shoot;
    }

    public Boolean getTrade() {
        return trade;
    }

    public Boolean getVehicleDestroy() {
        return vehicleDestroy;
    }

    public Boolean getHarvest() {
        return harvest;
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

    public PlayerPrivilegeDTO setDiode(Boolean diode) {
        this.diode = diode;
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

    public PlayerPrivilegeDTO setIgnite(Boolean ignite) {
        this.ignite = ignite;
        return update(this);
    }

    public PlayerPrivilegeDTO setMobKilling(Boolean mobKilling) {
        this.mobKilling = mobKilling;
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

    public PlayerPrivilegeDTO setShear(Boolean shear) {
        this.shear = shear;
        return update(this);
    }

    public PlayerPrivilegeDTO setShoot(Boolean shoot) {
        this.shoot = shoot;
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
                               Boolean beacon, Boolean bed, Boolean brew, Boolean button, Boolean cake,
                               Boolean container, Boolean craft, Boolean diode, Boolean door, Boolean dye,
                               Boolean egg, Boolean enchant, Boolean enderPearl, Boolean feed, Boolean glow,
                               Boolean honey, Boolean hook, Boolean ignite, Boolean mobKilling, Boolean move,
                               Boolean place, Boolean pressure, Boolean riding, Boolean shear, Boolean shoot,
                               Boolean trade, Boolean vehicleDestroy, Boolean harvest) {
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
        this.button = button;
        this.cake = cake;
        this.container = container;
        this.craft = craft;
        this.diode = diode;
        this.door = door;
        this.dye = dye;
        this.egg = egg;
        this.enchant = enchant;
        this.enderPearl = enderPearl;
        this.feed = feed;
        this.glow = glow;
        this.honey = honey;
        this.hook = hook;
        this.ignite = ignite;
        this.mobKilling = mobKilling;
        this.move = move;
        this.place = place;
        this.pressure = pressure;
        this.riding = riding;
        this.shear = shear;
        this.shoot = shoot;
        this.trade = trade;
        this.vehicleDestroy = vehicleDestroy;
        this.harvest = harvest;
    }

    public PlayerPrivilegeDTO(UUID playerUUID, Boolean admin, Integer domID) {
        this(null, playerUUID, admin, domID,
                false, false, false,
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, false);
    }

    private static List<PlayerPrivilegeDTO> query(String sql) {
        List<PlayerPrivilegeDTO> players = new ArrayList<>();
        try (ResultSet rs = Database.query(sql)) {
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
                        rs.getBoolean("button"),
                        rs.getBoolean("cake"),
                        rs.getBoolean("container"),
                        rs.getBoolean("craft"),
                        rs.getBoolean("diode"),
                        rs.getBoolean("door"),
                        rs.getBoolean("dye"),
                        rs.getBoolean("egg"),
                        rs.getBoolean("enchant"),
                        rs.getBoolean("ender_pearl"),
                        rs.getBoolean("feed"),
                        rs.getBoolean("glow"),
                        rs.getBoolean("honey"),
                        rs.getBoolean("hook"),
                        rs.getBoolean("ignite"),
                        rs.getBoolean("mob_killing"),
                        rs.getBoolean("move"),
                        rs.getBoolean("place"),
                        rs.getBoolean("pressure"),
                        rs.getBoolean("riding"),
                        rs.getBoolean("shear"),
                        rs.getBoolean("shoot"),
                        rs.getBoolean("trade"),
                        rs.getBoolean("vehicle_destroy"),
                        rs.getBoolean("harvest")
                );
                players.add(player);
            }
            if (sql.contains("UPDATE") || sql.contains("DELETE") || sql.contains("INSERT")){
                // 如果是更新操作，重新加载缓存
                Cache.instance.loadPlayerPrivileges();
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
                "button = " + player.getButton() + ", " +
                "cake = " + player.getCake() + ", " +
                "container = " + player.getContainer() + ", " +
                "craft = " + player.getCraft() + ", " +
                "diode = " + player.getDiode() + ", " +
                "door = " + player.getDoor() + ", " +
                "dye = " + player.getDye() + ", " +
                "egg = " + player.getEgg() + ", " +
                "enchant = " + player.getEnchant() + ", " +
                "ender_pearl = " + player.getEnderPearl() + ", " +
                "feed = " + player.getFeed() + ", " +
                "glow = " + player.getGlow() + ", " +
                "honey = " + player.getHoney() + ", " +
                "hook = " + player.getHook() + ", " +
                "ignite = " + player.getIgnite() + ", " +
                "mob_killing = " + player.getMobKilling() + ", " +
                "move = " + player.getMove() + ", " +
                "place = " + player.getPlace() + ", " +
                "pressure = " + player.getPressure() + ", " +
                "riding = " + player.getRiding() + ", " +
                "shear = " + player.getShear() + ", " +
                "shoot = " + player.getShoot() + ", " +
                "trade = " + player.getTrade() + ", " +
                "vehicle_destroy = " + player.getVehicleDestroy() + ", " +
                "harvest = " + player.getHarvest() + " " +
                "WHERE id = " + player.getId() + " " +
                "RETURNING *;";
        List<PlayerPrivilegeDTO> players = query(sql);
        if (players.size() == 0) return null;
        return players.get(0);
    }
}
