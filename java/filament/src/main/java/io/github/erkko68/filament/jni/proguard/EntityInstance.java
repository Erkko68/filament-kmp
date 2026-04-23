package io.github.erkko68.filament.jni.proguard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an {@code int} field or parameter as an entity instance (component manager index).
 * Used to aid readability; has no runtime effect.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface EntityInstance {
}
