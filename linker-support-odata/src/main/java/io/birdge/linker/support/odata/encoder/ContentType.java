package io.birdge.linker.support.odata.encoder;

public enum ContentType {
  UNDEFINED("undefined"),
  URLENCODED("application/x-www-form-urlencoded"),
  MULTIPART("multipart/form-data"),
  JSON("application/json");

  private final String header;

  private ContentType(String header) {
    this.header = header;
  }

  public static ContentType of(String str) {
    if (str == null) {
      return UNDEFINED;
    } else {
      String trimmed = str.trim();
      ContentType[] contentTypes = values();
      int length = contentTypes.length;

      for(int var4 = 0; var4 < length; ++var4) {
        ContentType type = contentTypes[var4];
        if (trimmed.startsWith(type.getHeader())) {
          return type;
        }
      }

      return UNDEFINED;
    }
  }

  public String getHeader() {
    return this.header;
  }
}