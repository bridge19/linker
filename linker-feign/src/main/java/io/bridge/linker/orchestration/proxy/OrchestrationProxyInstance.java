package io.bridge.linker.orchestration.proxy;

import feign.InvocationHandlerFactory.MethodHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class OrchestrationProxyInstance<T> implements
    InvocationHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrchestrationProxyInstance.class);

  private final Class<T> targetType;
  private final Map<Method,MethodHandler> methodHandler;

  public OrchestrationProxyInstance(Class<T> targetType,Map<Method,MethodHandler> methodHandler){
    this.targetType = targetType;
    this.methodHandler = methodHandler;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("equals".equals(method.getName())) {
      try {
        Object
            otherHandler =
            args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
        return equals(otherHandler);
      } catch (IllegalArgumentException e) {
        return false;
      }
    } else if ("hashCode".equals(method.getName())) {
      return hashCode();
    } else if ("toString".equals(method.getName())) {
      return toString();
    }
    MethodHandler proxyMethod = methodHandler.get(method);
    return proxyMethod.invoke(args);
  }
}
