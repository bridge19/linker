package io.bridge.linker.orchestration.handler.inner;

import io.bridge.linker.common.exception.LinkerRuntimeException;
import io.bridge.linker.orchestration.context.TranslationContext;
import io.bridge.linker.orchestration.translators.Translator;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InnerBeanReturnValueUtil {
  private static Map<Type,InnerBeanReturnType> typeMap = new HashMap<>();

  public static void registerType(Type returnType) {
    typeMap.put(returnType,new InnerBeanReturnType(returnType));
  }

  public static void releaseValue(TranslationContext context, Type returnType, Object result){
    if(result == null){
      return;
    }
    try {
      InnerBeanReturnType innerBeanReturnType = typeMap.get(returnType);
      Translator resultTranslator = innerBeanReturnType.getResultTranslator();
      Map<Field,Translator> fieldTranslatorMap = innerBeanReturnType.getFieldTranslatorMap();
      if(resultTranslator != null){
        resultTranslator.writeInto(context,result);
      }else if(fieldTranslatorMap != null){
        Iterator<Map.Entry<Field, Translator>> iterator = fieldTranslatorMap.entrySet().iterator();
        while (iterator.hasNext()){
          Map.Entry<Field, Translator> entry = iterator.next();
          Field field = entry.getKey();
          Translator translator = entry.getValue();
          if (translator != null) {
            field.setAccessible(true);
            Object fieldValue = field.get(result);
            translator.writeInto(context,fieldValue);
          }
        }
      }
    } catch (IllegalAccessException e) {
      throw new LinkerRuntimeException(String.format("create instance error for class {}",returnType.getTypeName()),e);
    }
  }
}
