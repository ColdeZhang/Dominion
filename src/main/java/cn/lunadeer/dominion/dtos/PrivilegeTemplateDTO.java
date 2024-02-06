package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.utils.Database;
import cn.lunadeer.dominion.utils.XLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PrivilegeTemplateDTO {

    public static PrivilegeTemplateDTO insert(PrivilegeTemplateDTO privilege) {
        String sql = "INSERT INTO privilege_template (name, creator, group) " +
                "VALUES ('" +
                privilege.getName() + "', '" +
                privilege.getCreator().toString() + "', " +
                privilege.getGroup() + ") " +
                "RETURNING *";
        List<PrivilegeTemplateDTO> templates = query(sql);
        if (templates.size() == 0) return null;
        return templates.get(0);
    }

    public static List<PrivilegeTemplateDTO> selectAll() {
        String sql = "SELECT * FROM privilege_template";
        return query(sql);
    }

    public static List<PrivilegeTemplateDTO> searchGroup(String name){
        String sql = "SELECT * FROM privilege_template WHERE name LIKE '%" + name + "%' AND group = true";
        return query(sql);
    }

    public static List<PrivilegeTemplateDTO> searchGroup(UUID creator){
        String sql = "SELECT * FROM privilege_template WHERE creator = '" + creator.toString() + "' AND group = true";
        return query(sql);
    }

    public static PrivilegeTemplateDTO select(Integer id) {
        String sql = "SELECT * FROM privilege_template WHERE id = " + id;
        List<PrivilegeTemplateDTO> templates = query(sql);
        if (templates.size() == 0) return null;
        return templates.get(0);
    }

    public static PrivilegeTemplateDTO select(UUID creator, String name) {
        String sql = "SELECT * FROM privilege_template WHERE creator = '" + creator.toString() + "' AND name = '" + name + "'";
        List<PrivilegeTemplateDTO> templates = query(sql);
        if (templates.size() == 0) return null;
        return templates.get(0);
    }

    public static List<PrivilegeTemplateDTO> search(String name) {
        String sql = "SELECT * FROM privilege_template WHERE name LIKE '%" + name + "%'";
        return query(sql);
    }

    public static List<PrivilegeTemplateDTO> search(UUID creator) {
        String sql = "SELECT * FROM privilege_template WHERE creator = '" + creator.toString() + "'";
        return query(sql);
    }

    public static void delete(PrivilegeTemplateDTO privilege) {
        String sql = "DELETE FROM privilege_template WHERE id = " + privilege.getId();
        query(sql);
    }

    public static void delete(UUID creator, String name) {
        String sql = "DELETE FROM privilege_template WHERE creator = '" + creator.toString() + "' AND name = '" + name + "'";
        query(sql);
    }

    private static PrivilegeTemplateDTO update(PrivilegeTemplateDTO privilege) {
        String sql = "UPDATE privilege_template SET " +
                "name = '" + privilege.getName() + "', " +
                "creator = '" + privilege.getCreator().toString() + "', " +
                "group = " + privilege.getGroup() + ", " +
                "anchor = " + privilege.getAnchor() + ", " +
                "animal_killing = " + privilege.getAnimalKilling() + ", " +
                "anvil = " + privilege.getAnvil() + ", " +
                "beacon = " + privilege.getBeacon() + ", " +
                "bed = " + privilege.getBed() + ", " +
                "brew = " + privilege.getBrew() + ", " +
                "button = " + privilege.getButton() + ", " +
                "cake = " + privilege.getCake() + ", " +
                "container = " + privilege.getContainer() + ", " +
                "craft = " + privilege.getCraft() + ", " +
                "diode = " + privilege.getDiode() + ", " +
                "door = " + privilege.getDoor() + ", " +
                "dye = " + privilege.getDye() + ", " +
                "egg = " + privilege.getEgg() + ", " +
                "enchant = " + privilege.getEnchant() + ", " +
                "ender_pearl = " + privilege.getEnderPearl() + ", " +
                "feed = " + privilege.getFeed() + ", " +
                "glow = " + privilege.getGlow() + ", " +
                "honey = " + privilege.getHoney() + ", " +
                "hook = " + privilege.getHook() + ", " +
                "ignite = " + privilege.getIgnite() + ", " +
                "mob_killing = " + privilege.getMobKilling() + ", " +
                "move = " + privilege.getMove() + ", " +
                "place = " + privilege.getPlace() + ", " +
                "pressure = " + privilege.getPressure() + ", " +
                "riding = " + privilege.getRiding() + ", " +
                "shear = " + privilege.getShear() + ", " +
                "shoot = " + privilege.getShoot() + ", " +
                "trade = " + privilege.getTrade() + ", " +
                "vehicle_destroy = " + privilege.getVehicleDestroy() + ", " +
                "harvest = " + privilege.getHarvest() + " " +
                "WHERE id = " + privilege.getId() + " " +
                "RETURNING *";
        List<PrivilegeTemplateDTO> templates = query(sql);
        if (templates.size() == 0) return null;
        return templates.get(0);
    }

    private final Integer id;
    private String name;
    private final UUID creator;
    private final Boolean group;
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

    public String getName() {
        return name;
    }

    public UUID getCreator() {
        return creator;
    }

    public Boolean getGroup() {
        return group;
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

    public PrivilegeTemplateDTO setName(String name) {
        this.name = name;
        return update(this);
    }

    public PrivilegeTemplateDTO setAnchor(Boolean anchor) {
        this.anchor = anchor;
        return update(this);
    }

    public PrivilegeTemplateDTO setAnimalKilling(Boolean animalKilling) {
        this.animalKilling = animalKilling;
        return update(this);
    }

    public PrivilegeTemplateDTO setAnvil(Boolean anvil) {
        this.anvil = anvil;
        return update(this);
    }

    public PrivilegeTemplateDTO setBeacon(Boolean beacon) {
        this.beacon = beacon;
        return update(this);
    }

    public PrivilegeTemplateDTO setBed(Boolean bed) {
        this.bed = bed;
        return update(this);
    }

    public PrivilegeTemplateDTO setBrew(Boolean brew) {
        this.brew = brew;
        return update(this);
    }

    public PrivilegeTemplateDTO setButton(Boolean button) {
        this.button = button;
        return update(this);
    }

    public PrivilegeTemplateDTO setCake(Boolean cake) {
        this.cake = cake;
        return update(this);
    }

    public PrivilegeTemplateDTO setContainer(Boolean container) {
        this.container = container;
        return update(this);
    }

    public PrivilegeTemplateDTO setCraft(Boolean craft) {
        this.craft = craft;
        return update(this);
    }

    public PrivilegeTemplateDTO setDiode(Boolean diode) {
        this.diode = diode;
        return update(this);
    }

    public PrivilegeTemplateDTO setDoor(Boolean door) {
        this.door = door;
        return update(this);
    }

    public PrivilegeTemplateDTO setDye(Boolean dye) {
        this.dye = dye;
        return update(this);
    }

    public PrivilegeTemplateDTO setEgg(Boolean egg) {
        this.egg = egg;
        return update(this);
    }

    public PrivilegeTemplateDTO setEnchant(Boolean enchant) {
        this.enchant = enchant;
        return update(this);
    }

    public PrivilegeTemplateDTO setEnderPearl(Boolean enderPearl) {
        this.enderPearl = enderPearl;
        return update(this);
    }

    public PrivilegeTemplateDTO setFeed(Boolean feed) {
        this.feed = feed;
        return update(this);
    }

    public PrivilegeTemplateDTO setGlow(Boolean glow) {
        this.glow = glow;
        return update(this);
    }

    public PrivilegeTemplateDTO setHoney(Boolean honey) {
        this.honey = honey;
        return update(this);
    }

    public PrivilegeTemplateDTO setHook(Boolean hook) {
        this.hook = hook;
        return update(this);
    }

    public PrivilegeTemplateDTO setIgnite(Boolean ignite) {
        this.ignite = ignite;
        return update(this);
    }

    public PrivilegeTemplateDTO setMobKilling(Boolean mobKilling) {
        this.mobKilling = mobKilling;
        return update(this);
    }

    public PrivilegeTemplateDTO setMove(Boolean move) {
        this.move = move;
        return update(this);
    }

    public PrivilegeTemplateDTO setPlace(Boolean place) {
        this.place = place;
        return update(this);
    }

    public PrivilegeTemplateDTO setPressure(Boolean pressure) {
        this.pressure = pressure;
        return update(this);
    }

    public PrivilegeTemplateDTO setRiding(Boolean riding) {
        this.riding = riding;
        return update(this);
    }

    public PrivilegeTemplateDTO setShear(Boolean shear) {
        this.shear = shear;
        return update(this);
    }

    public PrivilegeTemplateDTO setShoot(Boolean shoot) {
        this.shoot = shoot;
        return update(this);
    }

    public PrivilegeTemplateDTO setTrade(Boolean trade) {
        this.trade = trade;
        return update(this);
    }

    public PrivilegeTemplateDTO setVehicleDestroy(Boolean vehicleDestroy) {
        this.vehicleDestroy = vehicleDestroy;
        return update(this);
    }

    public PrivilegeTemplateDTO setHarvest(Boolean harvest) {
        this.harvest = harvest;
        return update(this);
    }

    public PrivilegeTemplateDTO(String name, UUID creator, Boolean group) {
        this(null, name, creator, group,
                false, false, false,
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, false);
    }

    private PrivilegeTemplateDTO(Integer id, String name, UUID creator, Boolean group,
                                 Boolean anchor, Boolean animalKilling, Boolean anvil,
                                 Boolean beacon, Boolean bed, Boolean brew, Boolean button, Boolean cake,
                                 Boolean container, Boolean craft, Boolean diode, Boolean door, Boolean dye,
                                 Boolean egg, Boolean enchant, Boolean enderPearl, Boolean feed, Boolean glow,
                                 Boolean honey, Boolean hook, Boolean ignite, Boolean mobKilling, Boolean move,
                                 Boolean place, Boolean pressure, Boolean riding, Boolean shear, Boolean shoot,
                                 Boolean trade, Boolean vehicleDestroy, Boolean harvest) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.group = group;
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

    private static List<PrivilegeTemplateDTO> query(String sql) {
        List<PrivilegeTemplateDTO> privilegeTemplates = new ArrayList<>();
        try (ResultSet rs = Database.query(sql)) {
            if (rs == null) return privilegeTemplates;
            while (rs.next()) {
                PrivilegeTemplateDTO privilegeTemplate = new PrivilegeTemplateDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        UUID.fromString(rs.getString("creator")),
                        rs.getBoolean("group"),
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
                privilegeTemplates.add(privilegeTemplate);
            }
        } catch (SQLException e) {
            XLogger.err("Database query failed: " + e.getMessage());
            XLogger.err("SQL: " + sql);
        }
        return privilegeTemplates;
    }

}
