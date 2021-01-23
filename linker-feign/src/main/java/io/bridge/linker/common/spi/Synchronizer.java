package io.bridge.linker.common.spi;

public interface Synchronizer {
  void lock(String lockKey);

  boolean releaseLock(String lockKey);
}
