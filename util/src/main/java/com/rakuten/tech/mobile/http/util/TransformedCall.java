package com.rakuten.tech.mobile.http.util;

import android.support.annotation.NonNull;
import com.rakuten.tech.mobile.http.Http;
import com.rakuten.tech.mobile.http.Http.Call;
import com.rakuten.tech.mobile.http.Http.FailureCallback;
import com.rakuten.tech.mobile.http.Http.ResponseCallback;
import com.rakuten.tech.mobile.http.Http.Transform;
import java.io.IOException;


public class TransformedCall<S, T> implements Http.Call<T> {

    final private Call<S> wrapped;
    final private Transform<S, T> transform;

    @SuppressWarnings("WeakerAccess")
    public TransformedCall(Call<S> wrapped, Transform<S, T> transform) {
      this.wrapped = wrapped;
      this.transform = transform;
    }

    @NonNull @Override public Http.Request request() {
      return wrapped.request();
    }

    @NonNull @Override public T execute() throws IOException {
      return transform.apply(wrapped.execute());
    }

    @Override public <V> Call<V> transform(Transform<T, V> transform) {
      return new TransformedCall<>(this, transform);
    }

    @Override public void enqueue(final ResponseCallback<T> responseCallback,
        final FailureCallback failureCallback) {
      final Http.Call<T> tCall = this;
      final Transform<S, T> transform = this.transform;
      wrapped.enqueue(
          (call, response) -> responseCallback.onResponse(tCall, transform.apply(response)),
          failureCallback);
    }

    @Override public void cancel() {
      wrapped.cancel();
    }

    @Override public boolean isCanceled() {
      return wrapped.isCanceled();
    }
  }

