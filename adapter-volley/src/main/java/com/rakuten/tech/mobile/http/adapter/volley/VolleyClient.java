package com.rakuten.tech.mobile.http.adapter.volley;

import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.rakuten.tech.mobile.http.Http;


public class VolleyClient implements Http.Client {
    private RequestQueue queue;

    public VolleyClient(@NonNull RequestQueue queue) {
        this.queue = queue;
    }

    @NonNull @Override public Http.Call newCall(@NonNull Http.Request request) {
        VolleyRequest volleyRequest = new VolleyRequest(request);
        return new VolleyCall(queue, volleyRequest);
    }
}
