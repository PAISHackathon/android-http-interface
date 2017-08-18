package com.rakuten.tech.mobile.http.adapter.url;

import android.support.annotation.NonNull;

import com.rakuten.tech.mobile.http.Http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

@SuppressWarnings("WeakerAccess")
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

    @NonNull @Override public Http.Call newCall(@NonNull Http.Request request) {
        return new UrlCall(request, socketFactory, hostnameVerifier);
    }
}
