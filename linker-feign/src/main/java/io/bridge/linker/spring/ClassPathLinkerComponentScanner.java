package io.bridge.linker.spring;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

public class ClassPathLinkerComponentScanner extends ClassPathBeanDefinitionScanner {

  private ScopeMetadataResolver scopeMetadataResolver;

  private Class<? extends Annotation>[] annotationClasses;

  private LinkerFactoryBean<?> linkerFactoryBean = new LinkerFactoryBean<Object>();

  public void setAnnotationClass(Class<? extends Annotation>[] annotationClasses) {
    this.annotationClasses = annotationClasses;
  }

  public ClassPathLinkerComponentScanner(BeanDefinitionRegistry registry) {
    super(registry);
  }

  public void registerFilters() {
    // if specified, use the given annotation and / or marker interface
    if (this.annotationClasses != null && this.annotationClasses.length>0) {
      for (Class<? extends Annotation> annotationClass : annotationClasses) {
        addIncludeFilter(new AnnotationTypeFilter(annotationClass));
      }
    }
  }

  @Override
  public Set<BeanDefinitionHolder> doScan(String... basePackages) {
    Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

    if (beanDefinitions.isEmpty()) {
      logger.warn(
          "No Linker Component definitions found in '"
              + Arrays.toString(basePackages)
              + "' package. Please check your configuration.");
    } else {
      logger.warn(
          "Process Linker Component definitions found in '"
              + Arrays.toString(basePackages)
              + "' package.");
      processBeanDefinitions(beanDefinitions);
    }

    return beanDefinitions;
  }

  public void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
    GenericBeanDefinition definition;
    for (BeanDefinitionHolder holder : beanDefinitions) {
      definition = (GenericBeanDefinition) holder.getBeanDefinition();

      if (logger.isDebugEnabled()) {
        logger.debug(
            "Creating LinkerFactoryBean with name '"
                + holder.getBeanName()
                + "' and '"
                + definition.getBeanClassName()
                + "' Linker Component");
      }
      definition
          .getConstructorArgumentValues()
          .addGenericArgumentValue(definition.getBeanClassName());
      definition.setBeanClass(this.linkerFactoryBean.getClass());
    }
  }

  @Override
  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    Set<String> annots =  beanDefinition.getMetadata().getAnnotationTypes();
    for(Class<? extends Annotation> anno : this.annotationClasses){
      if(annots.contains(anno.getName())){
        return true;
      }
    }
    return false;
  }

  @Override
  protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
    if (super.checkCandidate(beanName, beanDefinition)) {
      return true;
    } else {
      logger.warn(
          "Skipping Class with name '"
              + beanName
              + "' and '"
              + beanDefinition.getBeanClassName()
              + "'. Bean already defined with the same name!");
      return false;
    }
  }

//  public void registerBeanDefinitions(Map<String,Class> classMap){
//    if(classMap == null || classMap.size()==0)
//      return;
//    Set<BeanDefinitionHolder> beanDefinitionHolders = new HashSet<>();
//    Iterator<Map.Entry<String, Class>> it = classMap.entrySet().iterator();
//    while(it.hasNext()){
//      Map.Entry<String, Class> entry = it.next();
//      GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
//      beanDefinition.setBeanClass(entry.getValue());
//      beanDefinition.setScope("singleton");
//      BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition,entry.getKey());
//      beanDefinitionHolders.add(beanDefinitionHolder);
//    }
//    processBeanDefinitions(beanDefinitionHolders);
//  }
}
