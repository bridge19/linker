package io.bridge.linker.orchestration.handler;

import io.bridge.linker.orchestration.annotation.DataId;
import io.bridge.linker.orchestration.context.TranslationContext;
import io.bridge.linker.util.ArrayUtils;
import io.bridge.linker.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InvokerMethodArg {
  private static final Logger LOGGER = LoggerFactory.getLogger(InvokerMethodArg.class);

  private Class<?> paramType;
  private DataId dataId;
  private Map<Field, InvokerMethodArg> MethodArgsMap;

  public InvokerMethodArg(Class paramType){
    this.paramType = paramType;
  }
  public void init() {
    if(BeanUtils.isComplexValueType(this.paramType)){
      Field[] fields = this.paramType.getDeclaredFields();
      if(ArrayUtils.isNotEmpty(fields)){
        MethodArgsMap = new HashMap<>();
        for(Field field : fields){
          DataId dataId = field.getAnnotation(DataId.class);
          InvokerMethodArg MethodArg = new InvokerMethodArg(field.getType());
          MethodArg.setDataId(dataId);
          MethodArgsMap.put(field,MethodArg);
        }
      }
    }
  }

  public void setParam(TranslationContext context, Object paramValue) {
    if(BeanUtils.isComplexValueType(this.paramType)){
      if(MethodArgsMap != null){
        Iterator<Map.Entry<Field, InvokerMethodArg>> it = MethodArgsMap.entrySet().iterator();
        while (it.hasNext()){
          Map.Entry<Field, InvokerMethodArg> entry = it.next();
          Field field = entry.getKey();
          InvokerMethodArg methodArg = entry.getValue();
          field.setAccessible(true);
          try {
            Object value = field.get(paramValue);
            methodArg.setParam(context,value);
          } catch (IllegalAccessException e) {
            LOGGER.warn("get field Value error.",e);
          }
        }
      }
    }else{
      if (dataId != null) {
        context.putData(dataId.value(), paramValue);
      }
    }
  }

  public DataId getDataId() {
    return dataId;
  }

  public void setDataId(DataId dataId) {
    this.dataId = dataId;
  }
}
