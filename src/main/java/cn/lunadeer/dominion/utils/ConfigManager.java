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
        _db_name = _file.getString("Database.Name", "miniplayertitle");
        _db_user = _file.getString("Database.User", "postgres");
        _db_pass = _file.getString("Database.Pass", "postgres");
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


    private final Dominion _plugin;
    private FileConfiguration _file;
    private Boolean _debug;

    private String _db_host;
    private String _db_port;
    private String _db_user;
    private String _db_pass;
    private String _db_name;
}
