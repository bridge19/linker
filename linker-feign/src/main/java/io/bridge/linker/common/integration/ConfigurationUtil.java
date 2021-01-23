package io.bridge.linker.common.integration;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能说明：
 *
 */
public abstract class ConfigurationUtil {

  private static Map<String, ServiceConfig> serviceConfigMap = new HashMap();

  public static void addServiceConfig(String serviceName, ServiceConfig config) {
    serviceConfigMap.put(serviceName,config);
  }

  public static ServiceConfig getServiceConfig(String serviceName) {
    return serviceConfigMap.get(serviceName);
  }
}
