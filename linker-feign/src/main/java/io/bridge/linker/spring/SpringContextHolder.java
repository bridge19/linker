package io.bridge.linker.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHolder implements ApplicationContextAware {
  private static ApplicationContext appContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    appContext = applicationContext;
  }

  public static ApplicationContext getAppContext() {
    return appContext;
  }

  public static <T> T getBean(Class<T> targetClass){
    return appContext.getBean(targetClass);
  }
  public static <T> T getBean(String componentName, Class<T> targetClass){
    return appContext.getBean(componentName,targetClass);
  }
}
