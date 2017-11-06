package com.rakuten.tech.mobile.http.adapter.volley;

import android.content.Context;
import android.support.annotation.NonNull;
import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.rakuten.tech.mobile.http.Http;
import com.rakuten.tech.mobile.http.interop.HttpRequestAsyncSpec;
import com.rakuten.tech.mobile.http.interop.HttpRequestSyncSpec;
import com.rakuten.tech.mobile.http.interop.StatusCodeSpec;
import java.io.File;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

@SuppressWarnings("unused")
class VolleyClientSpec {

  @SuppressWarnings("WeakerAccess") static Http.Client newClient() {
    RequestQueue queue = requestQueue(RuntimeEnvironment.application);
    return new VolleyClient(queue);
  }

  public static class VolleyHttpRequestSyncSpec extends HttpRequestSyncSpec {
    @Test public void dummyTest() {}

    @NonNull @Override protected Http.Client newClient() {
      return VolleyClientSpec.newClient();
    }
  }

  public static class VolleyHttpRequestAsyncSpec extends HttpRequestAsyncSpec {
    @Test public void dummyTest() {}

    @NonNull @Override protected Http.Client newClient() {
      return VolleyClientSpec.newClient();
    }
  }

  public static class VolleyStatusCodeSpec extends StatusCodeSpec {
    @Test public void dummyTest() {}

    public VolleyStatusCodeSpec(int statusCode, int restultStatusCode) {
      super(statusCode, restultStatusCode);
    }

    @NonNull @Override protected Http.Client newClient() {
      return VolleyClientSpec.newClient();
    }
  }



  /**
   * To get real network response with Volley during a unit test,
   * the executor must be changed so that it doesn't use main looper
   */
  private static RequestQueue requestQueue(final Context context) {
    File cacheDir = new File(context.getCacheDir(), "cache/volley");
    Network network = new BasicNetwork(new HurlStack());
    ResponseDelivery responseDelivery = new ExecutorDelivery(Executors.newSingleThreadExecutor());
    RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network, 4,
        responseDelivery);
    queue.start();
    return queue;
  }
}
