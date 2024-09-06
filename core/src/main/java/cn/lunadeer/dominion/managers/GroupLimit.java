package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupLimit {
    private YamlConfiguration config;
    private final File file_path;
    private Integer min_y;
    private Integer max_y;
    private Integer size_x;
    private Integer size_y;
    private Integer size_z;
    private Integer amount;
    private Integer depth;
    private Boolean vert;
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
        min_y = config.getInt("MinY", -64);
        max_y = config.getInt("MaxY", 320);
        if (getLimitMinY() >= getLimitMaxY()) {
            XLogger.err(Translation.Config_Check_GroupMinYError, this.file_path.getName());
            setLimitMinY(-64);
            setLimitMaxY(320);
        }
        size_x = config.getInt("SizeX", 128);
        if (getLimitSizeX() <= 4 && getLimitSizeX() != -1) {
            XLogger.err(Translation.Config_Check_GroupSizeXError, this.file_path.getName());
            setLimitSizeX(128);
        }
        size_y = config.getInt("SizeY", 64);
        if (getLimitSizeY() <= 4 && getLimitSizeY() != -1) {
            XLogger.err(Translation.Config_Check_GroupSizeYError, this.file_path.getName());
            setLimitSizeY(64);
        }
        size_z = config.getInt("SizeZ", 128);
        if (getLimitSizeZ() <= 4 && getLimitSizeZ() != -1) {
            XLogger.err(Translation.Config_Check_GroupSizeZError, this.file_path.getName());
            setLimitSizeZ(128);
        }
        amount = config.getInt("Amount", 10);
        if (getLimitAmount() <= 0 && getLimitAmount() != -1) {
            XLogger.err(Translation.Config_Check_GroupAmountError, this.file_path.getName());
            setLimitAmount(10);
        }
        depth = config.getInt("Depth", 3);
        if (getLimitDepth() <= 0 && getLimitDepth() != -1) {
            XLogger.err(Translation.Config_Check_GroupDepthError, this.file_path.getName());
            setLimitDepth(3);
        }
        vert = config.getBoolean("Vert", false);
        price = config.getDouble("Price", 10.0);
        if (getPrice() < 0.0) {
            XLogger.err(Translation.Config_Check_GroupPriceError, this.file_path.getName());
            setPrice(10.0);
        }
        only_xz = config.getBoolean("OnlyXZ", false);
        refund = config.getDouble("Refund", 0.85);
        if (getRefundRatio() < 0.0 || getRefundRatio() > 1.0) {
            XLogger.err(Translation.Config_Check_GroupRefundError, this.file_path.getName());
            setRefundRatio(0.85);
        }
        save(); // 保存一次，确保文件中的数据是合法的
        saveAll();
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
        XLogger.info(Translation.Messages_LoadedGroupAmount, groups.size());
        return groups;
    }

    private void saveAll() {
        this.file_path.delete();
        this.config = new YamlConfiguration();
        this.config.set("MinY", min_y);
        this.config.setComments("MinY", Arrays.asList(
                Translation.Config_Comment_GroupLine1.trans(),
                Translation.Config_Comment_GroupLine2.trans(),
                Translation.Config_Comment_GroupLine3.trans(),
                Translation.Config_Comment_GroupLine4.trans(),
                Translation.Config_Comment_GroupLine5.trans(),
                Translation.Config_Comment_GroupLine6.trans(),
                Translation.Config_Comment_GroupLine7.trans(),
                String.format(Translation.Config_Comment_GroupLine8DocumentAddress.trans(), ConfigManager.instance.getLanguage())
        ));
        this.config.setInlineComments("MinY", List.of(Translation.Config_Comment_MinY.trans()));
        this.config.set("MaxY", max_y);
        this.config.setInlineComments("MaxY", List.of(Translation.Config_Comment_MaxY.trans()));
        this.config.set("SizeX", size_x);
        this.config.setInlineComments("SizeX", List.of(Translation.Config_Comment_SizeX.trans() + Translation.Config_Comment_NegativeOneUnlimited.trans()));
        this.config.set("SizeY", size_y);
        this.config.setInlineComments("SizeY", List.of(Translation.Config_Comment_SizeY.trans() + Translation.Config_Comment_NegativeOneUnlimited.trans()));
        this.config.set("SizeZ", size_z);
        this.config.setInlineComments("SizeZ", List.of(Translation.Config_Comment_SizeZ.trans() + Translation.Config_Comment_NegativeOneUnlimited.trans()));
        this.config.set("Amount", amount);
        this.config.setInlineComments("Amount", List.of(Translation.Config_Comment_Amount.trans() + Translation.Config_Comment_NegativeOneUnlimited.trans()));
        this.config.set("Depth", depth);
        this.config.setInlineComments("Depth", List.of(Translation.Config_Comment_Depth.trans() + Translation.Config_Comment_ZeroDisabled.trans() + Translation.Config_Comment_NegativeOneUnlimited.trans()));
        this.config.set("Vert", vert);
        this.config.setInlineComments("Vert", List.of(Translation.Config_Comment_Vert.trans()));
        this.config.set("Price", price);
        this.config.setInlineComments("Price", List.of(Translation.Config_Comment_Price.trans()));
        this.config.set("OnlyXZ", only_xz);
        this.config.setInlineComments("OnlyXZ", List.of(Translation.Config_Comment_OnlyXZ.trans()));
        this.config.set("Refund", refund);
        this.config.setInlineComments("Refund", List.of(Translation.Config_Comment_Refund.trans()));

        try {
            this.config.save(this.file_path);
        } catch (Exception e) {
            XLogger.err("Failed to save group limit file: " + this.file_path.getName());
        }
    }
}
