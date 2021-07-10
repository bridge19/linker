package io.bridge.linker.common.exception;

public class LinkerRuntimeException extends RuntimeException {
  private int code;
  private String message;

  public LinkerRuntimeException(String message){
    this.message = message;
  }
  public LinkerRuntimeException(Integer code,String message){
    this.code = code;
    this.message = message;
  }
  public LinkerRuntimeException(String message, Throwable cause) {
    super(message, cause);
    this.message = message;
  }

  public LinkerRuntimeException(BaseException exception){
    this.code = exception.getCode();
    this.message = exception.getMessage();
  }

  public int getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
