package io.bridge.linker.orchestration.translators;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@Constraint(translatedBy = StringConcatTranslator.class)
public @interface Concat {
  String[] dataIds() default {};
  String connectedBy() default "";
}
