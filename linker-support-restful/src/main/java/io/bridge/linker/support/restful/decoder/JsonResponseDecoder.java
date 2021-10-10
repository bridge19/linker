package io.bridge.linker.support.restful.decoder;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import feign.gson.DoubleToIntMapTypeAdapter;
import io.bridge.linker.common.exception.BaseException;
import io.bridge.linker.common.exception.LinkerRuntimeException;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

public class JsonResponseDecoder implements Decoder {

    private Gson gson = null;

    public JsonResponseDecoder() {
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

        if (response.body() == null) {
            return null;
        }
        Reader reader = response.body().asReader();
        try {
            return this.gson.fromJson(reader, responseTypeClass);
        } catch (JsonIOException e) {
            throw new LinkerRuntimeException(BaseException.SYSTEM_ERROR,e.getMessage());
        } finally {
            Util.ensureClosed(reader);
        }
    }
}