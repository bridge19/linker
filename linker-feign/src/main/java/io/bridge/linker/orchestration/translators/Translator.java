package io.bridge.linker.orchestration.translators;

import io.bridge.linker.orchestration.context.TranslationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface Translator<A extends Annotation,T> {
  void initialize(A constraintAnnotation, Type resultType);
  T readFrom(TranslationContext context);
  void writeInto(TranslationContext context, Object sourceValue);
}
