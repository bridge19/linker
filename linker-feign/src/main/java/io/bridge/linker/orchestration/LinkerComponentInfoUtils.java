package io.bridge.linker.orchestration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class LinkerComponentInfoUtils {

//  /**
//   * relationship for feign classes and service,
//   * set while loading feign classes,
//   * using while loading orche classes
//   */
//  private static Map<Class,String> classServiceNameMap = new HashMap<>();
//  /**
//   * relationship for feign Class and methods
//   * set while loading feign classes
//   */
//  private static Map<Class,Map<String, Method>> linkerClassMap = new HashMap<>();
//  /**
//   * relationship for local Class and methods, which used in orche classes.
//   */
//  private static Map<Class,Map<String, Method>> localClassMap = new HashMap<>();
//  /**
//   * relationship for return type and fields
//   */
  private static Map<Class,Map<String, Field>> fieldsMap = new HashMap<>();
  private static Map<Class,Map<String, Method>> methodsMap = new HashMap<>();

//  private static Map<Class,Map<String, MethodHandler>> linkerExecuteMethodMap = new HashMap<>();

  public static Map<String, Field> getFieldsMap(Class clazz){
    Map<String, Field> fieldMap = fieldsMap.get(clazz);
    if(fieldMap == null){
      fieldMap = new HashMap<>();
      Field[] fields = clazz.getDeclaredFields();
      for(Field field : fields){
        String fName = field.getName();
        fieldMap.put(fName, field);
      }
      fieldsMap.put(clazz,fieldMap);
    }
    return fieldMap;
  }

  public static Field getFieldByName(Class clazz, String fieldname){
    Map<String, Field> fieldMap = getFieldsMap(clazz);
    return fieldMap.get(fieldname);
  }

  public static Map<String, Method> getMethodsMap(Class clazz){
    Map<String, Method> methodMap = methodsMap.get(clazz);
    if(methodMap == null){
      methodMap = new HashMap<>();
      Method[] methods = clazz.getDeclaredMethods();
      for(Method method : methods){
        String mName = method.getName();
        methodMap.put(mName, method);
      }
      methodsMap.put(clazz,methodMap);
    }
    return methodMap;
  }

  public static Method getMethodByName(Class clazz, String methodName){
    Map<String, Method> methodMap = getMethodsMap(clazz);
    return methodMap.get(methodName);
  }
}
