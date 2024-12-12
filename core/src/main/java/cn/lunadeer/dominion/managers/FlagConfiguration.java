package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.minecraftpluginutils.JsonFile;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.i18n.Localization;
import com.alibaba.fastjson.JSONObject;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class FlagConfiguration {
    public static void loadFromFile() {
        try {
            loadLegacyJsonFlags();
            loadFlagsConfiguration();
        } catch (Exception e) {
            XLogger.err(Translation.Config_Check_LoadFlagError, e.getMessage());
        }
    }

    private static void loadLegacyJsonFlags() throws Exception {
        File jsonFile = new File(Dominion.instance.getDataFolder(), "flags.json");
        if (jsonFile.exists()) {
            JSONObject jsonObject = JsonFile.loadFromFile(jsonFile);
            if (jsonObject != null) {
                deserializeFromJson(jsonObject);
            }
            jsonFile.delete();
        }
    }

    private static void loadFlagsConfiguration() throws IOException {
        File yamlFile = new File(Dominion.instance.getDataFolder(), "flags.yml");
        if (!yamlFile.exists()) {
            Dominion.instance.saveResource("flags.yml", false);
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlFile);
        for (Flag flag : Flags.getAllFlags()) {
            // load flags name & description translations
            ((Translation) (Localization.instance)).loadOrSetFlagTranslation(flag);

            if (yaml.contains(flag.getConfigurationDefaultKey())) {
                flag.setDefaultValue(yaml.getBoolean(flag.getConfigurationDefaultKey()));
            } else {
                yaml.set(flag.getConfigurationDefaultKey(), flag.getDefaultValue());
            }
            if (yaml.contains(flag.getConfigurationEnableKey())) {
                flag.setEnable(yaml.getBoolean(flag.getConfigurationEnableKey()));
            } else {
                yaml.set(flag.getConfigurationEnableKey(), flag.getEnable());
            }
            yaml.setInlineComments(flag.getConfigurationDescKey(), Collections.singletonList(flag.getDisplayName() + "-" + flag.getDescription()));
        }
        yaml.save(yamlFile);
    }

    @Deprecated
    public static void deserializeFromJson(JSONObject jsonObject) {
        for (Flag flag : Flags.getAllFlags()) {
            try {
                JSONObject flagJson = (JSONObject) jsonObject.get(flag.getFlagName());
                if (flagJson != null) {
                    flag.setDefaultValue((Boolean) flagJson.getOrDefault("default_value", flag.getDefaultValue()));
                    flag.setEnable((Boolean) flagJson.getOrDefault("enable", flag.getEnable()));
                }
            } catch (Exception ignored) {
            }
        }
    }
}
