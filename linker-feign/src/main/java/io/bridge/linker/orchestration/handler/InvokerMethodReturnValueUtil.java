package io.bridge.linker.orchestration.handler;

import io.bridge.linker.common.exception.LinkerRuntimeException;
import io.bridge.linker.orchestration.context.TranslationContext;
import io.bridge.linker.orchestration.translators.Translator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class InvokerMethodReturnValueUtil {
  private static Map<Class,InvokerMethodReturnType> typeMap = new HashMap<>();

  public static void registerType(Class returnClass){
    typeMap.put(returnClass,new InvokerMethodReturnType(returnClass));
  }

  public static Object generateValue(TranslationContext context, Class returnClass){
    Object resultInstance = null;
    try {
      resultInstance = returnClass.newInstance();
      Map<Field, Translator<? extends Annotation, ?>> fieldTranslatorMap = typeMap.get(returnClass).getFieldTranslatorMap();
      if (fieldTranslatorMap != null) {
        Iterator<Map.Entry<Field, Translator<? extends Annotation,?>>> iterator = fieldTranslatorMap.entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry<Field, Translator<? extends Annotation,?>> entry = iterator.next();
          Field field = entry.getKey();
          Translator<? extends Annotation,?> translator = entry.getValue();
          Type fieldType = field.getGenericType();
          if (translator != null) {
            Object fieldValue = translator.readFrom(context);
            if (fieldValue == null) {
              continue;
            }
            field.setAccessible(true);
            field.set(resultInstance, fieldValue);
          }
        }
      }
    } catch (IllegalAccessException | InstantiationException e) {
      throw new LinkerRuntimeException(String.format("create instance error for class {}", returnClass.getName()), e);
    }
    return resultInstance;
  }
}
