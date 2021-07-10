package io.birdge.linker.support.odata.decoder.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.bridge.linker.util.BeanUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class OriginalFieldHandler implements IFieldHandler {

  private Type fieldType;
  private String srcName;

  public OriginalFieldHandler(Type fieldType, String srcName) {
    this.fieldType = fieldType;
    this.srcName = srcName;
  }

  @Override
  public Object parse(JSONObject jsonNode) {
    if (fieldType instanceof ParameterizedType || BeanUtils.isComplexValueType((Class) fieldType)) {
      ODataJsonBodyParser parser = ODataJsonBodyParser.getInstance(fieldType);
      jsonNode = parse(srcName,jsonNode);
      return parser.parse(jsonNode);
    } else {
      JSONArray jsonArray = jsonNode.getJSONArray("results");
      if(jsonArray !=null && jsonArray.size()>0){
        jsonNode = jsonArray.getJSONObject(0);
      }
      Object obj = jsonNode.get(srcName);
      if (obj == null) {
        return null;
      }
      String value = obj.toString().trim();
      return convert((Class) fieldType, value);
    }
  }
}