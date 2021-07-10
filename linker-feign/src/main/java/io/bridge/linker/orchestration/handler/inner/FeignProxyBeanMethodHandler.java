package io.bridge.linker.orchestration.handler.inner;

import com.google.common.collect.Maps;
import io.bridge.linker.common.exception.LinkerRuntimeException;
import io.bridge.linker.orchestration.annotation.DataId;
import io.bridge.linker.orchestration.context.TranslationContext;
import io.bridge.linker.common.utils.BeanUtils;
import feign.Param;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class FeignProxyBeanMethodHandler implements MethodExecutionDelegate {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeignProxyBeanMethodHandler.class);

  private Type returnType;
  private InnerBeanMethodArg[] params = null;
  private Object targetBean;
  private Method targetMethod;
  private Map<String, Integer> paramIndex = Maps.newHashMap();

  public FeignProxyBeanMethodHandler(Method targetMethod, Object targetBean) {
    this.targetMethod = targetMethod;
    this.targetBean = targetBean;
  }

  @Override
  public Type getReturnType() {
    return returnType;
  }

  public void init() {
    Class<?>[] types = targetMethod.getParameterTypes();
    int len = types == null ? 0 : types.length;
    if (len > 0) {
      Annotation[][] paramAnnotations = targetMethod.getParameterAnnotations();
      this.params = new InnerBeanMethodArg[len];
      for (int i = 0; i < len; i++) {
        InnerBeanMethodArg feignProxyMethodArg = new InnerBeanMethodArg(types[i]);
        if (BeanUtils.isSimpleValueType(types[i])) {
          Annotation[] annotations = paramAnnotations[i];
          for (Annotation annotation : annotations) {
            if (annotation instanceof DataId) {
              feignProxyMethodArg.setDataId((DataId) annotation);
            }
            if (annotation instanceof Param) {
              paramIndex.put(((Param) annotation).value(), i);
            }
          }
        } else {
          feignProxyMethodArg.init();
        }
        this.params[i] = feignProxyMethodArg;
      }
    }

    Type returnType = targetMethod.getGenericReturnType();
    this.returnType = returnType;
    InnerBeanReturnValueUtil.registerType(returnType);
  }

  @Override
  public String methodName() {
    return targetMethod.getDeclaringClass().getSimpleName()+"#"+targetMethod.getName();
  }

  public Object[] getArgs(TranslationContext context) {
    int argLength = params.length;
    Object[] args = new Object[argLength];
    for (int i = 0; i < argLength; i++) {
      args[i] = this.params[i].getParamValue(context);
    }
    //设置Param值
    Iterator<Entry<String, Integer>> it = paramIndex.entrySet().iterator();
    Map<String, Object> paramValueMap = Maps.newHashMap();
    while (it.hasNext()) {
      Entry<String, Integer> entry = it.next();
      String param = entry.getKey();
      int index = entry.getValue();
      Object value = args[index];
      paramValueMap.put(param, value);
    }
    return args;
  }

  @Override
  public Mono<Object> execute(Mono<Object[]> argvMono) {
    return argvMono.flatMap(argv -> {
      try {
         Publisher<Object> result = (Publisher<Object>)targetMethod.invoke(targetBean, argv);
         if(result instanceof Flux){
           return  Mono.just(((Flux<Object>)result).collectList());
         }else{
           return (Mono<Object>)result;
         }
      } catch (Throwable e) {
        LOGGER.error("invoke method error.", e);
        throw new LinkerRuntimeException("invoke method error: " + targetMethod.getDeclaringClass().getSimpleName() + "#" + targetMethod.getName(), e);
      }
    });
  }
}
