package io.birdge.linker.support.odata.decoder.impl;

import com.alibaba.fastjson.JSONObject;
import io.bridge.linker.common.exception.LinkerRuntimeException;

public class ErrorResultHandler implements IResultHandler {

  @Override
  public Object parse(JSONObject jsonObject) {
    String errorMsg = jsonObject.toJSONString();
    throw new LinkerRuntimeException("访问SF接口异常: " + errorMsg);
  }
}