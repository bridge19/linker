package io.bridge.linker.common.spi;

import io.bridge.linker.common.spi.simple.SimpleCache;
import io.bridge.linker.common.spi.simple.SimpleMethodSynchronizer;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ServiceLookup {

   private static ConcurrentHashMap<Class,Object> instanceMap = new ConcurrentHashMap<>();

   public static  <T> T lookup(Class<T> tClass){
     if(tClass != Cache.class && tClass != Synchronizer.class){
       return null;
     }
     T result = (T) instanceMap.get(tClass);
     if(result == null) {
       ServiceLoader<T> serviceLoader = ServiceLoader.load(tClass);
       Iterator<T> iterator = serviceLoader.iterator();
       if (iterator.hasNext()) {
         result = iterator.next();
       } else {
         result = tClass == Cache.class ? (T) new SimpleCache() : (T) new SimpleMethodSynchronizer();
       }
       result = (T)instanceMap.putIfAbsent(tClass,result);
     }
     return (T) instanceMap.get(tClass);
   }
}
