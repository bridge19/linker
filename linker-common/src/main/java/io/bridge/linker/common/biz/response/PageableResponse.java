package io.bridge.linker.common.biz.response;

import java.util.List;
import lombok.Data;

@Data
public class PageableResponse<T> {

  private List<T> result;
  private Long total;
}
