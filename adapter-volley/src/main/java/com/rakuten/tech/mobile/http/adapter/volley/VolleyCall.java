package com.rakuten.tech.mobile.http.adapter.volley;

import android.support.annotation.NonNull;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.rakuten.tech.mobile.http.Http;
import com.rakuten.tech.mobile.http.Http.Call;
import com.rakuten.tech.mobile.http.Http.Transform;
import com.rakuten.tech.mobile.http.util.TransformedCall;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


class VolleyCall implements Http.Call<Http.Response> {

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
      if (e instanceof ExecutionException && e.getCause() instanceof VolleyError) {
        return new VolleyResponse(((VolleyError) e.getCause()).networkResponse);
      }
      throw new IOException("IO Interrupted", e);
    }
  }

  @Override public <T> Call<T> transform(Transform<Http.Response, T> transform) {
    return new TransformedCall<>(this, transform);
  }

  private static class VolleyCallback implements Response.Listener<Http.Response>,
      Response.ErrorListener {

    private final Http.ResponseCallback<Http.Response> responseCallback;
    private final Http.FailureCallback failureCallback;
    private final Http.Call<Http.Response> call;


    VolleyCallback(@NonNull Http.Call<Http.Response> call,
        Http.ResponseCallback<Http.Response> responseCallback,
        Http.FailureCallback failureCallback) {
      this.call = call;
      this.responseCallback = responseCallback;
      this.failureCallback = failureCallback;
    }

    @Override public void onErrorResponse(VolleyError error) {
      if (failureCallback != null) {
        failureCallback.onFailure(call, new IOException("Volley error", error));
      }
    }

    @Override public void onResponse(Http.Response response) {
      if (responseCallback != null) {
        responseCallback.onResponse(call, response);
      }
    }
  }

  @Override
  public void enqueue(Http.ResponseCallback<Http.Response> responseCallback,
      Http.FailureCallback failureCallback) {
    VolleyCallback volleyCallback = new VolleyCallback(this, responseCallback, failureCallback);
    volleyRequest.errorListener = volleyCallback;
    volleyRequest.successListener = volleyCallback;
    volleyQueue.add(volleyRequest);
  }

  @SuppressWarnings("UnnecessaryLocalVariable")
  @Override public void cancel() {
    final VolleyRequest request = this.volleyRequest;
    volleyQueue.cancelAll(candidate -> {
      // == is intentional to cancel only the request represented by that object
      return request == candidate;
    });
    cancelled = true;
  }

  @Override public boolean isCanceled() {
    // relying on volley's cancel contract https://developer.android.com/training/volley/simple.html#cancel
    return cancelled;
  }
}
