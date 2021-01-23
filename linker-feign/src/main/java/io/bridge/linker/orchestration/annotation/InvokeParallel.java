package io.bridge.linker.orchestration.annotation;

import java.lang.annotation.*;

/**
 * 该注解用于并行计算
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface InvokeParallel {
  InvokerGroup[] invokerGroups() default {};
}
