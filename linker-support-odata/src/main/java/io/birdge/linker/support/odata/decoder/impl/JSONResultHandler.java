package io.birdge.linker.support.odata.decoder.impl;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;

public class JSONResultHandler implements IResultHandler {

  private Type resultType;

  public JSONResultHandler(Type resultType) {
    this.resultType = resultType;
  }

  @Override
  public Object parse(JSONObject jsonObject) {
    return JSONObject.parseObject(jsonObject.toString(), resultType);
  }
}
