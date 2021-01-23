package io.bridge.linker.orchestration.translators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTranslator<A extends Annotation,T> implements Translator<A,T> {
  private static Map<Class, Map<String, Field>> classFieldsMap = new HashMap<>();
  protected Object convertValue(Object value, Type targetClass) throws IllegalAccessException, InstantiationException {
    if (value == null) {
      return null;
    }
    Class valueClass = value.getClass();
    if (valueClass == targetClass) {
      return value;
    }
    if (value instanceof Number) {
      if (targetClass == String.class) {
        return String.valueOf(value);
      } else if (targetClass == Date.class) {
        return new Date(((Number) value).longValue());
      } else if (targetClass == Integer.class) {
        return ((Number) value).intValue();
      } else if (targetClass == Long.class) {
        return ((Number) value).longValue();
      } else if (targetClass == Double.class) {
        return ((Number) value).doubleValue();
      } else if (targetClass == Float.class) {
        return ((Number) value).floatValue();
      } else if (targetClass == Byte.class) {
        return ((Number) value).byteValue();
      } else {
        throw new RuntimeException("unsupported type conversion :" + targetClass.getTypeName());
      }
    } else if (value instanceof String) {
      String oValue = (String) value;
      if (targetClass == Integer.class) {
        return Integer.valueOf(oValue);
      } else if (targetClass == Long.class) {
        return Long.valueOf(oValue);
      } else if (targetClass == Double.class) {
        return Double.valueOf(oValue);
      } else if (targetClass == Float.class) {
        return Float.valueOf(oValue);
      } else if (targetClass == Byte.class) {
        return Byte.valueOf(oValue);
      } else {
        throw new RuntimeException("unsupported type conversion :" + targetClass.getTypeName());
      }
    } else {
      throw new RuntimeException("unsupported type conversion:" + targetClass.getTypeName());
    }
  }

  protected Map<String, Field> getClassFields(Class clazz) {
    Map<String, Field> classFields = classFieldsMap.get(clazz);
    if (classFields == null) {
      Field[] fields = clazz.getDeclaredFields();
      if (fields.length == 0) {
        return null;
      }
      classFields = new HashMap<>();
      for (Field field : fields) {
        classFields.put(field.getName(), field);
      }
      classFieldsMap.put(clazz, classFields);
    }
    return classFields;
  }
}
