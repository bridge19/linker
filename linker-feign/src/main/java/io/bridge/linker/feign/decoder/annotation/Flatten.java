package io.bridge.linker.feign.decoder.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Flatten {
  String value() default "";
}
