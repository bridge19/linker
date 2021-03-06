package io.birdge.linker.support.odata.model;


import io.bridge.linker.feign.decoder.annotation.Original;

public class AccessToken {

  @Original("access_token")
  private String accessToken;
  @Original("token_type")
  private String tokenType;
  @Original("expires_in")
  private Long expiresIn;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public Long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(Long expiresIn) {
    this.expiresIn = expiresIn;
  }
}
