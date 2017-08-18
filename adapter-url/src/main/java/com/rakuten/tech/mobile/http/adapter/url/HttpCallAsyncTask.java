package com.rakuten.tech.mobile.http.adapter.url;

import android.os.AsyncTask;

import com.rakuten.tech.mobile.http.Http;

import java.io.IOException;
import java.lang.ref.WeakReference;

class HttpCallAsyncTask extends AsyncTask<Void, Void, HttpCallAsyncTask.AsyncResult> {
    private final Http.Call call;
    private final WeakReference<Http.ResponseCallback> responseCallbackRef;
    private final WeakReference<Http.FailureCallback> failureCallbackRef;

    HttpCallAsyncTask(Http.Call call, Http.ResponseCallback responseCallback, Http.FailureCallback failureCallback) {
        super();
        this.call = call;
        this.responseCallbackRef = new WeakReference<>(responseCallback);
        this.failureCallbackRef = new WeakReference<>(failureCallback);
    }

    static final class AsyncResult {
        Http.Response response;
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
        Http.FailureCallback failureCallback = this.failureCallbackRef.get();
        Http.ResponseCallback responseCallback = this.responseCallbackRef.get();
        if (result.error != null && failureCallback != null) {
            failureCallback.onFailure(call, result.error);
        } else if (result.response != null && responseCallback != null) {
            responseCallback.onResponse(call, result.response);
        }
    }
}
