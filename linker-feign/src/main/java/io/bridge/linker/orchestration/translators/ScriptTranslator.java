package io.bridge.linker.orchestration.translators;

import io.bridge.linker.orchestration.context.TranslationContext;
import io.bridge.linker.orchestration.script.ScriptEvaluator;
import io.bridge.linker.util.BeanUtils;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptTranslator<T> extends AbstractTranslator<Script, T> implements Translator<Script, T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ScriptTranslator.class);
  private String[] dataIds;
  private String[] scriptStrs;
  private Type resultType;

  @Override
  public void initialize(Script constraintAnnotation, Type resultType) {
    this.dataIds = constraintAnnotation.dataIds();
    this.scriptStrs = constraintAnnotation.scripts();
    this.resultType = resultType;
  }

  @Override
  public T readFrom(TranslationContext context) {
    Object[] params = new Object[dataIds.length];
    for (int i = 0; i < dataIds.length; i++) {
      params[i] = context.getData(dataIds[i]);
    }
    try {
      StringBuilder scriptStr = new StringBuilder();
      for (String s : scriptStrs) {
        scriptStr.append(s);
      }
      Object result = ScriptEvaluator.eval(scriptStr.toString(), params);
      return (T) convertValue(result, (Type) resultType);
    } catch (ScriptException | IllegalAccessException | InstantiationException e) {
      LOGGER.error("read value from context error.", e);
    }
    return null;
  }

  @Override
  public void writeInto(TranslationContext context, Object sourceValue) {
    try {
      String dataId = dataIds[0];
      StringBuilder scriptStr = new StringBuilder();
      for (String s : scriptStrs) {
        scriptStr.append(s);
      }
      Object targetValue = ScriptEvaluator.eval(scriptStr.toString(), sourceValue);
      context.putData(dataId,targetValue);
    } catch (ScriptException e) {
      LOGGER.error("write value into context error.", e);
    }
  }

  protected Object convertValue(Object value, Type targetType) throws InstantiationException, IllegalAccessException {
    if (value == null) {
      return null;
    }

    if (value instanceof ScriptObjectMirror) {
      ScriptObjectMirror scriptValue = (ScriptObjectMirror) value;
      if (scriptValue.isEmpty()) {
        return null;
      }
      if (!(targetType instanceof ParameterizedType) && BeanUtils.isSimpleValueType((Class) targetType)) {
        return null;
      }
      if (targetType instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) targetType;
        Type actualType = parameterizedType.getActualTypeArguments()[0];
        String rawTypeName = parameterizedType.getRawType().getTypeName();
        if (rawTypeName.equals(List.class.getName()) && scriptValue.isArray()) {
          List result = new ArrayList<>();
          for (Map.Entry<String, Object> entry : scriptValue.entrySet()) {
            result.add(convertValue(entry.getValue(),actualType));
          }
          return result;
        }else{
          return null;
        }
      } else {
        Object result = ((Class) targetType).newInstance();
        String[] keys = scriptValue.getOwnKeys(true);
        for (String key : keys) {
          Object scriptKeyValue = scriptValue.get(key);
          Map<String, Field> classFields = getClassFields((Class) targetType);
          Field field = classFields.get(key);
          if (field != null) {
            field.setAccessible(true);
            field.set(result, convertValue(scriptKeyValue, field.getGenericType()));
          }
        }
        return result;
      }
    } else {
      return super.convertValue(value, targetType);
    }
  }
}
