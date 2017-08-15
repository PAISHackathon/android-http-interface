package com.rakuten.tech.mobile.http.adapter.volley;

import android.support.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.rakuten.tech.mobile.http.Http;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;


class VolleyResponse implements Http.Response {
    final private NetworkResponse volleyResponse;

    VolleyResponse(NetworkResponse volleyResponse) {
        this.volleyResponse = volleyResponse;
    }

    @Override public byte[] bytes() {
        return volleyResponse.data;
    }

    @NonNull @Override public String contentType() {
        return volleyResponse.headers.get("ContentType");
    }

    @NonNull @Override public String string() {
        return new String(volleyResponse.data, getResponseCharset(volleyResponse));
    }

    @NonNull @Override public Map<String, String> headers() {
        return volleyResponse.headers;
    }

    private static Charset getResponseCharset(NetworkResponse response) {
        String contentType = response.headers.get("Content-Type");
        if(contentType != null) {
            String[] params = contentType.split(";");

            for(int i = 1; i < params.length; ++i) {
                String[] pair = params[i].trim().split("=");
                if(pair.length == 2 && pair[0].equals("charset")) {
                    try {
                        return Charset.forName(pair[1].replaceAll("\"", ""));
                    } catch (IllegalCharsetNameException | UnsupportedCharsetException ignored) {}
                }
            }
        }

        return Charset.forName("UTF-8");
    }
}
