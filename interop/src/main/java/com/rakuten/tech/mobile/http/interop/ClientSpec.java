package com.rakuten.tech.mobile.http.interop;

import android.support.annotation.NonNull;

import com.rakuten.tech.mobile.http.Http;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
abstract public class ClientSpec {
    protected Http.Client client;

    @NonNull abstract protected Http.Client newClient();

    @SuppressWarnings("WeakerAccess") protected void destroyClient(@NonNull Http.Client client) {
        // override if you need to tear down
    }

    @Before public void setup() {
        client = newClient();
    }

    @After public void teardown() {
        destroyClient(client);
    }

}