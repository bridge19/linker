package io.birdge.linker.support.odata.decoder.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WrappedField {

  private Field field;
  private IFieldHandler fieldHandler;
  private Method valueSetter;

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
  }

  public IFieldHandler getFieldHandler() {
    return fieldHandler;
  }

  public void setFieldHandler(IFieldHandler fieldHandler) {
    this.fieldHandler = fieldHandler;
  }

  public Method getValueSetter() {
    return valueSetter;
  }

  public void setValueSetter(Method valueSetter) {
    this.valueSetter = valueSetter;
  }
}