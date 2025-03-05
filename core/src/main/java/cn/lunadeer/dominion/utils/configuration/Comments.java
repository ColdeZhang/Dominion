package cn.lunadeer.dominion.utils.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for adding comments to configuration fields in lines
 * <p>
 * Single line: {@code @Comments("This is a comment")}
 * <br>
 * Multi line: {@code @Comments({"This is a comment", "This is another comment"})}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Comments {
    String[] value();
}
