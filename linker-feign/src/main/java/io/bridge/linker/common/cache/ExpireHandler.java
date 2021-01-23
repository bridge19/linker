package io.bridge.linker.common.cache;

public interface ExpireHandler {
  class DEFAULT implements ExpireHandler {
    @Override
    public void onExpire(String key, Object object) {
    }
  }
  void onExpire(String key, Object object);
}
