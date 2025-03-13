package cn.lunadeer.dominion.utils.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for adding comment behind configuration fields.
 * <p>
 * {@code @Comments("This is a comment")}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Comment {
    String value();
}
