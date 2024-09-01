package cn.lunadeer.dominion.utils.i18n;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface i18nField {
    String defaultValue();
}