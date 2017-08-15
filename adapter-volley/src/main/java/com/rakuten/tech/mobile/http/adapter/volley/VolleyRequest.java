package com.rakuten.tech.mobile.http.adapter.volley;

import android.support.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.rakuten.tech.mobile.http.Http;


class VolleyRequest extends Request<Http.Response> {

    Response.ErrorListener errorListener = null;
    Response.Listener<Http.Response> successListener = null;


    final Http.Request request;

    VolleyRequest(Http.Request request) {
        super(convertMethod(request.method()), request.url().toString(), null);

        this.request = request;
    }

    @Override public void deliverError(VolleyError error) {
        if (errorListener != null) errorListener.onErrorResponse(error);
    }

    @Override protected Response<Http.Response> parseNetworkResponse(NetworkResponse response) {
        Http.Response volleyResponse = new VolleyResponse(response);
        return Response.success(volleyResponse, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override public void deliverResponse(Http.Response response) {
        if (successListener != null) successListener.onResponse(response);
    }

    private static int convertMethod(@NonNull String method) {
        switch (method.toLowerCase()) {
            case "get": return Method.GET;
            case "post": return Method.GET;
            case "put": return Method.GET;
            case "delete": return Method.GET;
            case "head": return Method.GET;
            case "options": return Method.GET;
            case "trace": return Method.GET;
            case "patch": return Method.GET;
            default: return Method.DEPRECATED_GET_OR_POST;
        }
    }
}
