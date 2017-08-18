package com.rakuten.tech.mobile.http.interop;

import android.net.Uri;

import com.rakuten.tech.mobile.http.Http;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static com.rakuten.tech.mobile.http.interop.Condition.equalJsonTo;
import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

abstract public class HttpRequestSpec extends ClientSpec {
    @Test public void shouldSucceedGetRequest() {
        ValueRequest getRequest = ValueRequest.builder()
                .setMethod("GET")
                .setUrl(Uri.parse("http://httpbin.org/get"))
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
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.bytes()).isNotEmpty();
    }

    @Test public void shouldSucceedPostRequest() throws UnsupportedEncodingException, JSONException {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");
        JSONObject requestJson = new JSONObject(map);

        ValueRequest postRequest = ValueRequest.builder()
                .setMethod("POST")
                .setUrl(Uri.parse("http://httpbin.org/post"))
                .setBody(requestJson.toString().getBytes("UTF-8"))
                .build();
        Http.Call call = client.newCall(postRequest);

        Http.Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to execute request to " + postRequest.url() + " due to " + e.getMessage());
        }

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.bytes()).isNotEmpty();

        JSONObject responseJson = null;
        try {
            responseJson = new JSONObject(response.string());
        } catch (JSONException e) {
            e.printStackTrace();
            fail("Failed to parse JSON response to " + postRequest.url() + " due to " + e.getMessage());
        }

        assertThat(responseJson).isNotNull();
        assertThat(responseJson.getJSONObject("json")).is(equalJsonTo(requestJson));
    }
}
