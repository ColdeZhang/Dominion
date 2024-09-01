package cn.lunadeer.dominion.utils.i18n;

import cn.lunadeer.minecraftpluginutils.XLogger;

import java.lang.reflect.Field;

public class i18n {

    private String translate;

    private final String sourceName;
    private final String key;

    public String trans() {
        return translate;
    }

    public void trans(String translate) {
        this.translate = translate;
    }

    public String getKey() {
        return key;
    }

    public String getSourceName() {
        return sourceName;
    }

    private i18n(Class<?> source, String key, String defaultValue) {
        this.sourceName = source.getName().replace("cn.lunadeer.dominion.", "").replace(".", "-");
        this.key = key;
        this.translate = defaultValue;
    }

    public static i18n create(Class<?> source, String key, String defaultValue) {
        i18n i18n = new i18n(source, key, defaultValue);
        Localization.registerNode(i18n);
        return i18n;
    }

    public static void initializeI18nFields(Object obj) {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(i18nField.class)) {
                i18nField annotation = field.getAnnotation(i18nField.class);
                String key = field.getName();
                i18n i18nValue = i18n.create(clazz, key, annotation.defaultValue());
                field.setAccessible(true);
                try {
                    field.set(obj, i18nValue);
                } catch (IllegalAccessException e) {
                    XLogger.err("Failed to set i18n field %s", field.getName());
                }
            }
        }
    }
}
