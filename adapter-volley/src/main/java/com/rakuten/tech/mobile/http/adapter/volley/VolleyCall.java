package com.rakuten.tech.mobile.http.adapter.volley;

import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.rakuten.tech.mobile.http.Http;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


class VolleyCall implements Http.Call {
    private final RequestQueue volleyQueue;
    private final VolleyRequest volleyRequest;
    private boolean cancelled = false;

    VolleyCall(RequestQueue queue, VolleyRequest request) {
        this.volleyQueue = queue;
        this.volleyRequest = request;
    }

    @NonNull @Override public Http.Request request() {
        return volleyRequest.request;
    }

    @NonNull @Override public Http.Response execute() throws IOException {
        RequestFuture<Http.Response> future = RequestFuture.newFuture();
        volleyRequest.errorListener = future;
        volleyRequest.successListener = future;
        volleyQueue.add(volleyRequest);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("IO Interruped", e);
        }
    }

    private static class VolleyCallback implements Response.Listener<Http.Response>, Response.ErrorListener {
        private final Http.Callback callback;
        private final Http.Call call;

        VolleyCallback(@NonNull Http.Call call, @NonNull Http.Callback callback) {
            this.callback = callback;
            this.call = call;
        }

        @Override public void onErrorResponse(VolleyError error) {
            callback.onFailure(call, new IOException("Volley error", error));
        }

        @Override public void onResponse(Http.Response response) {
            try {
                callback.onResponse(call, response);
            } catch (IOException ignored) {}
        }
    }

    @Override public void enqueue(@NonNull Http.Callback responseCallback) {
        VolleyCallback volleyCallback = new VolleyCallback(this, responseCallback);
        volleyRequest.errorListener = volleyCallback;
        volleyRequest.successListener = volleyCallback;
        volleyQueue.add(volleyRequest);
    }

    @Override public void cancel() {
        volleyQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override public boolean apply(Request<?> request) {
                // == is intentional to cancel only the request represented by that object
                return request == volleyRequest;
            }
        });
        cancelled = true;
    }

    @Override public boolean isCanceled() {
        return cancelled; // relying on volley's cancel contract https://developer.android.com/training/volley/simple.html#cancel
    }
}
