package io.bridge.linker.common.cache.delayed;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 用于存放在延迟队列的类
 * @param <T>
 */
public class DelayedKey<T> implements Delayed {

  private T item;
  private Long expireTime;

  public DelayedKey(T item, long expireTime) {
    this.item = item;
    this.expireTime = expireTime + System.currentTimeMillis();
  }

  public T getItem() {
    return item;
  }

  @Override
  public long getDelay(TimeUnit unit) {
    return expireTime - System.currentTimeMillis();
  }

  @Override
  public int compareTo(Delayed o) {
    return this.expireTime.compareTo(((DelayedKey) o).expireTime);
  }
}
