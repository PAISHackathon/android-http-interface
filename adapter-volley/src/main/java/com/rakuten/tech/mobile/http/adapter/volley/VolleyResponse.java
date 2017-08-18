package com.rakuten.tech.mobile.http.adapter.volley;

import android.support.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;
import com.rakuten.tech.mobile.http.Http;

import java.nio.charset.Charset;
import java.util.Map;


class VolleyResponse implements Http.Response {
    final private NetworkResponse volleyResponse;

    VolleyResponse(@NonNull NetworkResponse volleyResponse) {
        this.volleyResponse = volleyResponse;
    }

    @Override public byte[] bytes() {
        return volleyResponse.data;
    }

    @Override public int code() {
        return volleyResponse.statusCode;
    }

    @NonNull @Override public String contentType() {
        return volleyResponse.headers.get("ContentType");
    }

    @NonNull @Override public String string() {
        Charset charset = Charset.forName(HttpHeaderParser.parseCharset(volleyResponse.headers, "UTF-8"));
        return new String(volleyResponse.data, charset);
    }

    @NonNull @Override public Map<String, String> headers() {
        return volleyResponse.headers;
    }
}
