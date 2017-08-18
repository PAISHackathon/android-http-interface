package com.rakuten.tech.mobile.http.interop;

import android.net.Uri;

import com.rakuten.tech.mobile.http.Http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(ParameterizedRobolectricTestRunner.class)
public abstract class StatusCodeSpec extends ClientSpec {
    private final int statusCode;

    @ParameterizedRobolectricTestRunner.Parameters(name = "ErrorCode = {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {201}, {202}, {203},
                {301}, {302}, {303},
                {401}, {402}, {403}, {404}
        });
    }

    public StatusCodeSpec(int statusCode) {
        this.statusCode = statusCode;
    }

    @Test public void shouldProcessStatusCode() {

        ValueRequest getRequest = ValueRequest.builder()
                .setMethod("HEAD")
                .setUrl(Uri.parse("http://httpbin.org/status/" + statusCode))
                .build();
        Http.Call call = client.newCall(getRequest);

        Http.Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to execute request to " + getRequest.url() + " due to " + e.getMessage());
        }

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(statusCode);
    }
}
