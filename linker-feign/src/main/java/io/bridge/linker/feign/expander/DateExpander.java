package io.bridge.linker.feign.expander;

import feign.Param.Expander;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DateExpander implements Expander {

  @Override
  public String expand(Object value) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
    return dateFormat.format(value);
  }
}
