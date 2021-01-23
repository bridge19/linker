package io.bridge.linker.orchestration.handler;

import io.bridge.linker.orchestration.translators.Constraint;
import io.bridge.linker.orchestration.translators.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class InvokerMethodReturnType {

  private static final Logger LOGGER = LoggerFactory.getLogger(InvokerMethodReturnType.class);
  private Class returnType;
  private Map<Field, Translator<? extends Annotation, ?>> fieldTranslatorMap = new HashMap<>();

  public InvokerMethodReturnType(Class returnType) {
    this.returnType = returnType;
    Field[] fields = returnType.getDeclaredFields();
    for (Field field : fields) {
      Annotation[] annotations = field.getAnnotations();
      for (Annotation annotation : annotations) {
        Annotation constraintAnnotation = annotation.annotationType().getAnnotation(Constraint.class);
        if (constraintAnnotation != null) {
          Constraint constraint = (Constraint) constraintAnnotation;
          Class<? extends Translator> translatorClass = constraint.translatedBy();
          Translator translator = null;
          try {
            translator = translatorClass.newInstance();
            translator.initialize(annotation, field.getGenericType());
            fieldTranslatorMap.put(field, translator);
          } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.warn("init instance error for Class: " + translatorClass.getName());
          }
        }
      }
    }
  }

  public Class getReturnType() {
    return returnType;
  }

  public Map<Field, Translator<? extends Annotation, ?>> getFieldTranslatorMap() {
    return fieldTranslatorMap;
  }
}
