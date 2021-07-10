package io.birdge.linker.support.odata.decoder.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.bridge.linker.common.utils.BeanUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class FlattenFieldHandler implements IFieldHandler {

  private Type fieldType;
  private String[] srcNames;

  public FlattenFieldHandler(Type fieldType, String[] srcNames) {
    this.fieldType = fieldType;
    this.srcNames = srcNames;
  }

  @Override
  public Object parse(JSONObject jsonNode) {
    JSONObject node = jsonNode;
    String srcName = null;
    for (int i = 0, len = srcNames.length; i < len - 1; i++) {
      srcName = srcNames[i];
      node = parse(srcName, node);
      if (node == null) {
        return null;
      }
    }
    srcName = srcNames[srcNames.length - 1];
    if (fieldType instanceof ParameterizedType || BeanUtils.isComplexValueType((Class) fieldType)) {
      node = parse(srcName, node);
      if (node == null) {
        return null;
      }
      ODataJsonBodyParser sfJsonBodyParser = ODataJsonBodyParser.getInstance(fieldType);
      return sfJsonBodyParser.parse(node);
    } else {
      JSONArray jsonArray = node.getJSONArray("results");
      if(jsonArray !=null && jsonArray.size()>0){
        node = jsonArray.getJSONObject(0);
      }
      Object obj = node.get(srcName);
      if (obj == null) {
        return null;
      }
      String value = obj.toString().trim();
      return convert((Class) fieldType, value);
    }
  }
}