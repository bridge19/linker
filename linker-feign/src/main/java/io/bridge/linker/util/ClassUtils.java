package io.bridge.linker.util;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 功能说明：
 *
 */
public class ClassUtils {

  private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

  static {
    primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
    primitiveWrapperTypeMap.put(Byte.class, byte.class);
    primitiveWrapperTypeMap.put(Character.class, char.class);
    primitiveWrapperTypeMap.put(Double.class, double.class);
    primitiveWrapperTypeMap.put(Float.class, float.class);
    primitiveWrapperTypeMap.put(Integer.class, int.class);
    primitiveWrapperTypeMap.put(Long.class, long.class);
    primitiveWrapperTypeMap.put(Short.class, short.class);
  }

  public static boolean isPrimitiveWrapper(Class<?> clazz) {
    return primitiveWrapperTypeMap.containsKey(clazz);
  }

  /**
   * primitive:(i.e. boolean, byte,char, short, int, long, float, or double) primitive wrapper:(i.e.
   * Boolean, Byte, Character, Short, Integer, Long, Float, or Double).
   */
  public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
    return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
  }

  public static Class<?> forName(String name, ClassLoader classLoader)
      throws ClassNotFoundException, LinkageError {

    ClassLoader clToUse = classLoader;
    if (clToUse == null) {
      clToUse = getDefaultClassLoader();
    }
    try {
      return (clToUse != null ? clToUse.loadClass(name) : Class.forName(name));
    } catch (ClassNotFoundException ex) {
      throw ex;
    }
  }

  public static ClassLoader getDefaultClassLoader() {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    } catch (Throwable ex) {
      // Cannot access thread context ClassLoader - falling back...
    }
    if (cl == null) {
      // No thread context class loader -> use class loader of this class.
      cl = ClassUtils.class.getClassLoader();
      if (cl == null) {
        // getClassLoader() returning null indicates the bootstrap ClassLoader
        try {
          cl = ClassLoader.getSystemClassLoader();
        } catch (Throwable ex) {
          // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
        }
      }
    }
    return cl;
  }
}
