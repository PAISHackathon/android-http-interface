package com.rakuten.tech.mobile.http.adatpter.url;

import android.support.annotation.NonNull;
import com.rakuten.tech.mobile.http.Http;
import com.rakuten.tech.mobile.http.adapter.url.UrlClient;
import com.rakuten.tech.mobile.http.interop.HttpRequestAsyncSpec;
import com.rakuten.tech.mobile.http.interop.HttpRequestSyncSpec;
import com.rakuten.tech.mobile.http.interop.StatusCodeSpec;
import org.junit.Test;

@SuppressWarnings("unused")
class UrlClientSpec {

  @SuppressWarnings("WeakerAccess") static Http.Client newClient() {
    return new UrlClient();
  }

  public static class UrlHttpRequestSyncSpec extends HttpRequestSyncSpec {
    @Test public void dummyTest() {}

    @NonNull @Override protected Http.Client newClient() {
      return UrlClientSpec.newClient();
    }
  }

  public static class UrlHttpRequestAsyncSpec extends HttpRequestAsyncSpec {
    @Test public void dummyTest() {}

    @NonNull @Override protected Http.Client newClient() {
      return UrlClientSpec.newClient();
    }
  }

  public static class UrlStatusCodeSpec extends StatusCodeSpec {
    @Test public void dummyTest() {}

    public UrlStatusCodeSpec(int statusCode, int restultStatusCode) {
      super(statusCode, restultStatusCode);
    }

    @NonNull @Override protected Http.Client newClient() {
      return UrlClientSpec.newClient();
    }
  }
}
