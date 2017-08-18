package com.rakuten.tech.mobile.http.interop;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.rakuten.tech.mobile.http.Http;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public final class ValueRequest implements Http.Request {
    private final byte[] body;
    private final Map<String, String> headers;
    private final String contentType;
    private final Uri url;
    private final String method;

    ValueRequest(byte[] body, Map<String, String> headers, String contentType, Uri url, String method) {
        this.body = body;
        this.headers = headers;
        this.contentType = contentType;
        this.url = url;
        this.method = method;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override public byte[] body() {
        return body;
    }

    @NonNull @Override public Map<String, String> headers() {
        return headers;
    }

    @NonNull @Override public String contentType() {
        return contentType;
    }

    @NonNull @Override public Uri url() {
        return url;
    }

    @NonNull @Override public String method() {
        return method;
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private byte[] body = new byte[0];
        private Map<String, String> headers = new HashMap<>();
        private String contentType = "";
        private Uri url;
        private String method;

        public Builder setBody(@NonNull byte[] body) {
            this.body = body;
            return this;
        }

        public Builder setHeaders(@NonNull Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder setContentType(@NonNull String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder setUrl(@NonNull Uri url) {
            this.url = url;
            return this;
        }

        public Builder setMethod(@NonNull String method) {
            this.method = method;
            return this;
        }

        public ValueRequest build() {
            if(url == null || method == null) throw new IllegalArgumentException("url and HTTP method may not be null");
            return new ValueRequest(body, headers, contentType, url, method);
        }
    }
}
