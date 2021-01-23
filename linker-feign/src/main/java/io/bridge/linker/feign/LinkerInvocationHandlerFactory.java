package io.bridge.linker.feign;

import feign.InvocationHandlerFactory;
import feign.Target;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class LinkerInvocationHandlerFactory implements InvocationHandlerFactory {
  @Override
  public InvocationHandler create(Target target, Map<Method, MethodHandler> map) {
    return null;
  }

//  private static final Logger LOGGER =
//      LoggerFactory.getLogger(LinkerInvocationHandlerFactory.class);
//  private InvocationHandlerFactory delegate;
//  private LinkerRetryer retryer;
//
//  public LinkerInvocationHandlerFactory(InvocationHandlerFactory original) {
//    this.delegate = original;
//  }
//
//  public void setRetryer(LinkerRetryer retryer) {
//    this.retryer = retryer;
//  }
//
//  @Override
//  public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
//    Class targetType = target.type();
//
//    FeignModule feignModule = (FeignModule) targetType.getAnnotation(FeignModule.class);
//    String serviceName = feignModule.service();
//    // 方法排序，同一个类可能出现依赖现象，被依赖方法Order值应该要小于依赖的方法
//    List<Method> methodList = new ArrayList<>(dispatch.keySet());
//
//    for (Method method : methodList) {
//      MethodEnhancer methodEnhancer = method.getAnnotation(MethodEnhancer.class);
//      Class<? extends IMethodEnhancer> methodEnhancerClass = null;
//      if(methodEnhancer != null){
//        methodEnhancerClass = methodEnhancer.enhancer();
//      }
//      LinkerMethod methodHandler = new SimpleMethodHandler(method,dispatch.get(method), methodEnhancerClass,retryer);
//      Map<String, Method> methodMap = LinkerComponentInfoUtils.getLinkerMethodMap(targetType);
//      if(methodMap == null){
//        methodMap = new HashMap<>();
//        LinkerComponentInfoUtils.setLinkerMethodMap(targetType,methodMap);
//      }
//      methodMap.put(method.getName(),method);
//
//
//      Cacheable cacheable = method.getAnnotation(Cacheable.class);
//      if (cacheable != null) {
//        methodHandler = new CacheableMethodHandler(method,methodHandler);
//      }
//      LinkerComponentInfoUtils.setMethodInvocationByMethod(method, methodHandler);
//
//      LinkerMethodInvoker linkerMethodInvoker = new LinkerMethodInvoker(methodHandler);
//      dispatch.put(method, linkerMethodInvoker);
//      Map<String, InvocationHandlerFactory.MethodHandler> methodHandlerMap = LinkerComponentInfoUtils.getLinkerExecuteMethodMap(targetType);
//      if(methodHandlerMap == null){
//        methodHandlerMap = new HashMap<>();
//        LinkerComponentInfoUtils.setLinkerExecuteMethodMap(targetType,methodHandlerMap);
//      }
//      methodHandlerMap.put(method.getName(),linkerMethodInvoker);
//    }
//    return delegate.create(target, dispatch);
//  }
}
