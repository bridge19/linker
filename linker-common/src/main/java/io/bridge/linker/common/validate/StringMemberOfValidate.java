package io.bridge.linker.common.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ArrayUtils;

public class StringMemberOfValidate implements ConstraintValidator<StringMemberOf, Object> {
  String[] values = null;

  @Override
  public void initialize(StringMemberOf constraintAnnotation) {
      values =  constraintAnnotation.values();
  }
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if(ArrayUtils.isEmpty(values) || value == null ){
      return true;
    }
    for(Object item: values){
      if(value.equals(item)) {
        return true;
      }
    }
    return false;
  }
}
