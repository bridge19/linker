package io.bridge.linker.orchestration.handler;

import io.bridge.linker.LinkerBeanFactory;
import io.bridge.linker.orchestration.LinkerComponentInfoUtils;
import io.bridge.linker.orchestration.annotation.DataId;
import io.bridge.linker.orchestration.annotation.InvokeParallel;
import io.bridge.linker.orchestration.annotation.Invoker;
import io.bridge.linker.orchestration.annotation.InvokerGroup;
import io.bridge.linker.orchestration.context.TranslationContext;
import io.bridge.linker.orchestration.handler.inner.FeignProxyBeanMethodHandler;
import io.bridge.linker.orchestration.handler.inner.InnerBeanReturnValueUtil;
import io.bridge.linker.orchestration.handler.inner.MethodExecutionDelegate;
import io.bridge.linker.util.ArrayUtils;
import io.bridge.linker.util.BeanUtils;
import feign.InvocationHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class OrchestrationMethodHandler implements InvocationHandlerFactory.MethodHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrchestrationMethodHandler.class);
  private final Method method;

  private Class<?> returnType;
  private InvokerMethodArg[] params = null;

  private MethodExecutionDelegate[][] feignProxyMethods;

  public OrchestrationMethodHandler(Method method) {
    this.method = method;
  }

  public void init() {
    InvokeParallel invokeParallel = method.getAnnotation(InvokeParallel.class);
    if (invokeParallel == null) {
      return;
    }

    InvokerGroup[] invokerGroups = invokeParallel.invokerGroups();
    if (ArrayUtils.isEmpty(invokerGroups)) {
      return;
    }

    feignProxyMethods = new MethodExecutionDelegate[invokerGroups.length][];
    int i = 0;
    for (InvokerGroup invokerGroup : invokerGroups) {
      Invoker[] invokers = invokerGroup.invokers();
      MethodExecutionDelegate[] invokerMethod = new MethodExecutionDelegate[invokers.length];
      feignProxyMethods[i++] = invokerMethod;
      int j = 0;
      for (Invoker invoker : invokers) {
        MethodExecutionDelegate feignMethodHandler = null;
        Class feignClass = invoker.targetClass();
        String methodName = invoker.methodName();
        Method feignMethod = LinkerComponentInfoUtils.getMethodByName(feignClass, methodName);
        Object feignInstance = LinkerBeanFactory.getLinkerModule(feignClass);
        feignMethodHandler = new FeignProxyBeanMethodHandler(feignMethod, feignInstance);
        feignMethodHandler.init();
        invokerMethod[j++] = feignMethodHandler;
      }
    }

    Class<?>[] types = method.getParameterTypes();
    int len = types == null ? 0 : types.length;
    if (len > 0) {
      Annotation[][] paramAnnotations = method.getParameterAnnotations();
      this.params = new InvokerMethodArg[len];
      for (i = 0; i < len; i++) {
        InvokerMethodArg invokerMethodArg = new InvokerMethodArg(types[i]);
        if (BeanUtils.isSimpleValueType(types[i])) {
          Annotation[] annotations = paramAnnotations[i];
          for (Annotation annotation : annotations) {
            if (annotation instanceof DataId) {
              invokerMethodArg.setDataId((DataId) annotation);
              break;
            }
          }
        } else {
          invokerMethodArg.init();
        }
        this.params[i] = invokerMethodArg;
      }
    }

    this.returnType = method.getReturnType();
    InvokerMethodReturnValueUtil.registerType(returnType);
  }

  public void initContext(Object[] args, TranslationContext context) {
    if (ArrayUtils.isNotEmpty(args)) {
      for (int i = 0, len = args.length; i < len; i++) {
        this.params[i].setParam(context, args[i]);
      }
    }
  }

  @Override
  public Object invoke(Object[] args) throws Throwable {
    TranslationContext context = new TranslationContext();
    initContext(args, context);


    for (int i = 0; i < feignProxyMethods.length; i++) {
      MethodExecutionDelegate[] delegates = feignProxyMethods[i];
      Flux.fromArray(delegates).flatMap(delegate -> {
        Mono<Object> publisher = delegate.execute(Mono.just(delegate.getArgs(context)));
        if(publisher == Mono.empty()) {
          return publisher;
        }
        Type returnType = delegate.getReturnType();
        return publisher
            .doOnError(e -> LOGGER.error(String.format("invoker method %s error.", delegate.methodName()), e))
            .flatMap(object -> {
              InnerBeanReturnValueUtil.releaseValue(context, returnType, object);
              return Mono.empty();
            });
      }).doOnError(e -> LOGGER.error("error.", e))
          .blockLast();
    }
    return InvokerMethodReturnValueUtil.generateValue(context, returnType);
  }
}
