package io.bridge.linker.feign;

import com.google.gson.Gson;
import io.bridge.httpclient.client.ReactiveHttpAsyncClient;
import io.bridge.linker.common.integration.ServiceConfig;
import io.bridge.linker.feign.decoder.LinkerErrorDecoder;
import feign.Contract;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.reactor.ReactiveFeign;
import feign.reactor.client.ReactiveHttpRequestInterceptor;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class FeignProxy {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FeignProxy.class);

  private static ConcurrentHashMap<String, ReactiveFeign.Builder> builderMap = new ConcurrentHashMap<>();

  public static <T> T newInstance(ServiceConfig config, Class<T> type) {
    String serviceName = config.getServiceName();
    String url = config.getUrl();
    ReactiveFeign.Builder builder = getBuilder(serviceName, config);
    T instance = (T) builder.target(type, url);
    return instance;
  }

  private static ReactiveFeign.Builder getBuilder(String serviceName, ServiceConfig config) {
    ReactiveFeign.Builder builder = builderMap.get(serviceName);
    if (builder == null) {
      synchronized (builderMap) {
        builder = builderMap.get(serviceName);
        if (builder == null) {
          Encoder encoder = null;
          if (config.getEncoder() != null) {
            try {
              encoder = config.getEncoder().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
              LOGGER.warn("create encoder instance error", e);
              encoder = new GsonEncoder();
            }
          } else {
            encoder = new GsonEncoder();
          }
          Decoder decoder = null;
          if (config.getDecoder() != null) {
            try {
              decoder = config.getDecoder().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
              LOGGER.warn("create decoder instance error", e);
            }
          } else {
            Gson gson = new Gson();
            decoder = new GsonDecoder(gson);
          }
          Contract contract = null;
          if (config.getContract() != null) {
            try {
              contract = config.getContract().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
              LOGGER.warn("create contract instance error, will use Contract.BaseContract.Default", e);
              contract = new Contract.BaseContract.Default();
            }
          } else {
            contract = new Contract.BaseContract.Default();
          }
          ReactiveHttpRequestInterceptor requestInterceptor = null;
          if(config.getRequestInterceptor() != null){
            try {
              requestInterceptor = config.getRequestInterceptor().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
              LOGGER.warn("create requestInterceptor instance error", e);
            }
          }
          ErrorDecoder errorDecoder = null;
          if (config.getErrorDecoder() != null) {
            try {
              errorDecoder = config.getErrorDecoder().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
              LOGGER.warn("create contract instance error, will use Contract.BaseContract.Default", e);
            }
          } else {
            errorDecoder = new LinkerErrorDecoder();
          }

          builder = ReactiveFeign.builder()
                  .contract(contract).decoder(decoder).encoder(encoder)
                  .clientFactory(methodMetadata -> new ReactiveHttpAsyncClient(methodMetadata));
          if(requestInterceptor != null){
            builder.requestInterceptor(requestInterceptor);
          }
          builderMap.putIfAbsent(serviceName, builder);
        }
      }
    }
    return builder;
  }
}
