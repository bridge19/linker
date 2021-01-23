package io.bridge.linker.common.integration;

import feign.Contract;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.reactor.client.ReactiveHttpRequestInterceptor;

public class ServiceConfig {
  private String serviceName;
  private String url;
  /** 该方法可以存放全局注解 */
  private Class<? extends Encoder> encoder;
  private Class<? extends Decoder> decoder;
  private Class<? extends ErrorDecoder> errorDecoder;
  private Class<? extends Contract> contract;
  private Class<? extends ReactiveHttpRequestInterceptor> requestInterceptor;

  //http
  private Integer httpConnectTimeOut;
  private Integer httpReadTimeOut;
  private Integer httpWriteTimeOut;

  public Class<? extends Decoder> getDecoder() {
    return decoder;
  }

  public void setDecoder(Class<? extends Decoder> decoder) {
    this.decoder = decoder;
  }
  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Class<? extends ReactiveHttpRequestInterceptor> getRequestInterceptor() {
    return requestInterceptor;
  }

  public void setRequestInterceptor(Class<? extends ReactiveHttpRequestInterceptor> requestInterceptor) {
    this.requestInterceptor = requestInterceptor;
  }

  public Class<? extends Contract> getContract() {
    return contract;
  }

  public void setContract(Class<? extends Contract> contract) {
    this.contract = contract;
  }

  public Integer getHttpConnectTimeOut() {
    return httpConnectTimeOut;
  }

  public void setHttpConnectTimeOut(Integer httpConnectTimeOut) {
    this.httpConnectTimeOut = httpConnectTimeOut;
  }

  public Integer getHttpReadTimeOut() {
    return httpReadTimeOut;
  }

  public void setHttpReadTimeOut(Integer httpReadTimeOut) {
    this.httpReadTimeOut = httpReadTimeOut;
  }

  public Integer getHttpWriteTimeOut() {
    return httpWriteTimeOut;
  }

  public void setHttpWriteTimeOut(Integer httpWriteTimeOut) {
    this.httpWriteTimeOut = httpWriteTimeOut;
  }

  public Class<? extends Encoder> getEncoder() {
    return encoder;
  }

  public void setEncoder(Class<? extends Encoder> encoder) {
    this.encoder = encoder;
  }

  public Class<? extends ErrorDecoder> getErrorDecoder() {
    return errorDecoder;
  }

  public void setErrorDecoder(Class<? extends ErrorDecoder> errorDecoder) {
    this.errorDecoder = errorDecoder;
  }
}
