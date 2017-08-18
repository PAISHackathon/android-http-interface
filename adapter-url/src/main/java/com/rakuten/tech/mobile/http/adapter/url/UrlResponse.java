package com.rakuten.tech.mobile.http.adapter.url;

import android.support.annotation.NonNull;

import com.rakuten.tech.mobile.http.Http;

import java.nio.charset.Charset;
import java.util.Map;

class UrlResponse implements Http.Response {

    private final byte[] bytes;
    private final int code;
    private final String contentType;
    private final Map<String, String> headers;

    @SuppressWarnings("WeakerAccess")
    UrlResponse(@NonNull byte[] bytes, @NonNull int code, @NonNull String contentType, @NonNull Map<String, String> headers) {
        this.bytes = bytes;
        this.code = code;
        this.contentType = contentType;
        this.headers = headers;
    }

    static UrlResponse.Builder builder() {
        return new Builder();
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
        Charset charset = Charset.forName(parseCharset(headers(), "UTF-8"));
        return new String(bytes, charset);
    }

    @NonNull @Override public Map<String, String> headers() {
        return headers;
    }

    static class Builder {
        private byte[] bytes;
        private int code;
        private String contentType;
        private Map<String, String> headers;

        Builder setBytes(@NonNull byte[] bytes) {
            this.bytes = bytes;
            return this;
        }

        Builder setCode(int code) {
            this.code = code;
            return this;
        }

        Builder setContentType(@NonNull String contentType) {
            this.contentType = contentType;
            return this;
        }

        Builder setHeaders(@NonNull Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        UrlResponse build() {
            return new UrlResponse(bytes, code, contentType, headers);
        }
    }

    private static String parseCharset(Map<String, String> headers, String defaultCharset) {
        String contentType = headers.get("Content-Type");
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }

        return defaultCharset;
    }
}
