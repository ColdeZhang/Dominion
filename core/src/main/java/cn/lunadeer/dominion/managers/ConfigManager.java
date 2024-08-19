package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.minecraftpluginutils.VaultConnect.VaultConnect;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        _timer = _file.getBoolean("Timer", false);
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
        _spawn_protection = _file.getInt("Limit.SpawnProtection", 10);
        _blue_map = _file.getBoolean("BlueMap", false);
        _dynmap = _file.getBoolean("Dynmap", false);
        _auto_clean_after_days = _file.getInt("AutoCleanAfterDays", 180);
        if (_auto_clean_after_days == 0) {
            XLogger.err("AutoCleanAfterDays 不能等于 0，已重置为 180");
            setAutoCleanAfterDays(180);
        }
        _limit_op_bypass = _file.getBoolean("Limit.OpByPass", true);
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
        if (getEconomyEnable()) {
            new VaultConnect(this._plugin);
        }
        _fly_permission_nodes = _file.getStringList("FlyPermissionNodes");
        _residence_migration = _file.getBoolean("ResidenceMigration", false);
        _group_title_enable = _file.getBoolean("GroupTitle.Enable", false);
        _group_title_prefix = _file.getString("GroupTitle.Prefix", "&#ffffff[");
        _group_title_suffix = _file.getString("GroupTitle.Suffix", "&#ffffff]");

        GroupLimit defaultGroup = new GroupLimit();
        defaultGroup.setLimitSizeX(_file.getInt("Limit.SizeX", 128));
        defaultGroup.setLimitSizeY(_file.getInt("Limit.SizeY", 64));
        defaultGroup.setLimitSizeZ(_file.getInt("Limit.SizeZ", 128));
        defaultGroup.setLimitMinY(_file.getInt("Limit.MinY", -64));
        defaultGroup.setLimitMaxY(_file.getInt("Limit.MaxY", 320));
        defaultGroup.setLimitAmount(_file.getInt("Limit.Amount", 10));
        defaultGroup.setLimitDepth(_file.getInt("Limit.Depth", 3));
        defaultGroup.setLimitVert(_file.getBoolean("Limit.Vert", false));
        defaultGroup.setWorldBlackList(_file.getStringList("Limit.WorldBlackList"));
        defaultGroup.setPrice(_file.getDouble("Economy.Price", 10.0));
        defaultGroup.setPriceOnlyXZ(_file.getBoolean("Economy.OnlyXZ", false));
        defaultGroup.setRefundRatio(_file.getDouble("Economy.Refund", 0.85));
        limits.put("default", defaultGroup);
        if (defaultGroup.getLimitSizeX() <= 4 && defaultGroup.getLimitSizeX() != -1) {
            XLogger.err("Limit.SizeX 尺寸不能小于 4，已重置为 128");
            setLimitSizeX(128);
        }
        if (defaultGroup.getLimitSizeY() <= 4 && defaultGroup.getLimitSizeY() != -1) {
            XLogger.err("Limit.SizeY 尺寸不能小于 4，已重置为 64");
            setLimitSizeY(64);
        }
        if (defaultGroup.getLimitSizeZ() <= 4 && defaultGroup.getLimitSizeZ() != -1) {
            XLogger.err("Limit.SizeZ 尺寸不能小于 4，已重置为 128");
            setLimitSizeZ(128);
        }
        if (defaultGroup.getLimitMinY() >= defaultGroup.getLimitMaxY()) {
            XLogger.err("Limit.MinY 不能大于或等于 Limit.MaxY，已重置为 -64 320");
            setLimitMinY(-64);
            setLimitMaxY(320);
        }
        if (defaultGroup.getRefundRatio() < 0.0 || defaultGroup.getRefundRatio() > 1.0) {
            XLogger.err("Economy.Refund 设置不合法，已重置为 0.85");
            setEconomyRefund(0.85f);
        }
        if (defaultGroup.getPrice() < 0.0) {
            XLogger.err("Economy.Price 设置不合法，已重置为 10.0");
            setEconomyPrice(10.0f);
        }
        if (defaultGroup.getLimitVert() && defaultGroup.getLimitSizeY() <= defaultGroup.getLimitMaxY() - defaultGroup.getLimitMinY()) {
            XLogger.warn("启用 Limit.Vert 时 Limit.SizeY 不能小于 Limit.MaxY - Limit.MinY，已自动调整为 " + (defaultGroup.getLimitMaxY() - defaultGroup.getLimitMinY() + 1));
            setLimitSizeY(defaultGroup.getLimitMaxY() - defaultGroup.getLimitMinY() + 1);
        }
        if (defaultGroup.getLimitAmount() < 0 && defaultGroup.getLimitAmount() != -1) {
            XLogger.err("Limit.Amount 设置不合法，已重置为 10");
            setLimitAmount(10);
        }
        if (defaultGroup.getLimitDepth() < 0 && defaultGroup.getLimitDepth() != -1) {
            XLogger.err("Limit.Depth 设置不合法，已重置为 3");
            setLimitDepth(3);
        }

        limits.putAll(GroupLimit.loadGroups(_plugin));

        saveAll();  // 回写文件 防止文件中的数据不完整
        Flag.loadFromJson();    // 加载 Flag 配置
    }

    public void saveAll() {
        // 删除旧文件
        new File(_plugin.getDataFolder(), "config.yml").delete();
        // 保存新文件
        _plugin.saveDefaultConfig();
        // 重新加载
        _plugin.reloadConfig();
        _file = _plugin.getConfig();

        // 保存配置
        _file.set("Database.Type", _db_type);
        _file.set("Database.Host", _db_host);
        _file.set("Database.Port", _db_port);
        _file.set("Database.Name", _db_name);
        _file.set("Database.User", _db_user);
        _file.set("Database.Pass", _db_pass);

        _file.set("AutoCreateRadius", _auto_create_radius);

        _file.set("Limit.SpawnProtection", _spawn_protection);
        _file.set("Limit.MinY", limits.get("default").getLimitMinY());
        _file.set("Limit.MaxY", limits.get("default").getLimitMaxY());
        _file.set("Limit.SizeX", limits.get("default").getLimitSizeX());
        _file.set("Limit.SizeY", limits.get("default").getLimitSizeY());
        _file.set("Limit.SizeZ", limits.get("default").getLimitSizeZ());
        _file.set("Limit.Amount", limits.get("default").getLimitAmount());
        _file.set("Limit.Depth", limits.get("default").getLimitDepth());
        _file.set("Limit.Vert", limits.get("default").getLimitVert());
        _file.set("Limit.WorldBlackList", limits.get("default").getWorldBlackList());
        _file.set("Limit.OpByPass", _limit_op_bypass);

        _file.set("Teleport.Enable", _tp_enable);
        _file.set("Teleport.Delay", _tp_delay);
        _file.set("Teleport.CoolDown", _tp_cool_down);

        _file.set("AutoCleanAfterDays", _auto_clean_after_days);

        _file.set("Tool", _tool);

        _file.set("Economy.Enable", _economy_enable);
        _file.set("Economy.Price", limits.get("default").getPrice());
        _file.set("Economy.OnlyXZ", limits.get("default").getPriceOnlyXZ());
        _file.set("Economy.Refund", limits.get("default").getRefundRatio());

        _file.set("FlyPermissionNodes", _fly_permission_nodes);

        _file.set("ResidenceMigration", _residence_migration);

        _file.set("GroupTitle.Enable", _group_title_enable);
        _file.set("GroupTitle.Prefix", _group_title_prefix);
        _file.set("GroupTitle.Suffix", _group_title_suffix);

        _file.set("BlueMap", _blue_map);
        _file.set("Dynmap", _dynmap);

        _file.set("CheckUpdate", _check_update);

        _file.set("Debug", _debug);
        _file.set("Timer", _timer);

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

    public Boolean TimerEnabled() {
        return _timer;
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

    public Integer getLimitSizeX(Player player) {
        return limits.get(getPlayerGroup(player)).getLimitSizeX();
    }

    public void setLimitSizeX(Integer max_x) {
        limits.get("default").setLimitSizeX(max_x);
        _file.set("Limit.SizeX", max_x);
        _plugin.saveConfig();
    }

    public Integer getLimitSizeY(Player player) {
        return limits.get(getPlayerGroup(player)).getLimitSizeY();
    }

    public void setLimitSizeY(Integer max_y) {
        limits.get("default").setLimitSizeY(max_y);
        _file.set("Limit.SizeY", max_y);
        _plugin.saveConfig();
    }

    public Integer getLimitSizeZ(Player player) {
        return limits.get(getPlayerGroup(player)).getLimitSizeZ();
    }

    public void setLimitSizeZ(Integer max_z) {
        limits.get("default").setLimitSizeZ(max_z);
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

    public Boolean getDynmap() {
        return _dynmap;
    }

    public Integer getAutoCleanAfterDays() {
        return _auto_clean_after_days;
    }

    public void setAutoCleanAfterDays(Integer auto_clean_after_days) {
        _auto_clean_after_days = auto_clean_after_days;
        _file.set("AutoCleanAfterDays", auto_clean_after_days);
        _plugin.saveConfig();
    }

    public Integer getLimitMinY(Player player) {
        return limits.get(getPlayerGroup(player)).getLimitMinY();
    }

    public void setLimitMinY(Integer limit_bottom) {
        limits.get("default").setLimitMinY(limit_bottom);
        _file.set("Limit.MinY", limit_bottom);
        _plugin.saveConfig();
    }

    public Integer getLimitMaxY(Player player) {
        return limits.get(getPlayerGroup(player)).getLimitMaxY();
    }

    public void setLimitMaxY(Integer limit_top) {
        limits.get("default").setLimitMaxY(limit_top);
        _file.set("Limit.MaxY", limit_top);
        _plugin.saveConfig();
    }

    public Integer getLimitAmount(Player player) {
        return limits.get(getPlayerGroup(player)).getLimitAmount();
    }

    public void setLimitAmount(Integer limit_amount) {
        limits.get("default").setLimitAmount(limit_amount);
        _file.set("Limit.Amount", limit_amount);
        _plugin.saveConfig();
    }

    public Integer getLimitDepth(Player player) {
        return limits.get(getPlayerGroup(player)).getLimitDepth();
    }

    public void setLimitDepth(Integer limit_depth) {
        limits.get("default").setLimitDepth(limit_depth);
        _file.set("Limit.Depth", limit_depth);
        _plugin.saveConfig();
    }

    public Boolean getLimitVert(Player player) {
        return limits.get(getPlayerGroup(player)).getLimitVert();
    }

    public void setLimitVert(Boolean limit_vert) {
        limits.get("default").setLimitVert(limit_vert);
        _file.set("Limit.Vert", limit_vert);
        _plugin.saveConfig();
    }

    public List<String> getWorldBlackList(Player player) {
        return limits.get(getPlayerGroup(player)).getWorldBlackList();
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

    public Float getEconomyPrice(Player player) {
        return limits.get(getPlayerGroup(player)).getPrice().floatValue();
    }

    public void setEconomyPrice(Float economy_price) {
        limits.get("default").setPrice((double) economy_price);
        _file.set("Economy.Price", economy_price);
        _plugin.saveConfig();
    }

    public Boolean getEconomyOnlyXZ(Player player) {
        return limits.get(getPlayerGroup(player)).getPriceOnlyXZ();
    }

    public void setEconomyOnlyXZ(Boolean economy_only_xz) {
        limits.get("default").setPriceOnlyXZ(economy_only_xz);
        _file.set("Economy.OnlyXZ", economy_only_xz);
        _plugin.saveConfig();
    }

    public Float getEconomyRefund(Player player) {
        return limits.get(getPlayerGroup(player)).getRefundRatio().floatValue();
    }

    public void setEconomyRefund(Float economy_refund) {
        limits.get("default").setRefundRatio((double) economy_refund);
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

    public Integer getSpawnProtection() {
        return _spawn_protection;
    }

    public void setSpawnProtection(Integer spawn_protection) {
        _spawn_protection = spawn_protection;
        _file.set("Limit.SpawnProtection", spawn_protection);
        _plugin.saveConfig();
    }

    public Boolean getGroupTitleEnable() {
        return _group_title_enable;
    }

    public void setGroupTitleEnable(Boolean group_title_enable) {
        _group_title_enable = group_title_enable;
        _file.set("GroupTitle.Enable", group_title_enable);
        _plugin.saveConfig();
    }

    public String getGroupTitlePrefix() {
        return _group_title_prefix;
    }

    public void setGroupTitlePrefix(String group_title_prefix) {
        _group_title_prefix = group_title_prefix;
        _file.set("GroupTitle.Prefix", group_title_prefix);
        _plugin.saveConfig();
    }

    public String getGroupTitleSuffix() {
        return _group_title_suffix;
    }

    public void setGroupTitleSuffix(String group_title_suffix) {
        _group_title_suffix = group_title_suffix;
        _file.set("GroupTitle.Suffix", group_title_suffix);
        _plugin.saveConfig();
    }

    private final Dominion _plugin;
    private FileConfiguration _file;
    private Boolean _debug;
    private Boolean _timer;

    private String _db_type;
    private String _db_host;
    private String _db_port;
    private String _db_user;
    private String _db_pass;
    private String _db_name;

    private Integer _auto_create_radius;

    private Boolean _limit_op_bypass;

    private Boolean _blue_map;
    private Boolean _dynmap;
    private Integer _auto_clean_after_days;

    private Boolean _check_update;

    private Boolean _tp_enable;
    private Integer _tp_delay;
    private Integer _tp_cool_down;
    private String _tool;

    private Boolean _economy_enable;

    private List<String> _fly_permission_nodes;
    private Boolean _residence_migration;
    private Integer _spawn_protection;

    private Boolean _group_title_enable;
    private String _group_title_prefix;
    private String _group_title_suffix;

    private final Map<String, GroupLimit> limits = new HashMap<>();

    private String getPlayerGroup(@Nullable Player player) {
        if (player == null) {
            return "default";
        }
        for (String group : limits.keySet()) {
            if (group.equals("default")) {
                continue;
            }
            if (player.hasPermission("group." + group)) {
                return group;
            }
        }
        return "default";
    }
}
