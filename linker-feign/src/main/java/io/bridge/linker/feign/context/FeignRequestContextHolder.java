package io.bridge.linker.feign.context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 功能说明：
 *
 */
public class FeignRequestContextHolder {
  private static final ThreadLocal<Map<String, Object>> requestParams =
      ThreadLocal.withInitial(new HashMapSupplier());
  private static final ThreadLocal<Map<String, Object>> requestHeaders =
      ThreadLocal.withInitial(new HashMapSupplier());

  private static class HashMapSupplier implements Supplier<Map<String, Object>> {
    @Override
    public Map<String, Object> get() {
      return new HashMap<>(16);
    }
  }

  public static Object getParamValue(String param) {
    return requestParams.get().get(param);
  }

  public static void putParamValue(String param, Object value) {
    requestParams.get().put(param, value);
  }

  public static void removeParamValues() {
    requestParams.remove();
  }
  public static Object getHeaderValue(String param) {
    return requestHeaders.get().get(param);
  }

  public static void putHeaderValue(String param, Object value) {
    requestHeaders.get().put(param, value);
  }

  public static void removeHeaderValues() {
    requestHeaders.remove();
  }
  public static void clear(){
    requestParams.remove();
    requestHeaders.remove();
  }
}
