package io.bridge.linker.support.restful.decoder;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.bridge.linker.common.exception.BaseException;
import io.bridge.linker.common.exception.LinkerRuntimeException;
import io.bridge.linker.support.restful.model.ResponseWrapper;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import feign.gson.DoubleToIntMapTypeAdapter;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

public class RestfulResponseDecoder implements Decoder {

  private Gson gson = null;

  public RestfulResponseDecoder() {
    GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
    gsonBuilder.registerTypeAdapter(
        new TypeToken<Map<String, Object>>() {}.getType(), new DoubleToIntMapTypeAdapter());
    // timestamp 处理
    gsonBuilder.registerTypeAdapter(
        Date.class,
        new JsonDeserializer<Date>() {
          public Date deserialize(
              JsonElement json, Type typeOfT, JsonDeserializationContext context)
              throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
          }
        });
    this.gson = gsonBuilder.create();
  }

  @Override
  public Object decode(Response response, Type type) throws IOException, FeignException {
    Type responseTypeClass = type;
    if(!responseTypeClass.getTypeName().equals(ResponseWrapper.class.getName())) {
      responseTypeClass = new ParameterizedType() {
        @Override
        public Type[] getActualTypeArguments() {
          return new Type[]{type};
        }
        @Override
        public Type getRawType() {
          return ResponseWrapper.class;
        }

        @Override
        public Type getOwnerType() {
          return null;
        }
      };
    }
    if (response.body() == null) {
      return null;
    }
    Reader reader = response.body().asReader();

    ResponseWrapper responseObject;
    try {
      responseObject = this.gson.fromJson(reader, responseTypeClass);
      if (responseObject.getCode().intValue() != 0) {
        throw new LinkerRuntimeException(BaseException.SYSTEM_ERROR,responseObject.getMessage());
      }
      Object result = responseObject.getData();
      return result;
    } catch (JsonIOException e) {
      throw new LinkerRuntimeException(BaseException.SYSTEM_ERROR,e.getMessage());
    } finally {
      Util.ensureClosed(reader);
    }
  }
}
