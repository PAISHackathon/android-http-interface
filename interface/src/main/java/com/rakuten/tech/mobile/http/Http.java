package com.rakuten.tech.mobile.http;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Map;

/**
 * HTTP abstraction layer inspired by okhttp3 interfaces
 */
@SuppressWarnings("unused")
public interface Http {
    interface ResponseCallback {
        void onResponse(@NonNull Call call, @NonNull Response response);
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
        @NonNull Call newCall(@NonNull Request request);
    }

    interface Call {
        @NonNull Request request();
        @NonNull Response execute() throws IOException;
        void enqueue(ResponseCallback responseCallback, FailureCallback failureCallback);
        void cancel();
        boolean isCanceled();
    }
}
