package cn.lunadeer.dominion.utils.configuration;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Marker interface for configuration files.
 * <p>
 * The items in the configuration file should be public fields.
 * <p>
 * The constructor should be public and have no parameters.
 */
public abstract class ConfigurationFile extends ConfigurationPart {

    private YamlConfiguration yaml;

    public YamlConfiguration getYaml() {
        return yaml;
    }

    public void setYaml(YamlConfiguration yaml) {
        this.yaml = yaml;
    }

    public void save(File file) throws Exception {
        yaml.options().width(250);
        yaml.save(file);
    }

}
