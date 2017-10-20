package com.rakuten.tech.mobile.http.adapter.url;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.rakuten.tech.mobile.http.Http;
import com.rakuten.tech.mobile.http.Http.Call;
import com.rakuten.tech.mobile.http.Http.FailureCallback;
import com.rakuten.tech.mobile.http.Http.Request;
import com.rakuten.tech.mobile.http.Http.Response;
import com.rakuten.tech.mobile.http.Http.ResponseCallback;
import com.rakuten.tech.mobile.http.Http.Transform;
import com.rakuten.tech.mobile.http.util.TransformedCall;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;


class UrlCall implements Http.Call<Response> {

  private final Request request;
  private final SSLSocketFactory socketFactory;
  private final HostnameVerifier hostnameVerifier;
  private HttpCallAsyncTask task;

  UrlCall(@NonNull Http.Request request, @Nullable SSLSocketFactory socketFactory,
      @Nullable HostnameVerifier hostnameVerifier) {
    this.request = request;
    this.socketFactory = socketFactory;
    this.hostnameVerifier = hostnameVerifier;
  }

  @NonNull @Override public Http.Request request() {
    return request;
  }

  @SuppressWarnings({"ResultOfMethodCallIgnored", "TryFinallyCanBeTryWithResources"})
  @NonNull @Override public Response execute() throws IOException {
    HttpURLConnection connection = null;
    try {
      URL url = new URL(request.url().toString());
      connection = (HttpURLConnection) url.openConnection();
      connection.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());
      connection.setConnectTimeout(5*1000);
      connection.setReadTimeout(5*1000);
      connection.setUseCaches(false);
      connection.setDoInput(true);

      if (connection instanceof HttpsURLConnection) {
        HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
        if (socketFactory != null) {
          httpsConnection.setSSLSocketFactory(socketFactory);
        }
        if (hostnameVerifier != null) {
          httpsConnection.setHostnameVerifier(hostnameVerifier);
        }
      }

      connection.setRequestMethod(validateHttpMethod(request.method()));

      for (Map.Entry<String, String> header : request.headers().entrySet()) {
        connection.setRequestProperty(header.getKey(), header.getValue());
      }

      byte[] body = request().body();
      if (body.length > 0) {
        connection.setDoOutput(true);
        connection.addRequestProperty("Content-Type", request.contentType());
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.write(body);
        out.close();
      }

      UrlResponse.Builder responseBuilder = UrlResponse.builder();

      int code = connection.getResponseCode();
      if (code == -1) {
        throw new IOException("Could not retrieve response code from HttpUrlConnection.");
      }

      Map<String, String> responseHeaders = new HashMap<>();

      for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
        if (header.getKey() != null) {
          responseHeaders.put(header.getKey(), header.getValue().get(0));
        }
      }

      int contentLength = connection.getContentLength();
      byte[] bytes = new byte[Math.max(0, contentLength)];

      if (contentLength > 0) {
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
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  @Override public <S> Call<S> transform(Transform<Response, S> transform) {
    return new TransformedCall<>(this, transform);
  }

  @Override
  public void enqueue(ResponseCallback<Response> responseCallback,
      FailureCallback failureCallback) {
    task = new HttpCallAsyncTask(this, responseCallback, failureCallback);
    task.execute();
  }

  @Override public void cancel() {
    if (task != null) {
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
