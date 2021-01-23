package io.bridge.linker.orchestration.translators;

import io.bridge.linker.common.exception.LinkerRuntimeException;
import io.bridge.linker.orchestration.context.TranslationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class StringConcatTranslator<T>  extends AbstractTranslator<Concat,T>  implements Translator<Concat,T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(StringConcatTranslator.class);
  private String[] dataIds;
  private String connectedBy;
  private Type resultType;

  @Override
  public void initialize(Concat constraintAnnotation, Type resultType) {
    this.dataIds = constraintAnnotation.dataIds();
    this.connectedBy = constraintAnnotation.connectedBy();
    this.resultType = resultType;
  }

  @Override
  public T readFrom(TranslationContext context) {
    StringBuilder sb = new StringBuilder(128);
    String result = null;
    for(String dataId: dataIds){
      Object obj = context.getData(dataId);
      if(obj == null || "".equals(obj) || !(obj instanceof String)){
        break;
      }
      try {
        sb.append(convertValue(obj,String.class)).append(connectedBy);
      } catch (IllegalAccessException |InstantiationException e) {
        LOGGER.error("read value from context error.",e);
      }
    }
    if(sb.length()>0 && connectedBy != null && connectedBy.length() > 0) {
      result =sb.substring(0,sb.length()-connectedBy.length());
    }
    return (T)result;
  }

  @Override
  public void writeInto(TranslationContext context, Object sourceValue) {
    throw new LinkerRuntimeException("unsupported operation.");
  }
}
