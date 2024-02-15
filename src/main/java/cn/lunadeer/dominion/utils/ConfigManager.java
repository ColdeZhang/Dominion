package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Dominion;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    public ConfigManager(Dominion plugin) {
        _plugin = plugin;
        _plugin.saveDefaultConfig();
        reload();
        _plugin.saveConfig();
    }

    public void reload() {
        _plugin.reloadConfig();
        _file = _plugin.getConfig();
        _debug = _file.getBoolean("Debug", false);
        _db_host = _file.getString("Database.Host", "localhost");
        _db_port = _file.getString("Database.Port", "5432");
        _db_name = _file.getString("Database.Name", "dominion");
        _db_user = _file.getString("Database.User", "postgres");
        _db_pass = _file.getString("Database.Pass", "postgres");
        _auto_create_radius = _file.getInt("AutoCreateRadius", 10);
        _max_x = _file.getInt("MaxX", 128);
        _max_y = _file.getInt("MaxY", 64);
        _max_z = _file.getInt("MaxZ", 128);
        _blue_map = _file.getBoolean("BlueMap", true);
    }

    public Boolean isDebug() {
        return _debug;
    }

    public void setDebug(Boolean debug) {
        _debug = debug;
        _file.set("Debug", debug);
        _plugin.saveConfig();
    }

    public String getDBConnectionUrl() {
        return "jdbc:postgresql://" + _db_host + ":" + _db_port + "/" + _db_name;
    }


    public void setDbUser(String db_user) {
        _db_user = db_user;
        _file.set("Database.User", db_user);
        _plugin.saveConfig();
    }

    public String getDbUser() {
        if (_db_user.contains("@")) {
            setDbUser("'" + _db_user + "'");
        }
        return _db_user;
    }

    public void setDbPass(String db_pass) {
        _db_pass = db_pass;
        _file.set("Database.Pass", db_pass);
        _plugin.saveConfig();
    }

    public String getDbPass() {
        if (_db_pass.contains("@")) {
            setDbPass("'" + _db_pass + "'");
        }
        return _db_pass;
    }

    public Integer getMaxX() {
        return _max_x;
    }

    public void setMaxX(Integer max_x) {
        _max_x = max_x;
        _file.set("MaxX", max_x);
        _plugin.saveConfig();
    }

    public Integer getMaxY() {
        return _max_y;
    }

    public void setMaxY(Integer max_y) {
        _max_y = max_y;
        _file.set("MaxY", max_y);
        _plugin.saveConfig();
    }

    public Integer getMaxZ() {
        return _max_z;
    }

    public void setMaxZ(Integer max_z) {
        _max_z = max_z;
        _file.set("MaxZ", max_z);
        _plugin.saveConfig();
    }

    public Integer getAutoCreateRadius() {
        return _auto_create_radius;
    }

    public void setAutoCreateRadius(Integer radius) {
        _auto_create_radius = radius;
        _file.set("AutoCreateRadius", radius);
        _plugin.saveConfig();
    }

    public Boolean getBlueMap() {
        return _blue_map;
    }

    public void setBlueMap(Boolean blue_map) {
        _blue_map = blue_map;
        _file.set("BlueMap", blue_map);
        _plugin.saveConfig();
    }


    private final Dominion _plugin;
    private FileConfiguration _file;
    private Boolean _debug;

    private String _db_host;
    private String _db_port;
    private String _db_user;
    private String _db_pass;
    private String _db_name;

    private Integer _auto_create_radius;

    private Integer _max_x;
    private Integer _max_y;
    private Integer _max_z;

    private Boolean _blue_map;
}
