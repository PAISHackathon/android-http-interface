package com.rakuten.tech.mobile.http.adapter.url;

import android.os.AsyncTask;
import com.rakuten.tech.mobile.http.Http.Call;
import com.rakuten.tech.mobile.http.Http.FailureCallback;
import com.rakuten.tech.mobile.http.Http.Response;
import com.rakuten.tech.mobile.http.Http.ResponseCallback;
import java.io.IOException;
import java.lang.ref.WeakReference;

class HttpCallAsyncTask extends AsyncTask<Void, Void, HttpCallAsyncTask.AsyncResult> {

  private final Call<Response> call;
  private final WeakReference<ResponseCallback<Response>> responseCallbackRef;
  private final WeakReference<FailureCallback> failureCallbackRef;

  HttpCallAsyncTask(Call<Response> call, ResponseCallback<Response> responseCallback,
      FailureCallback failureCallback) {
    super();
    this.call = call;
    this.responseCallbackRef = new WeakReference<>(responseCallback);
    this.failureCallbackRef = new WeakReference<>(failureCallback);
  }

  static final class AsyncResult {

    Response response;
    IOException error;
  }

  @Override protected AsyncResult doInBackground(Void... params) {
    AsyncResult result = new AsyncResult();
    try {
      result.response = call.execute();
    } catch (IOException e) {
      result.error = e;
    }

    return result;
  }

  @Override protected void onPostExecute(AsyncResult result) {
    FailureCallback failureCallback = this.failureCallbackRef.get();
    ResponseCallback<Response> responseCallback = this.responseCallbackRef.get();
    if (result.error != null && failureCallback != null) {
      failureCallback.onFailure(call, result.error);
    } else if (result.response != null && responseCallback != null) {
      responseCallback.onResponse(call, result.response);
    }
  }
}
