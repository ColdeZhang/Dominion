package cn.lunadeer.dominion.managers;

import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupLimit {
    private final JavaPlugin plugin;
    private final File file_path;
    private Integer min_y;
    private Integer max_y;
    private Integer size_x;
    private Integer size_y;
    private Integer size_z;
    private Integer amount;
    private Integer depth;
    private Boolean vert;
    private List<String> world_black_list;
    private Double price;
    private Boolean only_xz;
    private Double refund;

    public GroupLimit(JavaPlugin plugin, File filePath) {
        this.plugin = plugin;
        this.file_path = filePath;
    }

    public Integer getLimitMinY() {
        return min_y;
    }

    public Integer getLimitMaxY() {
        return max_y;
    }

    public Integer getLimitSizeX() {
        return size_x;
    }

    public Integer getLimitSizeY() {
        return size_y;
    }

    public Integer getLimitSizeZ() {
        return size_z;
    }

    public Integer getLimitAmount() {
        return amount;
    }

    public Integer getLimitDepth() {
        return depth;
    }

    public Boolean getLimitVert() {
        return vert;
    }

    public List<String> getWorldBlackList() {
        return world_black_list;
    }

    public Double getPrice() {
        return price;
    }

    public Boolean getPriceOnlyXZ() {
        return only_xz;
    }

    public Double getRefundRatio() {
        return refund;
    }

    public void setLimitMinY(Integer min_y) {
        this.min_y = min_y;
    }

    public void setLimitMaxY(Integer max_y) {
        this.max_y = max_y;
    }

    public void setLimitSizeX(Integer size_x) {
        this.size_x = size_x;
    }

    public void setLimitSizeY(Integer size_y) {
        this.size_y = size_y;
    }

    public void setLimitSizeZ(Integer size_z) {
        this.size_z = size_z;
    }

    public void setLimitAmount(Integer amount) {
        this.amount = amount;
    }

    public void setLimitDepth(Integer depth) {
        this.depth = depth;
    }

    public void setLimitVert(Boolean vert) {
        this.vert = vert;
    }

    public void setWorldBlackList(List<String> world_black_list) {
        this.world_black_list = world_black_list;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setPriceOnlyXZ(Boolean only_xz) {
        this.only_xz = only_xz;
    }

    public void setRefundRatio(Double refund) {
        this.refund = refund;
    }

    private static GroupLimit loadGroup(JavaPlugin plugin, File file) {
        GroupLimit group = new GroupLimit(plugin, file);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        group.setLimitMinY(config.getInt("MinY", -64));
        group.setLimitMaxY(config.getInt("MaxY", 320));
        if (group.getLimitMinY() >= group.getLimitMaxY()) {
            XLogger.err("权限组 " + file.getName() + " 的 MinY 不能大于等于 MaxY，已重置为 -64 和 320");
            group.setLimitMinY(-64);
            group.setLimitMaxY(320);
        }
        group.setLimitSizeX(config.getInt("SizeX", 128));
        if (group.getLimitSizeX() <= 4 && group.getLimitSizeX() != -1) {
            XLogger.err("权限组 " + file.getName() + " 的 SizeX 设置过小，已重置为 128");
            group.setLimitSizeX(128);
        }
        group.setLimitSizeY(config.getInt("SizeY", 64));
        if (group.getLimitSizeY() <= 4 && group.getLimitSizeY() != -1) {
            XLogger.err("权限组 " + file.getName() + " 的 SizeY 设置过小，已重置为 64");
            group.setLimitSizeY(64);
        }
        group.setLimitSizeZ(config.getInt("SizeZ", 128));
        if (group.getLimitSizeZ() <= 4 && group.getLimitSizeZ() != -1) {
            XLogger.err("权限组 " + file.getName() + " 的 SizeZ 设置过小，已重置为 128");
            group.setLimitSizeZ(128);
        }
        group.setLimitAmount(config.getInt("Amount", 10));
        group.setLimitDepth(config.getInt("Depth", 3));
        group.setLimitVert(config.getBoolean("Vert", false));
        group.setWorldBlackList(config.getStringList("WorldBlackList"));
        group.setPrice(config.getDouble("Price", 10.0));
        group.setPriceOnlyXZ(config.getBoolean("OnlyXZ", false));
        group.setRefundRatio(config.getDouble("Refund", 0.85));
        if (group.getRefundRatio() < 0.0 || group.getRefundRatio() > 1.0) {
            XLogger.err("权限组 " + file.getName() + " 的 Refund 设置不合法，已重置为 0.85");
            group.setRefundRatio(0.85);
        }
        group.save();
        return group;
    }

    private void save() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("MinY", min_y);
        config.set("MaxY", max_y);
        config.set("SizeX", size_x);
        config.set("SizeY", size_y);
        config.set("SizeZ", size_z);
        config.set("Amount", amount);
        config.set("Depth", depth);
        config.set("Vert", vert);
        config.set("WorldBlackList", world_black_list);
        config.set("Price", price);
        config.set("OnlyXZ", only_xz);
        config.set("Refund", refund);
        try {
            config.save(file_path);
        } catch (Exception e) {
            XLogger.err("Failed to save group limit file: " + file_path.getName());
        }
    }


    public static Map<String, GroupLimit> loadGroups(JavaPlugin plugin) {
        Map<String, GroupLimit> groups = new HashMap<>();
        File root = plugin.getDataFolder();
        File groupsDir = new File(root, "groups");
        if (!groupsDir.exists()) {
            // 创建文件夹 并且从jar中复制文件
            plugin.saveResource("groups/sponsor.yml", true);
        }
        File[] files = groupsDir.listFiles();
        if (files == null) {
            return groups;
        }
        for (File file : files) {
            String name = file.getName();
            if (!name.endsWith(".yml")) {
                continue;
            }
            String groupName = name.substring(0, name.length() - 4);
            GroupLimit group = GroupLimit.loadGroup(plugin, file);
            groups.put(groupName, group);
        }
        return groups;
    }
}
