package io.bridge.linker.feign.decoder;

import io.bridge.linker.common.exception.LinkerRuntimeException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 功能说明：
 *
 */
public class LinkerErrorDecoder implements ErrorDecoder {
  private Logger LOGGER = LoggerFactory.getLogger(getClass());

  @Override
  public Exception decode(String methodKey, Response response) {
    int status = response.status();
    return new LinkerRuntimeException("feign request error, code: "+status);
  }
}
