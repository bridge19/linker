package io.bridge.linker.orchestration.annotation;

import io.bridge.linker.orchestration.translators.Constraint;
import io.bridge.linker.orchestration.translators.SimpleTranslator;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
@Documented
@Constraint(translatedBy = SimpleTranslator.class)
public @interface Simple {
  String dataId() default "";
}
