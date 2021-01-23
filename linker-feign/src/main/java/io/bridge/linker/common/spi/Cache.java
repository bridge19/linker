package io.bridge.linker.common.spi;

import java.util.concurrent.TimeUnit;

/**
 * 功能说明：
 *
 */
public interface Cache<K, V> {
  V get(K key);

  V put(K key, V value);

  V put(K key, V value, long expireTime, TimeUnit timeUnit);

  void clear();

  V remove(K key);

  boolean containsKey(K key);
}
