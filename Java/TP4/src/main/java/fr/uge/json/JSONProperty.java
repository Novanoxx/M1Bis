package fr.uge.json;

import java.lang.annotation.*;

@Documented
@Target({ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONProperty {
    String value() default "";
}
