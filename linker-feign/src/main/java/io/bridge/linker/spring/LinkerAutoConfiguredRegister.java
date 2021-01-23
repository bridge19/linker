package io.bridge.linker.spring;

import io.bridge.linker.annotation.FeignModule;
import io.bridge.linker.common.exception.LinkerRuntimeException;
import io.bridge.linker.common.integration.ConfigurationUtil;
import io.bridge.linker.common.integration.ServiceConfig;
import io.bridge.linker.util.StringUtils;
import feign.Contract;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.reactor.client.ReactiveHttpRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.AnnotationMetadata;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

public class LinkerAutoConfiguredRegister
    implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LinkerAutoConfiguredRegister.class);

  private static final String PACKAGE_CONFIG = "META-INF/linker.configuration";
  private ResourceLoader resourceLoader;

  private ClassPathLinkerComponentScanner scanner = null;

  private List<String> basePackages = new ArrayList<String>();

  private static final String FEIGN_BASE_PACKAGE = "server.base.packages";
  private static final String FEIGN_DECODER_CLASS = "server.service.decoder";
  private static final String FEIGN_ENCODER_CLASS = "server.service.encoder";
  private static final String FEIGN_ERROR_DECODER_CLASS = "server.service.errorDecoder";
  private static final String FEIGN_CONTRACT_CLASS = "server.service.contract";
  private static final String FEIGN_INTERCEPTOR_CLASS = "server.service.requestInterceptor";


  private static final String FEIGN_SERVICE_NAME = "server.service.name";
  private static final String FEIGN_SERVICE_HOST = "server.service.host";
  private static final String FEIGN_TOKEN_METHOD = "server.service.tokenMethodName";

  private static final String SERVER_PROXY_TYPE = "server.proxy.type";
  private static final String SERVER_PROXY_HOST = "server.proxy.host";
  private static final String SERVER_PROXY_PORT = "server.proxy.port";
  private static final String SERVER_PROXY_USERNAME = "server.proxy.userName";
  private static final String SERVER_PROXY_PASSWORD = "server.proxy.password";


  private static final String SERVER_HTTP_CONN_TIMEOUT = "server.http.connect.timeout";
  private static final String SERVER_HTTP_READ_TIMEOUT = "server.http.read.timeout";
  private static final String SERVER_HTTP_WRITE_TIMEOUT = "server.http.write.timeout";

  @Override
  public void registerBeanDefinitions(
      AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

    // 从注解域basePackages获取扫描目录
    AnnotationAttributes annoAttrs =
        AnnotationAttributes.fromMap(
            importingClassMetadata.getAnnotationAttributes(EnableLinker.class.getName()));
    for (String pkg : annoAttrs.getStringArray("basePackages")) {
      if (org.springframework.util.StringUtils.hasText(pkg)) {
        basePackages.add(pkg);
      }
    }
    scanner = new ClassPathLinkerComponentScanner(registry);
    if (resourceLoader != null) {
      scanner.setResourceLoader(resourceLoader);
    }
//    scanner.setAnnotationClass(new Class[]{FeignModule.class, OrcheModule.class});
    scanner.setAnnotationClass(new Class[]{FeignModule.class});
    scanner.registerFilters();
    scanner.doScan(basePackages.toArray(new String[basePackages.size()]));
  }


  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Override
  public void setEnvironment(Environment environment) {
    LOGGER.info("parse configuration of linked.servers");
    parseConfigurationFiles();
    parseEnvironment(environment);
  }

  private void parseConfigurationFiles() {
    try {
      Enumeration<URL> urls =
          LinkerAutoConfiguredRegister.class.getClassLoader().getResources(PACKAGE_CONFIG);
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        UrlResource resource = new UrlResource(url);
        // 读取文件内容，properties类似于HashMap，包含了属性的key和value
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
        configureServer(properties);
      }
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Unable to load components from location [" + PACKAGE_CONFIG + "]", e);
    }
  }

  private void parseEnvironment(Environment environment) {
    // spring-boot 2.0.0.RELEASE
//    Iterable<ConfigurationPropertySource> sources =
//        ConfigurationPropertySources.get(environment);
//    Binder binder = new Binder(sources);
//    BindResult<Properties> bindResult = binder.bind("linked.servers.server", Properties.class);
//    Properties properties;
//    try {
//      properties = bindResult.get();
//    }catch (NoSuchElementException e){
//      return;
//    }
    RelaxedPropertyResolver relaxedPropertyResolver =
        new RelaxedPropertyResolver(environment, "linker.servers.");
    Map<String, Object> propertieMap = relaxedPropertyResolver.getSubProperties("server.");
    if (propertieMap.size() > 0) {
      Map<Integer, Properties> propertiesMap = new HashMap<>();
      Integer DEF = Integer.valueOf(-1);
      Iterator<Entry<String, Object>> it = propertieMap.entrySet().iterator();
      Integer pos = 0;
      //分组
      while (it.hasNext()) {
        Entry<String, Object> entry = it.next();
        String key = entry.getKey();
        String value = (String) entry.getValue();

        int dotPos = key.indexOf('.');
        pos = DEF;
        if (dotPos > 0) {
          String firstPart = key.substring(0, dotPos);
          if (firstPart.matches("\\d*")) {
            pos = Integer.valueOf(key.substring(0, dotPos));
            key = key.substring(dotPos + 1);
          }
        }
        Properties prop = propertiesMap.get(pos);
        if (prop == null) {
          prop = new Properties();
          propertiesMap.put(pos, prop);
        }
        prop.setProperty("server." + key, value);
      }
      Iterator<Properties> iterator = propertiesMap.values().iterator();
      while (iterator.hasNext()) {
        Properties prop = iterator.next();
        configureServer(prop);
      }
    }
  }

  private void configureServer(Properties properties) {
    String packagesStr = properties.getProperty(FEIGN_BASE_PACKAGE);
    if (packagesStr != null) {
      String[] packageArray = packagesStr.split(";");
      basePackages.addAll(Arrays.asList(packageArray));
    }
    String serviceName = properties.getProperty(FEIGN_SERVICE_NAME);
    if (serviceName == null) {
      throw new LinkerRuntimeException("'server.service.name' not configured");
    }
    ServiceConfig serviceConfig = ConfigurationUtil.getServiceConfig(serviceName);
    if (serviceConfig == null) {
      serviceConfig = new ServiceConfig();
      serviceConfig.setServiceName(serviceName);
      ConfigurationUtil.addServiceConfig(serviceName, serviceConfig);
    }
    String host = properties.getProperty(FEIGN_SERVICE_HOST);
    if (host != null) {
      if (!host.startsWith("http")) {
        throw new LinkerRuntimeException(
            "value of 'server.service.host' should start with 'http'");
      }
      serviceConfig.setUrl(host);
    }
    String propertyValue = properties.getProperty(SERVER_HTTP_CONN_TIMEOUT);
    if (StringUtils.isNotBlank(propertyValue)) {
      serviceConfig.setHttpConnectTimeOut(Integer.valueOf(propertyValue));
    }
    propertyValue = properties.getProperty(SERVER_HTTP_READ_TIMEOUT);
    if (StringUtils.isNotBlank(propertyValue)) {
      serviceConfig.setHttpReadTimeOut(Integer.valueOf(propertyValue));
    }
    propertyValue = properties.getProperty(SERVER_HTTP_WRITE_TIMEOUT);
    if (StringUtils.isNotBlank(propertyValue)) {
      serviceConfig.setHttpWriteTimeOut(Integer.valueOf(propertyValue));
    }

    propertyValue = properties.getProperty(FEIGN_CONTRACT_CLASS);
    if (StringUtils.isNotBlank(propertyValue)) {
      try {
        Class clazz = Class.forName(propertyValue);
        if (Contract.class.isAssignableFrom(clazz)) {
          serviceConfig.setContract(clazz);
        }
      } catch (ClassNotFoundException e) {
        LOGGER.warn("Class " + propertyValue + " is not found");
      }
    }
    propertyValue = properties.getProperty(FEIGN_DECODER_CLASS);
    if (StringUtils.isNotBlank(propertyValue)) {
      try {
        Class clazz = Class.forName(propertyValue);
        if (Decoder.class.isAssignableFrom(clazz)) {
          serviceConfig.setDecoder(clazz);
        }
      } catch (ClassNotFoundException e) {
        LOGGER.warn("Class " + propertyValue + " is not found");
      }
    }
    propertyValue = properties.getProperty(FEIGN_ENCODER_CLASS);
    if(StringUtils.isNotBlank(propertyValue)) {
      try {
        Class clazz = Class.forName(propertyValue);
        if (Encoder.class.isAssignableFrom(clazz)) {
          serviceConfig.setEncoder(clazz);
        }
      } catch (ClassNotFoundException e) {
        LOGGER.warn("Class " +propertyValue + " is not found");
      }
    }
    propertyValue = properties.getProperty(FEIGN_ERROR_DECODER_CLASS);
    if(StringUtils.isNotBlank(propertyValue)) {
      try {
        Class clazz = Class.forName(propertyValue);
        if (ErrorDecoder.class.isAssignableFrom(clazz)) {
          serviceConfig.setErrorDecoder(clazz);
        }
      } catch (ClassNotFoundException e) {
        LOGGER.warn("Class " +propertyValue + " is not found");
      }
    }
    propertyValue = properties.getProperty(FEIGN_INTERCEPTOR_CLASS);
    if (StringUtils.isNotBlank(propertyValue)) {
      try {
        Class clazz = Class.forName(propertyValue);
        if (ReactiveHttpRequestInterceptor.class.isAssignableFrom(clazz)) {
          serviceConfig.setRequestInterceptor(clazz);
        }
      } catch (ClassNotFoundException e) {
        LOGGER.warn("Class " + propertyValue + " is not found");
      }
    }
  }
}
