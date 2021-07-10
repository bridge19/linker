package io.bridge.linker.common.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MobileValidate implements ConstraintValidator<Mobile, String> {

    String pattern = null;

    @Override
    public void initialize(Mobile constraintAnnotation) {
        pattern = constraintAnnotation.regexp();
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || "".equals(value)) {
            return true;
        }
        if(pattern == null){
            pattern = "1\\d{10}";
        }
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(value);
        return m.matches();
    }
}
