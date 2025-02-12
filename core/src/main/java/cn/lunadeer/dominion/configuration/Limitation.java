package cn.lunadeer.dominion.configuration;

import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.*;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static cn.lunadeer.dominion.misc.Converts.toWorld;

public class Limitation extends ConfigurationFile {
    public static class LimitationText extends ConfigurationPart {
        public String loadingWorldSettings = "Loading world settings...";
        public String loadingWorldSetting = "Loading setting for world %s...";
        public String loadWorldSettingFailed = "Failed to load world setting for world %s reason: %s";
        public String loadWorldSettingsSuccess = "Successfully loaded {0} world settings: {1}.";
    }


    @Comments("Do not modify this value.")
    public static int version = 2;  // <<<<<< When you change the configuration, you should increment this value.

    @Comments({
            "The priority of group when player has multiple groups.",
            "The group with lower priority will be used."
    })
    public int priority = 0;

    @Comments({
            "The settings of the economy support.",
            "You need to install vault and a economy plugin to use this feature."
    })
    public Economy economy = new Economy();

    public static class Economy extends ConfigurationPart {
        public boolean enable = false;
        @Comments("The price per block to claim dominion.")
        public double pricePerBlock = 10.0;
        @Comments("Only count the square blocks, ignore the height.")
        public boolean squareOnly = false;
        @Comments("The refund rate when player contract or delete dominion.")
        public double refundRate = 0.85;
    }

    @Comments("The settings of the teleportation feature.")
    public Teleportation teleportation = new Teleportation();

    public static class Teleportation extends ConfigurationPart {
        @Comments("Enable the teleportation feature.")
        public boolean enable = true;
        @Comments("The cooldown time of teleportation in seconds.")
        public int cooldown = 10;
        @Comments("Delay time before teleportation in seconds.")
        public int delay = 5;
    }

    @Comments({
            "How many dominions a player can create all over the world.",
            "Set -1 means no limitation."
    })
    public int amountAllOverTheWorld = 10;

    @HandleManually // See comments at loadWorldLimitationSettings() method
    public Map<String, WorldLimitationSettings> worldLimitations = Map.of(
            "default", new WorldLimitationSettings()
    );
    @HandleManually
    private final String worldLimitationsKey = "world-limitations";

    public static class WorldLimitationSettings extends ConfigurationPart {
        @Comments({
                "The maximum amount of dominions a player can create in this world.",
                "Set -1 means no limitation (but still limited by amountAllOverTheWorld)."
        })
        public int amount = 5;
        @Comments({
                "The depth limit of sub dominion.",
                "Set to -1 means no limitation, 0 means no sub dominion.",
        })
        public int maxSubDominionDepth = 3;
        @Comments({
                "Dominion's top no higher than this value.",
                "Should be larger than noLowerThan."
        })
        public int noHigherThan = 320;
        @Comments({
                "Dominion's bottom no lower than this value.",
                "Should be smaller than noHigherThan."
        })
        public int noLowerThan = -64;
        @Comments({
                "The maximum size of dominion in X axis (East-West).",
                "Set -1 means no limitation. Should be larger than sizeMinX and 0."
        })
        public int sizeMaxX = 128;
        @Comments({
                "The maximum size of dominion in Y axis (Height).",
                "Set -1 means no limitation. Should be larger than sizeMinY and 0."
        })
        public int sizeMaxY = 64;
        @Comments({
                "The maximum size of dominion in Z axis (North-South).",
                "Set -1 means no limitation. Should be larger than sizeMinZ and 0."
        })
        public int sizeMaxZ = 128;
        @Comments({
                "The minimum size of dominion in X axis (East-West).",
                "Should be smaller than sizeMaxX and larger than 0."
        })
        public int sizeMinX = 4;
        @Comments({
                "The minimum size of dominion in Y axis (Height).",
                "Should be smaller than sizeMaxY and larger than 0."
        })
        public int sizeMinY = 4;
        @Comments({
                "The minimum size of dominion in Z axis (North-South).",
                "Should be smaller than sizeMaxZ and larger than 0."
        })
        public int sizeMinZ = 4;
        @Comments("Weather to include all vertical blocks when calculating the size.")
        public boolean automaticIncludeVertical = false;
    }

    @PreProcess
    public void checkWorldLimitationSettings() {
        if (!yaml.contains(worldLimitationsKey)) {
            yaml.createSection(worldLimitationsKey);
            yaml.setComments(worldLimitationsKey, List.of(
                    "The settings of the limitations for each world.",
                    "The default settings will be used if the world is not listed here.",
                    "Do not delete the default."
            ));
        }
    }

    @PostProcess(priority = 1)
    public void loadWorldLimitationSettings() {
        XLogger.info(Language.limitationText.loadingWorldSettings);
        ConfigurationSection section = yaml.getConfigurationSection(worldLimitationsKey);
        if (section == null) {
            return;
        }
        Set<String> keys = section.getKeys(false);
        for (String key : keys) {
            XLogger.info(Language.limitationText.loadingWorldSetting, key);
            ConfigurationSection worldSection = section.getConfigurationSection(key);
            if (worldSection == null) {
                continue;
            }
            try {
                WorldLimitationSettings settings = new WorldLimitationSettings();
                ConfigurationManager.readConfigurationPart(worldSection, new WorldLimitationSettings(), null);
                worldLimitations.put(key, settings);
            } catch (Exception e) {
                XLogger.error(Language.limitationText.loadWorldSettingFailed, key, e.getMessage());
            }
        }
        XLogger.info(Language.limitationText.loadWorldSettingsSuccess, worldLimitations.size(), String.join(", ", worldLimitations.keySet()));
    }

    @PostProcess(priority = 2)
    public void checkLimitationParams() {
        if (economy.pricePerBlock < 0) {
            economy.pricePerBlock = 0;
        }

        if (economy.refundRate < 0) {
            economy.refundRate = 0;
        } else if (economy.refundRate > 1) {
            economy.refundRate = 1;
        }

        if (amountAllOverTheWorld < 0 && amountAllOverTheWorld != -1) {
            amountAllOverTheWorld = -1;
        }

        if (!worldLimitations.containsKey("default")) {
            worldLimitations.put("default", new WorldLimitationSettings());
        }

        for (WorldLimitationSettings settings : worldLimitations.values()) {
            if (settings.amount < 0 && settings.amount != -1) {
                settings.amount = -1;
            }

            if (settings.maxSubDominionDepth < 0) {
                settings.maxSubDominionDepth = -1;
            }

            if (settings.noHigherThan <= settings.noLowerThan) {
                settings.noHigherThan = settings.noLowerThan + 1;
            }

            if (settings.sizeMaxX < 0 && settings.sizeMaxX != -1) {
                settings.sizeMaxX = -1;
            }
            if (settings.sizeMaxY < 0 && settings.sizeMaxY != -1) {
                settings.sizeMaxY = -1;
            }
            if (settings.sizeMaxZ < 0 && settings.sizeMaxZ != -1) {
                settings.sizeMaxZ = -1;
            }
            if (settings.sizeMinX <= 0) {
                settings.sizeMinX = 1;
            }
            if (settings.sizeMaxX < settings.sizeMinX) {
                settings.sizeMaxX = settings.sizeMinX + 1;
            }
            if (settings.sizeMinY <= 0) {
                settings.sizeMinY = 1;
            }
            if (settings.sizeMaxY < settings.sizeMinY) {
                settings.sizeMaxY = settings.sizeMinY + 1;
            }
            if (settings.sizeMinZ <= 0) {
                settings.sizeMinZ = 1;
            }
            if (settings.sizeMaxZ < settings.sizeMinZ) {
                settings.sizeMaxZ = settings.sizeMinZ + 1;
            }

        }
    }

    @PostProcess(priority = 3)
    public void saveWorldLimitationSettings() {
        ConfigurationSection section = yaml.getConfigurationSection(worldLimitationsKey);
        if (section == null) {
            return;
        }
        for (Map.Entry<String, WorldLimitationSettings> entry : worldLimitations.entrySet()) {
            try {
                section.createSection(entry.getKey());
                ConfigurationSection worldSection = section.getConfigurationSection(entry.getKey());
                ConfigurationManager.writeConfigurationPart(worldSection, entry.getValue(), null, !Objects.equals(entry.getKey(), "default"));
                section.set(entry.getKey(), worldSection);
            } catch (Exception e) {
                XLogger.warn("Failed to save world limitation settings for world %s.", entry.getKey());
            }
        }
        yaml.set(worldLimitationsKey, section);
    }

    public @NotNull WorldLimitationSettings getWorldSettings(@NotNull UUID worldUUID) {
        return getWorldSettings(toWorld(worldUUID).getName());
    }

    public @NotNull WorldLimitationSettings getWorldSettings(@NotNull World world) {
        return getWorldSettings(world.getName());
    }

    public @NotNull WorldLimitationSettings getWorldSettings(@Nullable String worldName) {
        if (worldName == null) {
            return worldLimitations.get("default");
        }
        WorldLimitationSettings settings = worldLimitations.get(worldName);
        if (settings == null) {
            settings = worldLimitations.get("default");
        }
        return settings;
    }
}
