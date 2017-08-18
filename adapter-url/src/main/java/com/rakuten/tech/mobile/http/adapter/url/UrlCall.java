package com.rakuten.tech.mobile.http.adapter.url;

import android.support.annotation.NonNull;

import com.rakuten.tech.mobile.http.Http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;


class UrlCall implements Http.Call {
    private final Http.Request request;
    private final SSLSocketFactory socketFactory;
    private final HostnameVerifier hostnameVerifier;
    private HttpCallAsyncTask task;

    UrlCall(@NonNull Http.Request request, SSLSocketFactory socketFactory, HostnameVerifier hostnameVerifier) {
        this.request = request;
        this.socketFactory = socketFactory;
        this.hostnameVerifier = hostnameVerifier;
    }

    @NonNull @Override public Http.Request request() {
        return request;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "TryFinallyCanBeTryWithResources"})
    @NonNull @Override public Http.Response execute() throws IOException {
        HttpURLConnection connection = null;
        try {

            URL url = new URL(request.url().toString());
            connection = (HttpURLConnection) url.openConnection();

            if(connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                if(socketFactory != null) httpsConnection.setSSLSocketFactory(socketFactory);
                if(hostnameVerifier != null) httpsConnection.setHostnameVerifier(hostnameVerifier);
            }

            connection.setRequestMethod(validateHttpMethod(request.method()));

            for(Map.Entry<String, String> header: request.headers().entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            byte[] body = request().body();
            if (body.length > 0) {
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(body.length);
                connection.addRequestProperty("Content-Type", request.contentType());
                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                out.write(body);
            }

            UrlResponse.Builder responseBuilder = UrlResponse.builder();

            int code = connection.getResponseCode();
            if (code == -1) {
                throw new IOException("Could not retrieve response code from HttpUrlConnection.");
            }

            Map<String, String> responseHeaders = new HashMap<>();

            for (Map.Entry<String, List<String>> header: connection.getHeaderFields().entrySet()) {
                if(header.getKey() != null) {
                    responseHeaders.put(header.getKey(), header.getValue().get(0));
                }
            }

            int contentLength = connection.getContentLength();
            byte[] bytes = new byte[Math.max(0, contentLength)];

            if(contentLength > 0) {
                InputStream inputStream; // taken from volley - not sure this is what we want...
                try {
                    inputStream = connection.getInputStream();
                } catch (IOException ioe) {
                    inputStream = connection.getErrorStream();
                }

                InputStream in = new BufferedInputStream(inputStream);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }
            }

            return responseBuilder.setContentType(connection.getContentType())
                    .setHeaders(responseHeaders)
                    .setCode(code)
                    .setBytes(bytes)
                    .build();

        } finally {
            if(connection != null) connection.disconnect();
        }
    }

    @Override
    public void enqueue(Http.ResponseCallback responseCallback, Http.FailureCallback failureCallback) {
        task = new HttpCallAsyncTask(this, responseCallback, failureCallback);
        task.execute();
    }

    @Override public void cancel() {
        if(task != null) {
            task.cancel(true);
            task = null;
        }
    }

    @Override public boolean isCanceled() {
        return task == null;
    }

    private String validateHttpMethod(@NonNull String method) throws IOException {
        String candidate = method.toUpperCase();
        switch (candidate) {
            case "GET":
            case "POST":
            case "HEAD":
            case "OPTIONS":
            case "PUT":
            case "DELETE":
            case "TRACE":
                return candidate;
            default:
                throw new IOException("Unsupported HTTP method " + method);
        }
    }

}
