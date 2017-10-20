package com.rakuten.tech.mobile.http;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Map;

/**
 * <p>HTTP abstraction layer inspired by okhttp3 interfaces
 *
 * <p>Encodes minimal contract that our libraries expect from an http client implementation.
 */
@SuppressWarnings("unused")
public interface Http {
    interface ResponseCallback<T> {
        void onResponse(@NonNull Call<T> call, @NonNull T response);
    }

    interface FailureCallback {
        void onFailure(@NonNull Call call, IOException e);
    }

    interface Request {
        byte[] body();
        @NonNull Map<String, String> headers();
        @NonNull String contentType();
        @NonNull Uri url();
        @NonNull String method();
    }

    interface Response {
        @NonNull byte[] bytes();
        int code();
        @NonNull String contentType();
        @NonNull String string();
        @NonNull Map<String, String> headers();
    }

    interface Client {

        /**
         * <p>Creates a new {@link Call<Response>} that should be ready to execute without any other
         * steps of preparation
         * @param request request configuration
         * @return call ready to be executed
         */
        @NonNull Call<Response> newCall(@NonNull Request request);
    }

    interface Transform<S, T> {
        @NonNull T apply(S response);
    }

    interface Call<T> {

        /**
         * @return the request that defines this call
         */
        @NonNull Request request();

        /**
         * <p>Execute the HTTP request synchronously
         * @return result of the request
         * @throws IOException IO fails - note that this should <b>not</b> throw when HTTP status
         * codes signal errors.
         */
        @NonNull T execute() throws IOException;

        /**
         * <p>Apply a transformation to the response. If the call is executed asynchronously via
         * {@link #enqueue(ResponseCallback, FailureCallback)} the transformation should be
         * executed on a background thread.
         *
         * <p>A typical use case for such a transformation is parsing an raw http response into
         * java model objects, e.g.
         *
         * <code>
         *   Call<Http.Response> call = client.newCall(request);
         *   Call<JSONObject> transformedCall = call.transform(response -> response -> {
         *     try {
         *       return new JSONObject(response.string());
         *     } catch (JSONException e) {
         *       return new JSONObject();
         *     }
         *   });
         *   JSONObject parsed = transformedCall.enqueue();
         * </code>
         *
         * <p>another way of using this is to execute multiple sequential http requests on a
         * background thread, e.g.
         *
         * <code>
         *   Call<Http.Response> call = client.newCall(request);
         *   Call<JSONObject> transformedCall = call.transform(response -> response -> {
         *     String nextRequestUrl = JSONObject(response.string()).getString("url");
         *     Request<Http.Response> nextRequest = ValueRequest.build().method("GET").url(url).build();
         *     try {
         *       Http.Response nextResponse = client.newCall(nextRequest).execute();
         *       return new JSONObject(nextResponse.string())
         *     } catch (IOException | JSONException e) {
         *       return new JSONObject();
         *     }
         *   });
         *   JSONObject parsed = call.execute();
         * </code>
         *
         * @param transform transformation function that will be applied to the response
         * @param <V> type of the result of the transformation
         * @return new call that returns the transformed result when executed.
         */
        <V> Call<V> transform(Transform<T, V> transform);

        /**
         * <p>Execute the HTTP request on a <b>background thread</b> and return to the <b>main
         * thread</b> with the result or failure.
         *
         * @param responseCallback will be called with the response on the main thread
         * @param failureCallback wil be called with the cause of failure on the main thread
         */
        void enqueue(ResponseCallback<T> responseCallback, FailureCallback failureCallback);

        /**
         * <p>Abort executing the request.
         *
         * <p>If a request is executed asynchronouly via {@link #enqueue(ResponseCallback, FailureCallback)}
         * a call to {@link #cancel()} should abort the execution if possible. If the request was
         * aborted successfully the failure callback should be notified and the success callback
         * should not be called.
         *
         * <p>Should have no effect if the request already finished or hasn't started yet or the
         * request in executing synchronously via {@link #execute()}.
         */
        void cancel();

        /**
         * @return If {@link #cancel()} was called.
         */
        boolean isCanceled();
    }
}
