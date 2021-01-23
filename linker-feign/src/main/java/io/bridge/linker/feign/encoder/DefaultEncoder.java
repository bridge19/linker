package io.bridge.linker.feign.encoder;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.gson.GsonEncoder;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class DefaultEncoder implements Encoder {
  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final Pattern CHARSET_PATTERN = Pattern.compile("(?<=charset=)([\\w\\-]+)");
  private Map<ContentType, Encoder> delegateMap = new HashMap<>();

  public DefaultEncoder() {
    delegateMap.put(ContentType.JSON, new GsonEncoder());
    FormEncoder encoder = new FormEncoder();
    delegateMap.put(ContentType.URLENCODED, encoder);
    delegateMap.put(ContentType.MULTIPART, encoder);
    delegateMap.put(ContentType.UNDEFINED, encoder);
  }

  @Override
  public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
    String contentTypeValue = this.getContentTypeValue(template.headers());
    ContentType contentType = ContentType.of(contentTypeValue);
    delegateMap.get(contentType).encode(object, bodyType, template);
  }

  private String getContentTypeValue(Map<String, Collection<String>> headers) {
    Iterator<Map.Entry<String, Collection<String>>> iterator = headers.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Collection<String>> entry = iterator.next();
      if (!entry.getKey().equalsIgnoreCase(CONTENT_TYPE_HEADER)) {
        break;
      }
      Iterator<String> valueIterator = entry.getValue().iterator();

      while (valueIterator.hasNext()) {
        String contentTypeValue = valueIterator.next();
        if (contentTypeValue != null) {
          return contentTypeValue;
        }
      }
    }
    return null;
  }
}
