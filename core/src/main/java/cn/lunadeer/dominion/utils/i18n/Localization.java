package cn.lunadeer.dominion.utils.i18n;

import cn.lunadeer.minecraftpluginutils.XLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Localization {

    public static Localization instance;
    private static final Map<String, List<i18n>> nodeMap = new HashMap<>();

    private YamlConfiguration localeFile;

    private final File languageFolder;

    private String locale = "chinese-simplified";

    public Localization(JavaPlugin plugin) {
        this.languageFolder = new File(plugin.getDataFolder(), "languages");
        if (!this.languageFolder.exists()) {
            this.languageFolder.mkdirs();
            plugin.saveResource("languages", true);
        }
        instance = this;
    }

    public static void registerNode(i18n i18n) {
        if (!nodeMap.containsKey(i18n.getSourceName())) {
            nodeMap.put(i18n.getSourceName(), new ArrayList<>());
        }
        nodeMap.get(i18n.getSourceName()).add(i18n);
    }

    public void loadLocale(String locale) {
        this.locale = locale;
        File file = new File(languageFolder, this.locale + ".yml");
        if (!file.exists()) {
            XLogger.err("Locale file %s not found, using default locale.", locale);
            this.locale = "chinese-simplified";
            file = new File(languageFolder, this.locale + ".yml");
        }
        this.localeFile = YamlConfiguration.loadConfiguration(file);
        for (String name : nodeMap.keySet()) {
            if (!this.localeFile.contains(name)) {
                this.localeFile.createSection(name);
            }
            for (i18n i18n : nodeMap.get(name)) {
                String key = name + "." + i18n.getKey();
                if (!this.localeFile.contains(key)) {
                    this.localeFile.set(key, i18n.trans());
                } else {
                    i18n.trans(this.localeFile.getString(key));
                }
            }
        }
        try {
            this.localeFile.save(file);
        } catch (Exception e) {
            XLogger.err("Failed to save locale file: %s", file.getName());
        }
    }


}
