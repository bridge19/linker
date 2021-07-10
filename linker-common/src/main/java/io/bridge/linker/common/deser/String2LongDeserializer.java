package io.bridge.linker.common.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class String2LongDeserializer extends JsonDeserializer<Long> {



  @Override
  public Long deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    String source = p.getValueAsString();
    if ("".equals(source)) {
      return null;
    }
    try {
      return Long.valueOf(source);
    }catch (NumberFormatException e){
      log.error("parse String to Long error for %s",source);
      return null;
    }
  }
}
