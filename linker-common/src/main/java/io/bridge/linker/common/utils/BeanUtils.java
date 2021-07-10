package io.bridge.linker.common.utils;

import java.util.Date;

/**
 * 功能说明：
 *
 */
public class BeanUtils {

  /** 判断是否是简单的"值"类型 */
  public static boolean isSimpleValueType(Class<?> clazz) {
    return (ClassUtils.isPrimitiveOrWrapper(clazz)
        || Enum.class.isAssignableFrom(clazz)
        || CharSequence.class.isAssignableFrom(clazz)
        || Number.class.isAssignableFrom(clazz)
        || Date.class.isAssignableFrom(clazz));
  }

  public static boolean isComplexValueType(Class<?> clazz) {
    return !isSimpleValueType(clazz);
  }
}
