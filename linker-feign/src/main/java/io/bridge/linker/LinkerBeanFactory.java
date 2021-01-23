package io.bridge.linker;

import com.google.common.base.Preconditions;
import io.bridge.linker.common.integration.ConfigurationUtil;
import io.bridge.linker.common.integration.ServiceConfig;
import io.bridge.linker.annotation.FeignModule;
import io.bridge.linker.annotation.OrcheModule;
import io.bridge.linker.orchestration.proxy.OrchestrationProxy;
import io.bridge.linker.feign.FeignProxy;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class LinkerBeanFactory {

  private static final org.slf4j.Logger LOGGER =
      LoggerFactory.getLogger(LinkerBeanFactory.class);

  private static ConcurrentHashMap<Class, Object> instanceMap = new ConcurrentHashMap<>();

  public LinkerBeanFactory() {
  }

  public static <T> T getLinkerModule(Class<T> type) {
    T instance = (T) instanceMap.get(type);
    if (instance == null) {
      LOGGER.info("create dynamic class for linker interface: " + type.getName());
      FeignModule feignModule = type.getAnnotation(FeignModule.class);
      OrcheModule orcheModule = type.getAnnotation(OrcheModule.class);
      Preconditions.checkState(
          feignModule != null || orcheModule !=null,
          "FeignModule or OrcheModule Annotation needed for interface:" + type.getName());
      if (feignModule != null) {
        String serviceName = feignModule.service();
        Preconditions.checkState(
            serviceName != null, "api name is not specified for :" + type.getName());
        ServiceConfig config = ConfigurationUtil.getServiceConfig(serviceName);
        Preconditions.checkState(
            config != null, "configuration is not found for service :" + serviceName);
        instance = FeignProxy.newInstance(config, type);
      }else {
        instance = OrchestrationProxy.newInstance(type);
      }
      if(instance != null) {
        instanceMap.putIfAbsent(type, instance);
      }
    }
    return instance;
  }
}
