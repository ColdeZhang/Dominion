package cn.lunadeer.dominion.events;

import cn.lunadeer.dominion.utils.XVersionManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HighestVersion {
    XVersionManager.ImplementationVersion value();
}
