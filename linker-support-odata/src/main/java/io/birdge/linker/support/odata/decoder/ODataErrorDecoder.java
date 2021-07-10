package io.birdge.linker.support.odata.decoder;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import io.birdge.linker.support.odata.contant.ODataConstant;
import io.birdge.linker.support.odata.decoder.auth.IAuthenticationInfoHolder;
import io.birdge.linker.support.odata.model.AccessToken;
import io.birdge.linker.support.odata.model.AuthenticationInfo;
import io.bridge.linker.common.exception.LinkerRetryException;
import io.bridge.linker.common.exception.LinkerRuntimeException;
import io.bridge.linker.common.spi.Cache;
import io.bridge.linker.feign.context.FeignRequestContextHolder;
import io.bridge.linker.spring.SpringContextHolder;

public class ODataErrorDecoder extends ErrorDecoder.Default {
  @Override
  public Exception decode(String methodKey, Response response) {
    Exception exception = super.decode(methodKey,response);
    if(exception instanceof RetryableException){
      return exception;
    }
    if(response.status() == 403 || response.status() == 401){
      IAuthenticationInfoHolder authenticationInfoHolder = SpringContextHolder.getBean(IAuthenticationInfoHolder.class);

      if(authenticationInfoHolder == null){
        throw new LinkerRuntimeException("a implement instance of "
            + "io.birdge.linker.sf.model.AuthenticationInfo.IAuthenticationInfoHolder "
            + "should inject in spring container.");
      }

      AuthenticationInfo authenticationInfo = authenticationInfoHolder.getAuthenticationInfo();

      String clientId = authenticationInfo.getClientId();
      String userId = authenticationInfo.getAdminId();

      String cacheKey = String.format("LinkerSFToken_%s_%s",clientId,userId);
      Cache cache = SpringContextHolder.getBean(Cache.class);
      AccessToken accessToken = (AccessToken) cache.get(cacheKey);
      String oldToken = (String) FeignRequestContextHolder.getHeaderValue(ODataConstant.TOKEN);
      if(accessToken== null || oldToken== null || oldToken.equals(accessToken.getAccessToken())) {
        cache.remove(cacheKey);
        FeignRequestContextHolder.removeHeaderValues();
        throw new LinkerRetryException(true);
      }else{
        FeignRequestContextHolder.putHeaderValue(ODataConstant.TOKEN,accessToken.getAccessToken());
        throw new LinkerRetryException(false);
      }
    }
    return exception;
  }
}
