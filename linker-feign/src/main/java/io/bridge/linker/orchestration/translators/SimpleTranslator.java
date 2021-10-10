package io.bridge.linker.orchestration.translators;

import io.bridge.linker.common.utils.BeanUtils;
import io.bridge.linker.feign.Types;
import io.bridge.linker.orchestration.annotation.Simple;
import io.bridge.linker.orchestration.context.TranslationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SimpleTranslator<T> extends AbstractTranslator<Simple, T> implements Translator<Simple, T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTranslator.class);
  private String dataId;
  private Type resultType;

  @Override
  public void initialize(Simple constraintAnnotation, Type resultType) {
    this.dataId = constraintAnnotation.dataId();
    if(resultType instanceof ParameterizedType && (((ParameterizedType) resultType).getRawType() == Mono.class || ((ParameterizedType) resultType).getRawType() == Flux.class)){
      if(((ParameterizedType) resultType).getRawType() == Mono.class){
        this.resultType = ((ParameterizedType) resultType).getActualTypeArguments()[0];
      }else{
        this.resultType = new Types.ParameterizedTypeImpl(null,List.class,((ParameterizedType) resultType).getActualTypeArguments());
      }
    }else {
      this.resultType = resultType;
    }
  }

  @Override
  public T readFrom(TranslationContext context) {
    Object obj = context.getData(dataId);
    try {
      return (T) convertValue(obj, resultType);
    } catch (IllegalAccessException | InstantiationException e) {
      LOGGER.error("read value from context error.", e);
    }
    return null;
  }

  @Override
  public void writeInto(TranslationContext context, Object sourceValue) {
    try {
      Object targetValue = convertValue(sourceValue, resultType);
      context.putData(dataId,targetValue);
    } catch (IllegalAccessException | InstantiationException e) {
      LOGGER.error("write value into context error.", e);
    }
  }

  @Override
  public Object convertValue(Object value, Type targetClass) throws IllegalAccessException, InstantiationException {
    if (value == null) {
      return null;
    }
    if (((targetClass instanceof ParameterizedType || BeanUtils.isComplexValueType((Class) targetClass)) && BeanUtils.isSimpleValueType(value.getClass()))
            || (BeanUtils.isComplexValueType(value.getClass()) && !(targetClass instanceof ParameterizedType || BeanUtils.isComplexValueType((Class)targetClass)))) {
      return null;
    }
    if (targetClass instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) targetClass;
      String rawTypeName = parameterizedType.getRawType().getTypeName();
      Type actualType = parameterizedType.getActualTypeArguments()[0];
      if (rawTypeName.equals(List.class.getName())) {
        List result = new ArrayList<>();
        if (value instanceof List) {
          List valueList = (List) value;
          for (Object object : valueList) {
            Object objValue = convertValue(object, actualType);
            if (objValue != null) {
              result.add(objValue);
            }
          }
        } else {
          Object objValue = convertValue(value, actualType);
          if (objValue != null) {
            result.add(objValue);
          }
        }
        return result;
      } else {
        return null;
      }
    } else if (BeanUtils.isComplexValueType((Class)targetClass)) {
      if(value.getClass().getName().equals(((Class)targetClass).getName())){
        return value;
      }
      Object result = ((Class) targetClass).newInstance();
      Map<String, Field> targetClassFieldMap = getClassFields((Class) targetClass);
      Map<String, Field> valueFieldMap = getClassFields(value.getClass());
      boolean hasFieldValue = false;
      for (Map.Entry<String, Field> entry : valueFieldMap.entrySet()) {
        String fieldName = entry.getKey();
        Field srcfield = entry.getValue();
        Object sourceFieldValue;
        srcfield.setAccessible(true);
        sourceFieldValue = srcfield.get(value);
        Field targetField = targetClassFieldMap.get(fieldName);
        if (targetField != null) {
          Object targetValue = convertValue(sourceFieldValue, targetField.getGenericType());
          if (targetValue != null) {
            hasFieldValue = true;
            targetField.setAccessible(true);
            targetField.set(result, targetValue);
          }
        }
      }
      if (hasFieldValue) {
        return result;
      } else {
        return null;
      }
    } else {
      return super.convertValue(value, targetClass);
    }
  }
}
