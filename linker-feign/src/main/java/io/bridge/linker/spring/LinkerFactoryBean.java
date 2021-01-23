package io.bridge.linker.spring;

import io.bridge.linker.LinkerBeanFactory;
import org.springframework.beans.factory.FactoryBean;

public class LinkerFactoryBean<T> implements FactoryBean<T> {
  private Class<T> linkerModuleClass;

  public LinkerFactoryBean() {}

  public LinkerFactoryBean(Class<T> linkerModuleClass) {
    this.linkerModuleClass = linkerModuleClass;
  }

  @Override
  public T getObject() throws Exception {
    return LinkerBeanFactory.getLinkerModule(linkerModuleClass);
  }

  @Override
  public Class<?> getObjectType() {
    return linkerModuleClass;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }
}
