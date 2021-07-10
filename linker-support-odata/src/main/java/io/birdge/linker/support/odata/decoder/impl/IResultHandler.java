package io.birdge.linker.support.odata.decoder.impl;

import com.alibaba.fastjson.JSONObject;
import io.bridge.linker.common.exception.LinkerRuntimeException;
import io.bridge.linker.feign.decoder.annotation.Flatten;
import io.bridge.linker.feign.decoder.annotation.Merging;
import io.bridge.linker.feign.decoder.annotation.Original;
import io.bridge.linker.common.utils.ArrayUtils;
import io.bridge.linker.common.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public interface IResultHandler {
  Logger LOGGER = LoggerFactory.getLogger(IResultHandler.class);

  Object parse(JSONObject jsonObj);

  default Object convert(Object instance, List<WrappedField> wrappedFields,
                         JSONObject jsonObject) {
    boolean hasFieldValue = false;
    for (WrappedField wrappedField : wrappedFields) {
      try {
        LOGGER.info("set field value for: " + wrappedField.getField().getName());
        Object object = wrappedField.getFieldHandler().parse(jsonObject);
        if (object != null) {
          hasFieldValue = true;
          wrappedField.getValueSetter().invoke(instance, object);
        }
      } catch (IllegalAccessException | InvocationTargetException e) {
        LOGGER.error("invoke error", e);
      }
    }
    if (hasFieldValue) {
      return instance;
    } else {
      return null;
    }
  }

  default List<WrappedField> analyze(Type resultType) {
    Class resultClass = null;
    if (resultType instanceof ParameterizedType) {
      Type rawType = ((ParameterizedType) resultType).getRawType();
      if (!rawType.getTypeName().equals(List.class.getTypeName())) {
        throw new LinkerRuntimeException("not supported for generic type: " + rawType.getTypeName());
      }
      resultClass = (Class) ((ParameterizedType) resultType).getActualTypeArguments()[0];
    } else {
      resultClass = (Class) resultType;
    }
    if (BeanUtils.isSimpleValueType(resultClass)) {
      return new ArrayList<>();
    }
    List<WrappedField> wrappedFields = new ArrayList<>();
    Field[] fields = resultClass.getDeclaredFields();
    if (ArrayUtils.isEmpty(fields)) {
      throw new LinkerRuntimeException("not one field found for class:" + resultClass.getName());
    }
    WrappedField wrappedField = null;
    for (Field field : fields) {
      if (field.isSynthetic()) {
        continue;
      }
      String fieldName = field.getName();
      Annotation[] annotations = field.getAnnotations();
      wrappedField = new WrappedField();
      wrappedFields.add(wrappedField);
      wrappedField.setField(field);
      try {
        wrappedField.setValueSetter(resultClass
            .getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1),
                field.getType()));
      } catch (NoSuchMethodException e) {
        throw new LinkerRuntimeException(
            "not setter method found for field: " + fieldName + " class:" + resultClass.getName(),
            e);
      }
      if (ArrayUtils.isEmpty(annotations)) {
        //默认值
        wrappedField.setFieldHandler(new OriginalFieldHandler(field.getGenericType(), fieldName));
      } else {
        for (Annotation annotation : annotations) {
          if (annotation instanceof Original) {
            wrappedField.setFieldHandler(
                new OriginalFieldHandler(field.getGenericType(), ((Original) annotation).value()));
            break;
          } else if (annotation instanceof Flatten) {
            wrappedField.setFieldHandler(new FlattenFieldHandler(field.getGenericType(),
                ((Flatten) annotation).value().split("\\.")));
            break;
          } else if (annotation instanceof Merging) {
            String[] values = ((Merging) annotation).value().split("\\+");
            List<IFieldHandler> fieldHandlers = new ArrayList<>();
            for (String value : values) {
              if (value.indexOf('.') > 0) {
                fieldHandlers.add(new FlattenFieldHandler(String.class, value.split("\\.")));
              } else {
                fieldHandlers.add(new OriginalFieldHandler(String.class, value));
              }
            }
            wrappedField.setFieldHandler(new MergingFieldHandler(fieldHandlers));
            break;
          }
        }
      }
    }
    return wrappedFields;
  }
}