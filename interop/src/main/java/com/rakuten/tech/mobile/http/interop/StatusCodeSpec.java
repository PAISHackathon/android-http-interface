package com.rakuten.tech.mobile.http.interop;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

import android.net.Uri;
import com.rakuten.tech.mobile.http.Http;
import com.rakuten.tech.mobile.http.util.ValueRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;

@RunWith(ParameterizedRobolectricTestRunner.class)
public abstract class StatusCodeSpec extends ClientSpec {

  private final int statusCode;
  private final int resultStatusCode;

  @ParameterizedRobolectricTestRunner.Parameters(name = "ErrorCode = {0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {201, 201}, {202, 202}, {203, 203},
        {301, 200}, {302, 200}, {303, 200},
        {401, 401}, {402, 402}, {403, 403}, {404, 404},
        {500, 500}
    });
  }

  public StatusCodeSpec(int statusCode, int resultStatusCode) {
    this.statusCode = statusCode;
    this.resultStatusCode = resultStatusCode;
  }

  @Test public void shouldProcessStatusCode() {

    ValueRequest getRequest = ValueRequest.builder()
        .method("HEAD")
        .url(Uri.parse("http://httpbin.org/status/" + statusCode))
        .build();
    Http.Call<Http.Response> call = client.newCall(getRequest);

    Http.Response response = null;
    try {
      response = call.execute();
    } catch (IOException e) {
      e.printStackTrace();
      fail("Failed to execute request to " + getRequest.url() + " due to " + e.getMessage());
    }

    assertThat(response).isNotNull();
    assertThat(response.code()).isEqualTo(resultStatusCode);
  }
}
