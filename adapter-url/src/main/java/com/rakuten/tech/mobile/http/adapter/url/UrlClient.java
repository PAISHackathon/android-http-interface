package com.rakuten.tech.mobile.http.adapter.url;

import android.support.annotation.NonNull;
import com.rakuten.tech.mobile.http.Http;
import com.rakuten.tech.mobile.http.Http.Response;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

@SuppressWarnings({"WeakerAccess", "unused"})
public class UrlClient implements Http.Client {

  private final SSLSocketFactory socketFactory;
  private final HostnameVerifier hostnameVerifier;

  public UrlClient() {
    this(null, null);
  }

  public UrlClient(SSLSocketFactory socketFactory, HostnameVerifier hostnameVerifier) {
    this.socketFactory = socketFactory;
    this.hostnameVerifier = hostnameVerifier;
  }

  @NonNull @Override public Http.Call<Response> newCall(@NonNull Http.Request request) {
    return new UrlCall(request, socketFactory, hostnameVerifier);
  }
}
