package io.bridge.linker.common.exception;

public class LinkerRetryException extends RuntimeException {

  private boolean refreshToken;

  public LinkerRetryException(boolean refreshToken){
    this.refreshToken = refreshToken;
  }

  public boolean isRefreshToken(){
    return this.refreshToken;
  }
}
