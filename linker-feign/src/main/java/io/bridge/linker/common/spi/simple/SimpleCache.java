package io.bridge.linker.common.spi.simple;

import io.bridge.linker.common.spi.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 功能说明：
 *
 */
public class SimpleCache<K, V> implements Cache<K, V> {

  private Map<K, V> cache = new HashMap<>(64);

  @Override
  public V get(K key) {
    return cache.get(key);
  }

  @Override
  public V put(K key, V value) {
    return cache.put(key, value);
  }

  @Override
  public V put(K key, V value, long expireTime, TimeUnit timeUnit) {
    return cache.put(key, value);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public V remove(K key) {
    return cache.remove(key);
  }

  @Override
  public boolean containsKey(K key) {
    return cache.containsKey(key);
  }
}
