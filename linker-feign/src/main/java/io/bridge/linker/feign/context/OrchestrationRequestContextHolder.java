package io.bridge.linker.feign.context;

import java.util.HashMap;
import java.util.Map;

public class OrchestrationRequestContextHolder {

  /**
   * 线程池使用：
   * <dependency>
   *   <groupId>com.alibaba</groupId>
   *   <artifactId>transmittable-thread-local</artifactId>
   * </dependency>
   */
  private static final ThreadLocal<Map<String, Object>> colorValue = new InheritableThreadLocal<>();
//  private static final ThreadLocal<Map<String, Object>> currentMethodParamValue = new ThreadLocal<>();

  public static Object getColor(String color){
    return getMap().get(color);
  }

  public static Object setColor(String color, Object value){
    return getMap().put(color,value);
  }

//  public static Map<String, Object> getCurrentMethodParamValue(){
//    return currentMethodParamValue.get();
//  }
//
//  public static void setCurrentMethodParamValue(Map<String, Object> paramValue){
//    currentMethodParamValue.set(paramValue);
//  }

  private static Map<String,Object> getMap(){
    Map<String,Object> map = colorValue.get();
    if(map == null){
      map = new HashMap<>();
      colorValue.set(map);
    }
    return map;
  }

  public static void remove(){
    colorValue.remove();
  }

//  public static void removeCurrentMethodParamValue(){
//    currentMethodParamValue.remove();
//  }
}
