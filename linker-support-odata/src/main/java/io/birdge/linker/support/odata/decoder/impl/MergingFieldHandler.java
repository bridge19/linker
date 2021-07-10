package io.birdge.linker.support.odata.decoder.impl;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class MergingFieldHandler implements IFieldHandler {

  private List<IFieldHandler> fieldHandlers;

  public MergingFieldHandler(List<IFieldHandler> fieldHandlers) {
    this.fieldHandlers = fieldHandlers;
  }

  @Override
  public Object parse(JSONObject jsonNode) {
    StringBuilder sb = new StringBuilder(56);
    for (IFieldHandler fieldHandler : fieldHandlers) {
      Object obj = fieldHandler.parse(jsonNode);
      sb.append(obj == null ? "" : obj);
    }
    return sb.toString();
  }
}