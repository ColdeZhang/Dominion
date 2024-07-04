package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.minecraftpluginutils.VaultConnect;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

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
        XLogger.setDebug(_debug);
        _db_type = _file.getString("Database.Type", "sqlite");
        _db_host = _file.getString("Database.Host", "localhost");
        _db_port = _file.getString("Database.Port", "5432");
        _db_name = _file.getString("Database.Name", "dominion");
        _db_user = _file.getString("Database.User", "postgres");
        _db_pass = _file.getString("Database.Pass", "postgres");
        _auto_create_radius = _file.getInt("AutoCreateRadius", 10);
        if (_auto_create_radius == 0) {
            XLogger.err("AutoCreateRadius 不能等于 0，已重置为 10");
            setAutoCreateRadius(10);
        }
        _limit_size_x = _file.getInt("Limit.SizeX", 128);
        if (_limit_size_x <= 4 && _limit_size_x != -1) {
            XLogger.err("Limit.SizeX 尺寸不能小于 4，已重置为 128");
            setLimitSizeX(128);
        }
        _limit_size_y = _file.getInt("Limit.SizeY", 64);
        if (_limit_size_y <= 4 && _limit_size_y != -1) {
            XLogger.err("Limit.SizeY 尺寸不能小于 4，已重置为 64");
            setLimitSizeY(64);
        }
        _limit_size_z = _file.getInt("Limit.SizeZ", 128);
        if (_limit_size_z <= 4 && _limit_size_z != -1) {
            XLogger.err("Limit.SizeZ 尺寸不能小于 4，已重置为 128");
            setLimitSizeZ(128);
        }
        _blue_map = _file.getBoolean("BlueMap", true);
        _auto_clean_after_days = _file.getInt("AutoCleanAfterDays", 180);
        if (_auto_clean_after_days == 0) {
            XLogger.err("AutoCleanAfterDays 不能等于 0，已重置为 180");
            setAutoCleanAfterDays(180);
        }
        _limit_min_y = _file.getInt("Limit.MinY", -64);
        _limit_max_y = _file.getInt("Limit.MaxY", 320);
        if (_limit_min_y >= _limit_max_y) {
            XLogger.err("Limit.MinY 不能大于或等于 Limit.MaxY，已重置为 -64 320");
            setLimitMinY(-64);
            setLimitMaxY(320);
        }
        _limit_amount = _file.getInt("Limit.Amount", 10);
        _limit_depth = _file.getInt("Limit.Depth", 10);
        _limit_vert = _file.getBoolean("Limit.Vert", false);
        if (_limit_vert && _limit_size_y <= _limit_max_y - _limit_min_y) {
            setLimitSizeY(_limit_max_y - _limit_min_y + 1);
            XLogger.warn("启用 Limit.Vert 时 Limit.SizeY 不能小于 Limit.MaxY - Limit.MinY，已自动调整为 " + (_limit_max_y - _limit_min_y + 1));
        }
        _limit_op_bypass = _file.getBoolean("Limit.OpByPass", true);
        _world_black_list = _file.getStringList("Limit.WorldBlackList");
        _check_update = _file.getBoolean("CheckUpdate", true);
        _tp_enable = _file.getBoolean("Teleport.Enable", false);
        _tp_delay = _file.getInt("Teleport.Delay", 0);
        _tp_cool_down = _file.getInt("Teleport.CoolDown", 0);
        _tool = _file.getString("Tool", "ARROW");
        if (Material.getMaterial(_tool) == null) {
            XLogger.err("工具名称设置错误，已重置为 ARROW");
            setTool("ARROW");
        }
        _economy_enable = _file.getBoolean("Economy.Enable", false);
        _economy_price = (float) _file.getDouble("Economy.Price", 10.0);
        _economy_only_xz = _file.getBoolean("Economy.OnlyXZ", false);
        _economy_refund = (float) _file.getDouble("Economy.Refund", 0.85);
        if (getEconomyEnable()) {
            new VaultConnect(this._plugin);
        }
        _fly_permission_nodes = _file.getStringList("FlyPermissionNodes");
        _residence_migration = _file.getBoolean("ResidenceMigration", false);
        saveAll();  // 回写文件 防止文件中的数据不完整
        Flag.loadFromJson();
    }

    public void saveAll() {
        _file.set("AutoCreateRadius", _auto_create_radius);
        _file.set("Limit.SizeX", _limit_size_x);
        _file.set("Limit.SizeY", _limit_size_y);
        _file.set("Limit.SizeZ", _limit_size_z);
        _file.set("BlueMap", _blue_map);
        _file.set("AutoCleanAfterDays", _auto_clean_after_days);
        _file.set("Limit.MinY", _limit_min_y);
        _file.set("Limit.MaxY", _limit_max_y);
        _file.set("Limit.Amount", _limit_amount);
        _file.set("Limit.Depth", _limit_depth);
        _file.set("Limit.Vert", _limit_vert);
        _file.set("Limit.OpByPass", _limit_op_bypass);
        _file.set("Limit.WorldBlackList", _world_black_list);
        _file.set("CheckUpdate", _check_update);
        _file.set("Teleport.Enable", _tp_enable);
        _file.set("Teleport.Delay", _tp_delay);
        _file.set("Teleport.CoolDown", _tp_cool_down);
        _file.set("Tool", _tool);
        _file.set("Economy.Enable", _economy_enable);
        _file.set("Economy.Price", _economy_price);
        _file.set("Economy.OnlyXZ", _economy_only_xz);
        _file.set("Economy.Refund", _economy_refund);
        _file.set("FlyPermissionNodes", _fly_permission_nodes);
        _file.set("ResidenceMigration", _residence_migration);
        _plugin.saveConfig();
    }

    public Boolean isDebug() {
        return _debug;
    }

    public void setDebug(Boolean debug) {
        _debug = debug;
        _file.set("Debug", debug);
        _plugin.saveConfig();
        XLogger.setDebug(debug);
    }

    public String getDbType() {
        return _db_type;
    }

    public void setDbType(String db_type) {
        _db_type = db_type;
        _file.set("Database.Type", db_type);
        _plugin.saveConfig();
    }

    public String getDbHost() {
        return _db_host;
    }

    public String getDbPort() {
        return _db_port;
    }

    public String getDbName() {
        return _db_name;
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
        return _db_pass;
    }

    public Integer getLimitSizeX() {
        return _limit_size_x;
    }

    public void setLimitSizeX(Integer max_x) {
        _limit_size_x = max_x;
        _file.set("Limit.SizeX", max_x);
        _plugin.saveConfig();
    }

    public Integer getLimitSizeY() {
        return _limit_size_y;
    }

    public void setLimitSizeY(Integer max_y) {
        _limit_size_y = max_y;
        _file.set("Limit.SizeY", max_y);
        _plugin.saveConfig();
    }

    public Integer getLimitSizeZ() {
        return _limit_size_z;
    }

    public void setLimitSizeZ(Integer max_z) {
        _limit_size_z = max_z;
        _file.set("Limit.SizeZ", max_z);
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

    public Integer getAutoCleanAfterDays() {
        return _auto_clean_after_days;
    }

    public void setAutoCleanAfterDays(Integer auto_clean_after_days) {
        _auto_clean_after_days = auto_clean_after_days;
        _file.set("AutoCleanAfterDays", auto_clean_after_days);
        _plugin.saveConfig();
    }

    public Integer getLimitMinY() {
        return _limit_min_y;
    }

    public void setLimitMinY(Integer limit_bottom) {
        _limit_min_y = limit_bottom;
        _file.set("Limit.MinY", limit_bottom);
        _plugin.saveConfig();
    }

    public Integer getLimitMaxY() {
        return _limit_max_y;
    }

    public void setLimitMaxY(Integer limit_top) {
        _limit_max_y = limit_top;
        _file.set("Limit.MaxY", limit_top);
        _plugin.saveConfig();
    }

    public Integer getLimitAmount() {
        return _limit_amount;
    }

    public void setLimitAmount(Integer limit_amount) {
        _limit_amount = limit_amount;
        _file.set("Limit.Amount", limit_amount);
        _plugin.saveConfig();
    }

    public Integer getLimitDepth() {
        return _limit_depth;
    }

    public void setLimitDepth(Integer limit_depth) {
        _limit_depth = limit_depth;
        _file.set("Limit.Depth", limit_depth);
        _plugin.saveConfig();
    }

    public Boolean getLimitVert() {
        return _limit_vert;
    }

    public void setLimitVert(Boolean limit_vert) {
        _limit_vert = limit_vert;
        _file.set("Limit.Vert", limit_vert);
        _plugin.saveConfig();
    }

    public List<String> getWorldBlackList() {
        return _world_black_list;
    }

    public Boolean getLimitOpBypass() {
        return _limit_op_bypass;
    }

    public void setLimitOpBypass(Boolean limit_op_bypass) {
        _limit_op_bypass = limit_op_bypass;
        _file.set("Limit.OpByPass", limit_op_bypass);
        _plugin.saveConfig();
    }

    public Boolean getCheckUpdate() {
        return _check_update;
    }

    public Boolean getTpEnable() {
        return _tp_enable;
    }

    public void setTpEnable(Boolean tp_enable) {
        _tp_enable = tp_enable;
        _file.set("Teleport.Enable", tp_enable);
        _plugin.saveConfig();
    }

    public Integer getTpDelay() {
        return _tp_delay;
    }

    public void setTpDelay(Integer tp_delay) {
        _tp_delay = tp_delay;
        _file.set("Teleport.Delay", tp_delay);
        _plugin.saveConfig();
    }

    public Integer getTpCoolDown() {
        return _tp_cool_down;
    }

    public void setTpCoolDown(Integer tp_cool_down) {
        _tp_cool_down = tp_cool_down;
        _file.set("Teleport.CoolDown", tp_cool_down);
        _plugin.saveConfig();
    }

    public Material getTool() {
        return Material.getMaterial(_tool);
    }

    public void setTool(String tool) {
        _tool = tool;
        _file.set("Tool", tool);
        _plugin.saveConfig();
    }

    public Boolean getEconomyEnable() {
        return _economy_enable;
    }

    public void setEconomyEnable(Boolean economy_enable) {
        _economy_enable = economy_enable;
        _file.set("Economy.Enable", economy_enable);
        _plugin.saveConfig();
    }

    public Float getEconomyPrice() {
        return _economy_price;
    }

    public void setEconomyPrice(Float economy_price) {
        _economy_price = economy_price;
        _file.set("Economy.Price", economy_price);
        _plugin.saveConfig();
    }

    public Boolean getEconomyOnlyXZ() {
        return _economy_only_xz;
    }

    public void setEconomyOnlyXZ(Boolean economy_only_xz) {
        _economy_only_xz = economy_only_xz;
        _file.set("Economy.OnlyXZ", economy_only_xz);
        _plugin.saveConfig();
    }

    public Float getEconomyRefund() {
        return _economy_refund;
    }

    public void setEconomyRefund(Float economy_refund) {
        _economy_refund = economy_refund;
        _file.set("Economy.Refund", economy_refund);
        _plugin.saveConfig();
    }

    public List<String> getFlyPermissionNodes() {
        return _fly_permission_nodes;
    }

    public void setFlyPermissionNodes(List<String> fly_permission_nodes) {
        _fly_permission_nodes = fly_permission_nodes;
        _file.set("FlyPermissionNodes", fly_permission_nodes);
        _plugin.saveConfig();
    }

    public Boolean getResidenceMigration() {
        return _residence_migration;
    }

    public void setResidenceMigration(Boolean residence_migration) {
        _residence_migration = residence_migration;
        _file.set("ResidenceMigration", residence_migration);
        _plugin.saveConfig();
    }

    private final Dominion _plugin;
    private FileConfiguration _file;
    private Boolean _debug;

    private String _db_type;
    private String _db_host;
    private String _db_port;
    private String _db_user;
    private String _db_pass;
    private String _db_name;

    private Integer _auto_create_radius;

    private Integer _limit_size_x;
    private Integer _limit_size_y;
    private Integer _limit_size_z;
    private Boolean _limit_op_bypass;

    private Boolean _blue_map;
    private Integer _auto_clean_after_days;
    private Integer _limit_min_y;
    private Integer _limit_max_y;
    private Integer _limit_amount;
    private Integer _limit_depth;
    private Boolean _limit_vert;
    private List<String> _world_black_list;
    private Boolean _check_update;

    private Boolean _tp_enable;
    private Integer _tp_delay;
    private Integer _tp_cool_down;
    private String _tool;

    private Boolean _economy_enable;
    private Float _economy_price;
    private Boolean _economy_only_xz;
    private Float _economy_refund;
    private List<String> _fly_permission_nodes;
    private Boolean _residence_migration;
}
