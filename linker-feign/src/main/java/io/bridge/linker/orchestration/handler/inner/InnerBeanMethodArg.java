package io.bridge.linker.orchestration.handler.inner;

import io.bridge.linker.orchestration.annotation.DataId;
import io.bridge.linker.orchestration.context.TranslationContext;
import io.bridge.linker.common.utils.ArrayUtils;
import io.bridge.linker.common.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InnerBeanMethodArg<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(InnerBeanMethodArg.class);

  private Class<T> paramType;
  private DataId dataId;
  private Map<Field, InnerBeanMethodArg> innerBeanMethodArgMap;

  public InnerBeanMethodArg(Class paramType){
    this.paramType = paramType;
  }
  public void init() {
    if(BeanUtils.isComplexValueType(this.paramType)){
      Field[] fields = this.paramType.getDeclaredFields();
      if(ArrayUtils.isNotEmpty(fields)){
        innerBeanMethodArgMap = new HashMap<>();
        for(Field field : fields){
          DataId dataId = field.getAnnotation(DataId.class);
          InnerBeanMethodArg feignProxyMethodArg = new InnerBeanMethodArg(field.getType());
          feignProxyMethodArg.setDataId(dataId);
          innerBeanMethodArgMap.put(field,feignProxyMethodArg);
        }
      }
    }
  }


  public T getParamValue(TranslationContext context) {
    T paramValue = null;
    if(BeanUtils.isComplexValueType(this.paramType)){
      if(innerBeanMethodArgMap != null){
        try {
          paramValue = paramType.newInstance();
          Iterator<Map.Entry<Field, InnerBeanMethodArg>> it = innerBeanMethodArgMap.entrySet().iterator();
          while (it.hasNext()){
            Map.Entry<Field, InnerBeanMethodArg> entry = it.next();
            Field field = entry.getKey();
            InnerBeanMethodArg proxyMethodArg = entry.getValue();
            field.setAccessible(true);
            Object value = proxyMethodArg.getParamValue(context);
            field.set(paramValue,value);
          }
        } catch (InstantiationException |IllegalAccessException e) {
          LOGGER.warn("create new instance error."+paramType.getName(),e);
        }

      }
    }else{
      if (dataId != null) {
        paramValue = (T) context.getData(dataId.value());
      }
    }
    return  paramValue;
  }

  public DataId getDataId() {
    return dataId;
  }

  public void setDataId(DataId dataId) {
    this.dataId = dataId;
  }
}
