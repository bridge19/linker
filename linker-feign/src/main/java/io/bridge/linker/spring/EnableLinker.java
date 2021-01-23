package io.bridge.linker.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({LinkerAutoConfiguredRegister.class})
public @interface EnableLinker {
  String[] basePackages() default {};
}
