package cn.lunadeer.dominion.utils.configuration;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Marker interface for configuration files.
 * <p>
 * The items in the configuration file should be public fields.
 * <p>
 * The constructor should be public and have no parameters.
 */
public abstract class ConfigurationFile extends ConfigurationPart {

    public YamlConfiguration yaml;

}
