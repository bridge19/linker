package io.bridge.linker.feign.retry;

import io.bridge.linker.common.exception.LinkerRetryException;

public interface LinkerRetryer extends Cloneable {
  void continueOrPropagate(LinkerRetryException e);
  LinkerRetryer clone();
  public static class Default implements LinkerRetryer{

    private final Class<?> tokenClass;
    private final String tokenMethod;
    private final int maxAttempts;
    private int attempt = 1;

    public Default(Class<?> tokenClass, String tokenMethod, int maxAttempts){
      this.tokenClass = tokenClass;
      this.tokenMethod =tokenMethod;
      this.maxAttempts = maxAttempts;
    }

    @Override
    public void continueOrPropagate(LinkerRetryException e) {
      if(attempt++ >=maxAttempts || !e.isRefreshToken()){
        throw e;
      }
//      FeignRequestContextHolder.removeHeaderValues();
//      Map<String, Method> tokenClassMethods = LinkerComponentInfoUtils.getLinkerMethodMap(tokenClass);
//      if(tokenClassMethods == null || tokenClassMethods.size()==0){
//        throw new LinkerRuntimeException("token class is not found.");
//      }
//      Method tokenMethod = tokenClassMethods.get(this.tokenMethod);
//      if(tokenMethod == null){
//        throw new LinkerRuntimeException("token method is not found.");
//      }
//
//      Object tokenClassInstance = LinkerBeanFactory.getLinkerModule(this.tokenClass);
//      try {
//        int paramCount = tokenMethod.getParameterCount();
//        tokenMethod.invoke(tokenClassInstance,new Object[paramCount]);
//      } catch (IllegalAccessException | InvocationTargetException exception) {
//        throw new LinkerRuntimeException("get token error.",exception);
//      }
    }

    @Override
    public LinkerRetryer clone() {
      return new Default(tokenClass,tokenMethod,maxAttempts);
    }

  }
}
