package io.bridge.linker.orchestration.handler.inner;

import io.bridge.linker.orchestration.context.TranslationContext;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;

public interface MethodExecutionDelegate {
  Type getReturnType();
  void init();
  String methodName();
  public Object[] getArgs(TranslationContext context);
  Mono<Object> execute(Mono<Object[]> argv);
}
