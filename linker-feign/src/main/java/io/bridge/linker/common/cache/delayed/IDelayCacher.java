package io.bridge.linker.common.cache.delayed;

import io.bridge.linker.common.cache.ExpireHandler;

import java.util.concurrent.TimeUnit;

public interface IDelayCacher {
  void addItem(
      String key, Object value, long expireIn, TimeUnit timeUnit, ExpireHandler expireHandler);
  void addItem(
      String key, Object value, long expireIn, TimeUnit timeUnit);
}
