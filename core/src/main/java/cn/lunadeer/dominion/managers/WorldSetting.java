package cn.lunadeer.dominion.managers;

import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;

public class WorldSetting {
    public Integer min_y;
    public Integer max_y;
    public Integer size_x;
    public Integer size_y;
    public Integer size_z;
    public Integer amount;
    public Integer depth;
    public Boolean vert;
    private final String sourceName;

    /**
     * 生成默认设置
     *
     * @return 设置内容
     */
    public static YamlConfiguration getDefaultList() {
        YamlConfiguration section = new YamlConfiguration();
        section.set("some_world_name.MinY", -64);
        section.set("some_world_name.MaxY", 320);
        section.set("some_world_name.SizeX", 128);
        section.set("some_world_name.SizeY", 64);
        section.set("some_world_name.SizeZ", 128);
        section.set("some_world_name.Amount", 10);
        section.set("some_world_name.Depth", 3);
        section.set("some_world_name.Vert", false);
        return section;
    }

    public WorldSetting(String sourceName) {
        this.sourceName = sourceName;
    }

    public static Map<String, WorldSetting> load(String sourceName, ConfigurationSection worldSettings) {
        Map<String, WorldSetting> world_limits = new java.util.HashMap<>();
        for (String worldName : worldSettings.getKeys(false)) {
            WorldSetting setting = new WorldSetting(sourceName);
            setting.min_y = worldSettings.getInt(worldName + ".MinY", -64);
            setting.max_y = worldSettings.getInt(worldName + ".MaxY", 320);
            setting.size_x = worldSettings.getInt(worldName + ".SizeX", 128);
            setting.size_y = worldSettings.getInt(worldName + ".SizeY", 64);
            setting.size_z = worldSettings.getInt(worldName + ".SizeZ", 128);
            setting.amount = worldSettings.getInt(worldName + ".Amount", 10);
            setting.depth = worldSettings.getInt(worldName + ".Depth", 3);
            setting.vert = worldSettings.getBoolean(worldName + ".Vert", false);
            if (worldSettings.contains(worldName + ".Allow") && !worldSettings.getBoolean(worldName + ".Allow")) {
                setting.amount = 0;
            }
            world_limits.put(worldName, setting);
        }
        return world_limits;
    }

    public YamlConfiguration getYaml() {
        YamlConfiguration section = new YamlConfiguration();
        section.set("MinY", min_y);
        section.set("MaxY", max_y);
        section.set("SizeX", size_x);
        section.set("SizeY", size_y);
        section.set("SizeZ", size_z);
        section.set("Amount", amount);
        section.set("Depth", depth);
        section.set("Vert", vert);
        return section;
    }

    public void checkRules() {
        if (min_y > max_y) {
            XLogger.err(Translation.Config_Check_GroupMinYError, sourceName);
            min_y = -64;
            max_y = 320;
        }
        if (size_x <= 4 && size_x != -1) {
            XLogger.err(Translation.Config_Check_GroupSizeXError, sourceName);
            size_x = 128;
        }
        if (size_y <= 4 && size_y != -1) {
            XLogger.err(Translation.Config_Check_GroupSizeYError, sourceName);
            size_y = 64;
        }
        if (size_z <= 4 && size_z != -1) {
            XLogger.err(Translation.Config_Check_GroupSizeZError, sourceName);
            size_z = 128;
        }
        if (amount < 0 && amount != -1) {
            XLogger.err(Translation.Config_Check_GroupAmountError, sourceName);
            amount = 10;
        }
        if (depth < 0 && depth != -1) {
            XLogger.err(Translation.Config_Check_GroupDepthError, sourceName);
            depth = 3;
        }
    }
}
