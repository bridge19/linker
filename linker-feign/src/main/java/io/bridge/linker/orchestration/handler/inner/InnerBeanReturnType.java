package io.bridge.linker.orchestration.handler.inner;

import io.bridge.linker.orchestration.annotation.Simple;
import io.bridge.linker.orchestration.handler.InvokerMethodReturnType;
import io.bridge.linker.orchestration.translators.Script;
import io.bridge.linker.orchestration.translators.ScriptTranslator;
import io.bridge.linker.orchestration.translators.SimpleTranslator;
import io.bridge.linker.orchestration.translators.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class InnerBeanReturnType {
  private static final Logger LOGGER = LoggerFactory.getLogger(InvokerMethodReturnType.class);
  private Type returnType;
  private Map<Field, Translator> fieldTranslatorMap = new HashMap<>();
  private Translator resultTranslator;

  public InnerBeanReturnType(Type returnType){
    this.returnType = returnType;
    Class returnClass = null;
    if(returnType instanceof ParameterizedType){
      returnClass = (Class) ((ParameterizedType) returnType).getActualTypeArguments()[0];
    }else {
      returnClass = (Class) returnType;
    }
    Field[] fields = returnClass.getDeclaredFields();
    for(Field field : fields){
      Simple simpleAnno = field.getAnnotation(Simple.class);
      if(simpleAnno != null){
        SimpleTranslator simpleTranslator = new SimpleTranslator();
        simpleTranslator.initialize(simpleAnno,field.getGenericType());
        fieldTranslatorMap.put(field,simpleTranslator);
      }else {
        Script scriptAnno = field.getAnnotation(Script.class);
        if(scriptAnno != null) {
          ScriptTranslator scriptTranslator = new ScriptTranslator();
          scriptTranslator.initialize(scriptAnno,field.getGenericType());
          fieldTranslatorMap.put(field,scriptTranslator);
        }
      }
    }
    Simple resultAnno = (Simple) returnClass.getAnnotation(Simple.class);
    if(resultAnno != null){
      resultTranslator = new SimpleTranslator();
      resultTranslator.initialize(resultAnno,returnType);
    }
  }

  public Map<Field, Translator> getFieldTranslatorMap() {
    return fieldTranslatorMap;
  }

  public Translator getResultTranslator() {
    return resultTranslator;
  }
}
