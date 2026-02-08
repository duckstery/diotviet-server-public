package diotviet.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Print Tag for Tinymce
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PrintTag {
    /**
     * Optional argument that defines whether this annotation is active
     * or not. The only use for value 'false' if for overriding purposes
     * (which is not needed often); most likely it is needed for use
     * with "mix-in annotations" (aka "annotation overrides").
     * For most cases, however, default value of "true" is just fine
     * and should be omitted.
     *
     * @return True if annotation is enabled (normal case); false if it is to
     *   be ignored (only useful for mix-in annotations to "mask" annotation)
     */

    String value() default "";

    int sequence();

    String[] example() default {};

    Class<?>[] component() default {};

    boolean isIterable() default false;

    boolean isIdentifier() default false;

    boolean ignored() default false;
}
