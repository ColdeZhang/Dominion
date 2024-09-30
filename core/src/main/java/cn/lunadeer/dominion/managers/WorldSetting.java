package cn.lunadeer.dominion.managers;

import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;

public class WorldSetting {
    public Integer min_y;
    public Integer max_y;
    public Integer size_max_x;
    public Integer size_max_y;
    public Integer size_max_z;
    public Integer size_min_x;
    public Integer size_min_y;
    public Integer size_min_z;
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
        section.set("some_world_name.Size.MaxX", 128);
        section.set("some_world_name.Size.MaxY", 64);
        section.set("some_world_name.Size.MaxZ", 128);
        section.set("some_world_name.Size.MinX", 4);
        section.set("some_world_name.Size.MinY", 4);
        section.set("some_world_name.Size.MinZ", 4);
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
            if (worldSettings.contains(worldName + ".SizeX")) { // todo: should be removed in the future
                setting.size_max_x = worldSettings.getInt(worldName + ".SizeX", 128);
                setting.size_max_y = worldSettings.getInt(worldName + ".SizeY", 64);
                setting.size_max_z = worldSettings.getInt(worldName + ".SizeZ", 128);
                setting.size_min_x = 4;
                setting.size_min_y = 4;
                setting.size_min_z = 4;
            } else {
                setting.size_max_x = worldSettings.getInt(worldName + ".Size.MaxX", 128);
                setting.size_max_y = worldSettings.getInt(worldName + ".Size.MaxY", 64);
                setting.size_max_z = worldSettings.getInt(worldName + ".Size.MaxZ", 128);
                setting.size_min_x = worldSettings.getInt(worldName + ".Size.MinX", 4);
                setting.size_min_y = worldSettings.getInt(worldName + ".Size.MinY", 4);
                setting.size_min_z = worldSettings.getInt(worldName + ".Size.MinZ", 4);
            }
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
        section.set("Size.MaxX", size_max_x);
        section.set("Size.MaxY", size_max_y);
        section.set("Size.MaxZ", size_max_z);
        section.set("Size.MinX", size_min_x);
        section.set("Size.MinY", size_min_y);
        section.set("Size.MinZ", size_min_z);
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
        if (size_max_x <= 4 && size_max_x != -1) {
            XLogger.err(Translation.Config_Check_GroupSizeMaxXError, sourceName);
            size_max_x = 128;
        }
        if (size_max_y <= 4 && size_max_y != -1) {
            XLogger.err(Translation.Config_Check_GroupSizeMaxYError, sourceName);
            size_max_y = 64;
        }
        if (size_max_z <= 4 && size_max_z != -1) {
            XLogger.err(Translation.Config_Check_GroupSizeMaxZError, sourceName);
            size_max_z = 128;
        }
        if (size_min_x <= 0) {
            XLogger.err(Translation.Config_Check_GroupSizeMinXError, sourceName);
            size_min_x = 4;
        }
        if (size_min_y <= 0) {
            XLogger.err(Translation.Config_Check_GroupSizeMinYError, sourceName);
            size_min_y = 4;
        }
        if (size_min_z <= 0) {
            XLogger.err(Translation.Config_Check_GroupSizeMinZError, sourceName);
            size_min_z = 4;
        }
        if (size_max_x < size_min_x && size_max_x != -1) {
            XLogger.err(Translation.Config_Check_GroupMaxMinXError, sourceName);
            size_max_x = 128;
            size_min_x = 4;
        }
        if (size_max_y < size_min_y && size_max_y != -1) {
            XLogger.err(Translation.Config_Check_GroupMaxMinYError, sourceName);
            size_max_y = 64;
            size_min_y = 4;
        }
        if (size_max_z < size_min_z && size_max_z != -1) {
            XLogger.err(Translation.Config_Check_GroupMaxMinZError, sourceName);
            size_max_z = 128;
            size_min_z = 4;
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
