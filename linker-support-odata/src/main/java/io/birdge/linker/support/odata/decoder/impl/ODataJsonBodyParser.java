package io.birdge.linker.support.odata.decoder.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ODataJsonBodyParser implements IResultHandler {

  private List<WrappedField> wrappedFields;
  private Type resultType;
  private static Map<String, ODataJsonBodyParser> sfBodyParserMap = new HashMap<>();

  public static ODataJsonBodyParser getInstance(Type type) {
    synchronized (type) {
      ODataJsonBodyParser parser = sfBodyParserMap.get(type.getTypeName());
      if (parser == null) {
        parser = new ODataJsonBodyParser(type);
      }
      return parser;
    }
  }

  private ODataJsonBodyParser(Type resultType) {
    this.resultType = resultType;
    this.wrappedFields = analyze(resultType);
  }

  @Override
  public Object parse(JSONObject jsonObj) {
    if (jsonObj == null) {
      return null;
    }
    JSONObject jsonObject = jsonObj;
    if (resultType instanceof ParameterizedType) {
      Type actualType = ((ParameterizedType) resultType).getActualTypeArguments()[0];
      JSONArray jsonElements = jsonObject.getJSONArray("results");
      if (jsonElements == null || jsonElements.size() == 0) {
        ODataJsonBodyParser listParser = getInstance(actualType);
        Object obj = listParser.parse(jsonObj);
        if (obj != null) {
          if (obj instanceof List) {
            return obj;
          } else {
            List result = new ArrayList();
            result.add(obj);
            return result;
          }
        }else {
          return null;
        }
      } else {
        List result = new ArrayList();
        Iterator it = jsonElements.iterator();
        while (it.hasNext()) {
          ODataJsonBodyParser listParser = getInstance(actualType);
          JSONObject item = (JSONObject) it.next();
          Object obj = listParser.parse(item);
          if(obj != null) {
            result.add(obj);
          }
        }
        return result;
      }
    } else {
      try {
        Object result = ((Class) resultType).newInstance();
        result = convert(result, wrappedFields, jsonObject);
        return result;
      } catch (InstantiationException | IllegalAccessException e) {
        LOGGER.warn("instance can't be created." + resultType.getTypeName());
      }
    }
    return null;
  }
}
