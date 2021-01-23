package io.bridge.linker.orchestration.proxy;

import io.bridge.linker.common.exception.LinkerRuntimeException;
import io.bridge.linker.orchestration.handler.OrchestrationMethodHandler;
import feign.InvocationHandlerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class OrchestrationProxy {

  public static <T> T newInstance(Class<T> type) {
    Method[] methods = type.getDeclaredMethods();
    if (methods.length == 0) {
      throw new LinkerRuntimeException("no method is defined for class: " + type.getName());
    }
    Map<Method, InvocationHandlerFactory.MethodHandler> methodHandlerMap = new HashMap<>();
    for (Method method : methods) {
      OrchestrationMethodHandler proxyMethod = new OrchestrationMethodHandler(method);
      proxyMethod.init();
      methodHandlerMap.put(method, proxyMethod);
    }
    InvocationHandler handler = new OrchestrationProxyInstance(type,methodHandlerMap);
    return newProxy(type, handler);
  }

  private static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
    Object object =
        Proxy.newProxyInstance(
            interfaceType.getClassLoader(), new Class[]{interfaceType}, handler);
    return interfaceType.cast(object);
  }

}
