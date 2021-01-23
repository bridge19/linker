package io.bridge.linker.orchestration.translators;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@Constraint(translatedBy = ScriptTranslator.class)
public @interface Script {
  String[] dataIds() default {};
  String[] scripts() default "";
}
