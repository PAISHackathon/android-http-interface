package com.rakuten.tech.mobile.http.util;

import android.support.annotation.NonNull;
import com.rakuten.tech.mobile.http.Http;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class ValueResponse implements Http.Response {
  private final byte[] bytes;
  private final int code;
  private final String contentType;
  private final String string;
  private final Map<String, String> headers;

  public static Builder build() {
    return new Builder();
  }

  public ValueResponse(byte[] bytes, int code, String contentType, String string,
      Map<String, String> headers) {
    this.bytes = bytes;
    this.code = code;
    this.contentType = contentType;
    this.string = string;
    this.headers = headers;
  }

  @NonNull @Override public byte[] bytes() {
    return bytes;
  }

  @Override public int code() {
    return code;
  }

  @NonNull @Override public String contentType() {
    return contentType;
  }

  @NonNull @Override public String string() {
    return string;
  }

  @NonNull @Override public Map<String, String> headers() {
    return headers;
  }

  public static class Builder {
    private byte[] bytes;
    private int code;
    private String contentType;
    private String string;
    private Map<String, String> headers;

    public Builder bytes(byte[] bytes) {
      this.bytes = bytes;
      return this;
    }

    public Builder code(int code) {
      this.code = code;
      return this;
    }

    public Builder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder string(String string) {
      this.string = string;
      return this;
    }

    public Builder headers(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public ValueResponse build() {
      return new ValueResponse(bytes, code, contentType, string, headers);
    }
  }
}
