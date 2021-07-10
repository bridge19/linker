package io.bridge.linker.common.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ArrayUtils;

public class IntMemberOfValidate implements ConstraintValidator<IntegerMemberOf, Object> {
  int[] values = null;

  @Override
  public void initialize(IntegerMemberOf constraintAnnotation) {
      values = constraintAnnotation.values();
  }
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if(ArrayUtils.isEmpty(values) || value == null ){
      return true;
    }
    for(int item: values){
      if(((Integer) value).intValue() == item) {
        return true;
      }
    }
    return false;
  }
}
