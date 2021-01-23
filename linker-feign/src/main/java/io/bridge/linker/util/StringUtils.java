package io.bridge.linker.util;

/**
 * 功能说明：
 *
 */
public class StringUtils {
  private StringUtils() {}

  public static boolean isBlank(final CharSequence cs) {
    int strLen;
    if (cs == null || (strLen = cs.length()) == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (Character.isWhitespace(cs.charAt(i)) == false) {
        return false;
      }
    }
    return true;
  }

  public static boolean isNotBlank(final CharSequence cs) {
    return !isBlank(cs);
  }

  public static boolean hasLength(String str) {
    return (str != null && !str.isEmpty());
  }
}
