package io.bridge.linker.orchestration.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.TYPE})
@Documented
public @interface DataId {
  String value();
}
