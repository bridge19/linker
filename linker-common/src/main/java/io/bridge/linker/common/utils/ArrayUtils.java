package io.bridge.linker.common.utils;

/**
 * 功能说明：
 *
 */
public class ArrayUtils {
  private ArrayUtils() {}

  public static boolean isEmpty(Object[] array) {
    return array == null || array.length == 0;
  }

  public static boolean isNotEmpty(Object[] array) {
    return !isEmpty(array);
  }
}
