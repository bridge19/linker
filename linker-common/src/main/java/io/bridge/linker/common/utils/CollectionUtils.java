package io.bridge.linker.common.utils;

import java.util.Collection;

/**
 * 功能说明：
 *
 */
public class CollectionUtils {
  private CollectionUtils() {}

  public static boolean isEmpty(Collection coll) {
    return (coll == null || coll.isEmpty());
  }

  public static boolean isNotEmpty(Collection coll) {
    return !isEmpty(coll);
  }
}
