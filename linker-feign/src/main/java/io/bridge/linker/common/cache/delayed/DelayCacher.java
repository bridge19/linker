package io.bridge.linker.common.cache.delayed;

import com.google.gson.Gson;
import io.bridge.linker.common.cache.ExpireHandler;
import io.bridge.linker.common.spi.Cache;
import io.bridge.linker.common.spi.ServiceLookup;
import io.bridge.linker.common.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.*;

public class DelayCacher implements IDelayCacher {
  private static final Logger LOGGER = LoggerFactory.getLogger(DelayCacher.class);

  private Cache cache;

  private Executor executor = Executors.newFixedThreadPool(2);

  private HashMap<String, ExpireHandler> expireHandlerHashMap = new HashMap<>();

  private ConcurrentHashMap<String, DelayedKey<String>> delayedKeyMap = new ConcurrentHashMap<>();

  private DelayCacheThread delayCacheThread = new DelayCacheThread();

  private ExpireHandlerThread expireHandlerThread = new ExpireHandlerThread();

  public DelayCacher(){
    init();
  }
  public void init(){
    this.cache = ServiceLookup.lookup(Cache.class);
    executor.execute(delayCacheThread);
    executor.execute(expireHandlerThread);
  }

  @Override
  public void addItem(
      String key, Object value, long expireIn, TimeUnit timeUnit, ExpireHandler expireHandler) {
    cache.put(key, value, expireIn, timeUnit);
    DelayedKey newDelayedKey = new DelayedKey<>(key, timeUnit.toMillis(expireIn));
    DelayedKey oldDelayedKey = delayedKeyMap.putIfAbsent(key, newDelayedKey);
    if (oldDelayedKey != null) {
      delayCacheThread.remove(oldDelayedKey);
      delayedKeyMap.put(key, newDelayedKey);
    }
    delayCacheThread.add(newDelayedKey);
    if (expireHandler != null) {
      expireHandlerHashMap.put(key, expireHandler);
    }
  }

  @Override
  public void addItem(String key, Object value, long expireIn, TimeUnit timeUnit) {
    addItem(key, value, expireIn, timeUnit, null);
  }

  private class DelayCacheThread implements Runnable {

    private DelayQueue<DelayedKey<String>> delayQueue = new DelayQueue<>();

    private volatile boolean stop = false;

    public void remove(DelayedKey<String> key) {
      delayQueue.remove(key);
    }

    public void add(DelayedKey<String> key) {
      delayQueue.add(key);
    }

    @Override
    public void run() {
      while (!stop) {
        DelayedKey<String> delayedKey = null;
        try {
          delayedKey = delayQueue.take();
          if (delayedKey != null) {
            // 缓存过期
            String key = delayedKey.getItem();
            Object value = cache.get(key);
            delayQueue.remove(key);
            if (value != null) {
              cache.remove(key);
              expireHandlerThread.put(new KeyValuePair(key, value));
            }
            if (LOGGER.isInfoEnabled()) {
              LOGGER.info("delete expired key:" + delayedKey.getItem());
            }
          }
        } catch (InterruptedException e) {
          LOGGER.warn("delayed queue thread interrupted", e);
        } catch (Exception e) {
          LOGGER.warn("delayed queue thread captured exception", e);
        }
      }
    }
  }

  private class ExpireHandlerThread implements Runnable {
    private BlockingQueue<KeyValuePair<Object>> blockingQueue =
        new LinkedBlockingDeque<>(100);
    private volatile boolean stop = false;

    public void put(KeyValuePair keyValuePair) throws InterruptedException {
      blockingQueue.put(keyValuePair);
    }

    @Override
    public void run() {
      while (!stop) {
        KeyValuePair<Object> keyValuePair = null;
        try {
          keyValuePair = blockingQueue.poll(1, TimeUnit.MINUTES);
          if (LOGGER.isInfoEnabled()) {
            LOGGER.info("invoke expire thread wakes up by timeout");
          }
          if (keyValuePair != null) {
            String key = keyValuePair.getKey();
            Object value = keyValuePair.getValue();
            if (value != null) {
              LOGGER.info("invoke expire handler for key:" + key);
              expireHandlerHashMap.get(key).onExpire(key, value);
            }
          }
        } catch (InterruptedException e) {
          LOGGER.warn("expire handler exception: \n" + keyValuePair.toString(), e);
        } catch (Exception e) {
          LOGGER.warn("expire handler exception: \n" + keyValuePair.toString(), e);
        }
      }
    }
  }

  private class KeyValuePair<V> {
    private String key;
    private V value;

    public KeyValuePair(String key, V value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("key: ").append(this.key).append(", value:");
      if(BeanUtils.isSimpleValueType(value.getClass())){
        sb.append(String.valueOf(value));
      }else {
        Gson gson = new Gson();
        sb.append(gson.toJson(value));
      }
      return sb.toString();
    }
  }
}
