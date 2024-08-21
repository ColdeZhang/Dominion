package cn.lunadeer.dominion.managers;

import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupLimit {
    private final  YamlConfiguration config;
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

    public GroupLimit() {
        this.file_path = null;
        this.config = new YamlConfiguration();
    }

    public GroupLimit(File filePath) {
        this.file_path = filePath;
        config = YamlConfiguration.loadConfiguration(this.file_path);
        setLimitMinY(config.getInt("MinY", -64));
        setLimitMaxY(config.getInt("MaxY", 320));
        if (getLimitMinY() >= getLimitMaxY()) {
            XLogger.err("权限组 %s 的 MinY 不能大于等于 MaxY，已重置为 -64 和 320", this.file_path.getName());
            setLimitMinY(-64);
            setLimitMaxY(320);
        }
        setLimitSizeX(config.getInt("SizeX", 128));
        if (getLimitSizeX() <= 4 && getLimitSizeX() != -1) {
            XLogger.err("权限组 %s 的 SizeX 设置过小，已重置为 128", this.file_path.getName());
            setLimitSizeX(128);
        }
        setLimitSizeY(config.getInt("SizeY", 64));
        if (getLimitSizeY() <= 4 && getLimitSizeY() != -1) {
            XLogger.err("权限组 %s 的 SizeY 设置过小，已重置为 64", this.file_path.getName());
            setLimitSizeY(64);
        }
        setLimitSizeZ(config.getInt("SizeZ", 128));
        if (getLimitSizeZ() <= 4 && getLimitSizeZ() != -1) {
            XLogger.err("权限组 %s 的 SizeZ 设置过小，已重置为 128", this.file_path.getName());
            setLimitSizeZ(128);
        }
        setLimitAmount(config.getInt("Amount", 10));
        if (getLimitAmount() <= 0 && getLimitAmount() != -1) {
            XLogger.err("权限组 %s 的 Amount 设置不合法，已重置为 10", this.file_path.getName());
            setLimitAmount(10);
        }
        setLimitDepth(config.getInt("Depth", 3));
        if (getLimitDepth() <= 0 && getLimitDepth() != -1) {
            XLogger.err("权限组 %s 的 Depth 设置不合法，已重置为 3", this.file_path.getName());
            setLimitDepth(3);
        }
        setLimitVert(config.getBoolean("Vert", false));
        setWorldBlackList(config.getStringList("WorldBlackList"));
        setPrice(config.getDouble("Price", 10.0));
        if (getPrice() < 0.0) {
            XLogger.err("权限组 %s 的 Price 设置不合法，已重置为 10.0", this.file_path.getName());
            setPrice(10.0);
        }
        setPriceOnlyXZ(config.getBoolean("OnlyXZ", false));
        setRefundRatio(config.getDouble("Refund", 0.85));
        if (getRefundRatio() < 0.0 || getRefundRatio() > 1.0) {
            XLogger.err("权限组 %s 的 Refund 设置不合法，已重置为 0.85", this.file_path.getName());
            setRefundRatio(0.85);
        }
        save(); // 保存一次，确保文件中的数据是合法的
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
        this.config.set("MinY", min_y);
        this.save();
    }

    public void setLimitMaxY(Integer max_y) {
        this.max_y = max_y;
        this.config.set("MaxY", max_y);
        this.save();
    }

    public void setLimitSizeX(Integer size_x) {
        this.size_x = size_x;
        this.config.set("SizeX", size_x);
        this.save();
    }

    public void setLimitSizeY(Integer size_y) {
        this.size_y = size_y;
        this.config.set("SizeY", size_y);
        this.save();
    }

    public void setLimitSizeZ(Integer size_z) {
        this.size_z = size_z;
        this.config.set("SizeZ", size_z);
        this.save();
    }

    public void setLimitAmount(Integer amount) {
        this.amount = amount;
        this.config.set("Amount", amount);
        this.save();
    }

    public void setLimitDepth(Integer depth) {
        this.depth = depth;
        this.config.set("Depth", depth);
        this.save();
    }

    public void setLimitVert(Boolean vert) {
        this.vert = vert;
        this.config.set("Vert", vert);
        this.save();
    }

    public void setWorldBlackList(List<String> world_black_list) {
        this.world_black_list = world_black_list;
        this.config.set("WorldBlackList", world_black_list);
        this.save();
    }

    public void setPrice(Double price) {
        this.price = price;
        this.config.set("Price", price);
        this.save();
    }

    public void setPriceOnlyXZ(Boolean only_xz) {
        this.only_xz = only_xz;
        this.config.set("OnlyXZ", only_xz);
        this.save();
    }

    public void setRefundRatio(Double refund) {
        this.refund = refund;
        this.config.set("Refund", refund);
        this.save();
    }

    private void save() {
        if (file_path == null) {
            return;
        }
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
            GroupLimit group = new GroupLimit(file);
            groups.put(groupName, group);
        }
        XLogger.info("共加载了 %d 个领地组。", groups.size());
        return groups;
    }
}
