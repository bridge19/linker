package io.bridge.linker.orchestration.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface Invoker {

  Class<?> targetClass();

  String methodName();

  boolean proxy() default true;
}
