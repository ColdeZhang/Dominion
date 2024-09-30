package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class GroupLimit {


    private YamlConfiguration config;
    private final File file_path;
    private final Map<String, WorldSetting> world_limits = new HashMap<>();
    private Double price;
    private Boolean only_xz;
    private Double refund;

    public GroupLimit() {
        this.file_path = null;
        this.config = new YamlConfiguration();
        WorldSetting defaultSetting = new WorldSetting("config.yml");
        world_limits.put("default", defaultSetting);
    }

    private GroupLimit(File filePath) {
        this.file_path = filePath;
        config = YamlConfiguration.loadConfiguration(this.file_path);
        WorldSetting defaultSetting = new WorldSetting(filePath.getName());
        defaultSetting.min_y = config.getInt("MinY", -64);
        defaultSetting.max_y = config.getInt("MaxY", 320);
        if (config.contains("SizeX")) { // todo: should be removed in the future
            defaultSetting.size_max_x = config.getInt("SizeX", 128);
            defaultSetting.size_max_y = config.getInt("SizeY", 64);
            defaultSetting.size_max_z = config.getInt("SizeZ", 128);
            defaultSetting.size_min_x = 4;
            defaultSetting.size_min_y = 4;
            defaultSetting.size_min_z = 4;
        } else {
            defaultSetting.size_max_x = config.getInt("Size.MaxX", 128);
            defaultSetting.size_max_y = config.getInt("Size.MaxY", 64);
            defaultSetting.size_max_z = config.getInt("Size.MaxZ", 128);
            defaultSetting.size_min_x = config.getInt("Size.MinX", 4);
            defaultSetting.size_min_y = config.getInt("Size.MinY", 4);
            defaultSetting.size_min_z = config.getInt("Size.MinZ", 4);
        }
        defaultSetting.amount = config.getInt("Amount", 10);
        defaultSetting.depth = config.getInt("Depth", 3);
        defaultSetting.vert = config.getBoolean("Vert", false);
        world_limits.put("default", defaultSetting);
        ConfigurationSection worldSettings = config.getConfigurationSection("WorldSettings");
        if (worldSettings != null) {
            addWorldLimits(WorldSetting.load(filePath.getName() + ":WorldSettings", worldSettings));
        }
        price = config.getDouble("Price", 10.0);
        only_xz = config.getBoolean("OnlyXZ", false);
        refund = config.getDouble("Refund", 0.85);
        checkRules();
        saveAll();
    }

    public Integer getLimitMinY(@Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            return world_limits.get("default").min_y;
        } else {
            return world_limits.get(world.getName()).min_y;
        }
    }

    public Integer getLimitMaxY(@Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            return world_limits.get("default").max_y;
        } else {
            return world_limits.get(world.getName()).max_y;
        }
    }

    public Integer getLimitSizeMaxX(@Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            return world_limits.get("default").size_max_x;
        } else {
            return world_limits.get(world.getName()).size_max_x;
        }
    }

    public Integer getLimitSizeMaxY(@Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            return world_limits.get("default").size_max_y;
        } else {
            return world_limits.get(world.getName()).size_max_y;
        }
    }

    public Integer getLimitSizeMaxZ(@Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            return world_limits.get("default").size_max_z;
        } else {
            return world_limits.get(world.getName()).size_max_z;
        }
    }

    public Integer getLimitSizeMinX(@Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            return world_limits.get("default").size_min_x;
        } else {
            return world_limits.get(world.getName()).size_min_x;
        }
    }

    public Integer getLimitSizeMinY(@Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            return world_limits.get("default").size_min_y;
        } else {
            return world_limits.get(world.getName()).size_min_y;
        }
    }

    public Integer getLimitSizeMinZ(@Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            return world_limits.get("default").size_min_z;
        } else {
            return world_limits.get(world.getName()).size_min_z;
        }
    }

    public Integer getLimitAmount(@Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            return world_limits.get("default").amount;
        } else {
            return world_limits.get(world.getName()).amount;
        }
    }

    public Integer getLimitDepth(@Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            return world_limits.get("default").depth;
        } else {
            return world_limits.get(world.getName()).depth;
        }
    }

    public Boolean getLimitVert(@Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            return world_limits.get("default").vert;
        } else {
            return world_limits.get(world.getName()).vert;
        }
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


    public void setLimitMinY(Integer min_y, @Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            world_limits.get("default").min_y = min_y;
        } else {
            world_limits.get(world.getName()).min_y = min_y;
        }
    }

    public void setLimitMaxY(Integer max_y, @Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            world_limits.get("default").max_y = max_y;
        } else {
            world_limits.get(world.getName()).max_y = max_y;
        }
    }

    public void setLimitSizeMaxX(Integer size_x, @Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            world_limits.get("default").size_max_x = size_x;
        } else {
            world_limits.get(world.getName()).size_max_x = size_x;
        }
    }

    public void setLimitSizeMaxY(Integer size_y, @Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            world_limits.get("default").size_max_y = size_y;
        } else {
            world_limits.get(world.getName()).size_max_y = size_y;
        }
    }

    public void setLimitSizeMaxZ(Integer size_z, @Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            world_limits.get("default").size_max_z = size_z;
        } else {
            world_limits.get(world.getName()).size_max_z = size_z;
        }
    }

    public void setLimitSizeMinX(Integer size_x, @Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            world_limits.get("default").size_min_x = size_x;
        } else {
            world_limits.get(world.getName()).size_min_x = size_x;
        }
    }

    public void setLimitSizeMinY(Integer size_y, @Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            world_limits.get("default").size_min_y = size_y;
        } else {
            world_limits.get(world.getName()).size_min_y = size_y;
        }
    }

    public void setLimitSizeMinZ(Integer size_z, @Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            world_limits.get("default").size_min_z = size_z;
        } else {
            world_limits.get(world.getName()).size_min_z = size_z;
        }
    }

    public void setLimitAmount(Integer amount, @Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            world_limits.get("default").amount = amount;
        } else {
            world_limits.get(world.getName()).amount = amount;
        }
    }

    public void setLimitDepth(Integer depth, @Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            world_limits.get("default").depth = depth;
        } else {
            world_limits.get(world.getName()).depth = depth;
        }
    }

    public void setLimitVert(Boolean vert, @Nullable World world) {
        if (world == null || !world_limits.containsKey(world.getName())) {
            world_limits.get("default").vert = vert;
        } else {
            world_limits.get(world.getName()).vert = vert;
        }
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

    public void addWorldLimits(Map<String, WorldSetting> limits) {
        world_limits.putAll(limits);
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
        this.config.set("MinY", world_limits.get("default").min_y);
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
        this.config.set("MaxY", world_limits.get("default").max_y);
        this.config.setInlineComments("MaxY", List.of(Translation.Config_Comment_MaxY.trans()));
        this.config.set("Size.MaxX", world_limits.get("default").size_max_x);
        this.config.setInlineComments("Size.MaxX", List.of(Translation.Config_Comment_SizeMaxX.trans() + Translation.Config_Comment_NegativeOneUnlimited.trans()));
        this.config.set("Size.MaxY", world_limits.get("default").size_max_y);
        this.config.setInlineComments("Size.MaxY", List.of(Translation.Config_Comment_SizeMaxY.trans() + Translation.Config_Comment_NegativeOneUnlimited.trans()));
        this.config.set("Size.MaxZ", world_limits.get("default").size_max_z);
        this.config.setInlineComments("Size.MaxZ", List.of(Translation.Config_Comment_SizeMaxZ.trans() + Translation.Config_Comment_NegativeOneUnlimited.trans()));
        this.config.set("Size.MinX", world_limits.get("default").size_min_x);
        this.config.setInlineComments("Size.MinX", List.of(Translation.Config_Comment_SizeMinX.trans()));
        this.config.set("Size.MinY", world_limits.get("default").size_min_y);
        this.config.setInlineComments("Size.MinY", List.of(Translation.Config_Comment_SizeMinY.trans()));
        this.config.set("Size.MinZ", world_limits.get("default").size_min_z);
        this.config.setInlineComments("Size.MinZ", List.of(Translation.Config_Comment_SizeMinZ.trans()));
        this.config.set("Amount", world_limits.get("default").amount);
        this.config.setInlineComments("Amount", List.of(Translation.Config_Comment_Amount.trans() + Translation.Config_Comment_NegativeOneUnlimited.trans()));
        this.config.set("Depth", world_limits.get("default").depth);
        this.config.setInlineComments("Depth", List.of(Translation.Config_Comment_Depth.trans() + Translation.Config_Comment_ZeroDisabled.trans() + Translation.Config_Comment_NegativeOneUnlimited.trans()));
        this.config.set("Vert", world_limits.get("default").vert);
        this.config.setInlineComments("Vert", List.of(Translation.Config_Comment_Vert.trans()));
        this.config.set("Price", price);
        this.config.setInlineComments("Price", List.of(Translation.Config_Comment_Price.trans()));
        this.config.set("OnlyXZ", only_xz);
        this.config.setInlineComments("OnlyXZ", List.of(Translation.Config_Comment_OnlyXZ.trans()));
        this.config.set("Refund", refund);
        this.config.setInlineComments("Refund", List.of(Translation.Config_Comment_Refund.trans()));

        this.config.set("WorldSettings", getWorldSettings());
        this.config.setInlineComments("WorldSettings", List.of(Translation.Config_Comment_WorldSettings.trans()));

        try {
            this.config.save(this.file_path);
        } catch (Exception e) {
            XLogger.err("Failed to save group limit file: " + this.file_path.getName());
        }
    }

    public YamlConfiguration getWorldSettings() {
        YamlConfiguration section = new YamlConfiguration();
        if (world_limits.size() <= 1) {
            return WorldSetting.getDefaultList();
        }
        for (Map.Entry<String, WorldSetting> entry : world_limits.entrySet()) {
            if (entry.getKey().equals("default")) {
                continue;
            }
            section.set(entry.getKey(), entry.getValue().getYaml());
        }
        return section;
    }

    public void checkRules() {
        if (getPrice() < 0.0) {
            XLogger.err(Translation.Config_Check_GroupPriceError, this.file_path.getName());
            setPrice(10.0);
        }
        if (getRefundRatio() < 0.0 || getRefundRatio() > 1.0) {
            XLogger.err(Translation.Config_Check_GroupRefundError, this.file_path.getName());
            setRefundRatio(0.85);
        }
        for (WorldSetting w : world_limits.values()) {
            w.checkRules();
        }
    }

    public List<String> getWorldBlackList() {
        List<String> list = new ArrayList<>();
        if (world_limits.getOrDefault("default", new WorldSetting("default")).amount == 0) {
            list.addAll(Dominion.instance.getServer().getWorlds().stream().map(World::getName).toList());
        }
        for (Map.Entry<String, WorldSetting> entry : world_limits.entrySet()) {
            if (entry.getKey().equals("default")) {
                continue;
            }
            if (entry.getValue().amount == 0) {
                list.add(entry.getKey());
            } else {
                list.remove(entry.getKey());
            }
        }
        return list;
    }
}
