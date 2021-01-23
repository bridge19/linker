package io.bridge.linker.orchestration.context;

import java.util.HashMap;
import java.util.Map;

public class TranslationContext {
  private Map<String,Object> objectMap = new HashMap<>();

  public Object getData(String dataId){
    return objectMap.get(dataId);
  }

  public void putData(String dataId, Object obj){
    objectMap.put(dataId,obj);
  }
}
