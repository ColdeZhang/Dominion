package cn.lunadeer.dominion.utils.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a method to be run before the configuration is processed.
 * <p>
 * The method must be public.
 * <p>
 * The method must have no parameters.
 * <p>
 * Priority is determined by the priority field.
 * <p>
 * Priority is in ascending order. The lower the number, the earlier the method is run.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PreProcess {
    int priority() default 0;
}
