package io.bridge.linker.common.exception;

public class LinkerRuntimeException extends RuntimeException {
  public LinkerRuntimeException(String message){
    super(message);
  }
  public LinkerRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
