package io.birdge.linker.support.odata.encoder;

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

public class ODataEncoder implements Encoder {
  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final Pattern CHARSET_PATTERN = Pattern.compile("(?<=charset=)([\\w\\-]+)");
  private Map<ContentType,Encoder> delegateMap=new HashMap<>();

  public ODataEncoder(){
    delegateMap.put(ContentType.JSON,new GsonEncoder());
    FormEncoder encoder = new FormEncoder();
    delegateMap.put(ContentType.URLENCODED,encoder);
  }

  @Override
  public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
    String contentTypeValue = this.getContentTypeValue(template.headers());
    ContentType contentType = ContentType.of(contentTypeValue);
    delegateMap.get(contentType).encode(object,bodyType,template);
  }

  private String getContentTypeValue(Map<String, Collection<String>> headers) {
    Iterator var2 = headers.entrySet().iterator();

    while(true) {
      Map.Entry entry;
      do {
        if (!var2.hasNext()) {
          return null;
        }

        entry = (Map.Entry)var2.next();
      } while(!((String)entry.getKey()).equalsIgnoreCase(CONTENT_TYPE_HEADER));

      Iterator var4 = ((Collection)entry.getValue()).iterator();

      while(var4.hasNext()) {
        String contentTypeValue = (String)var4.next();
        if (contentTypeValue != null) {
          return contentTypeValue;
        }
      }
    }
  }
}