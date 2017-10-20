# HTTP Abstraction Layer for Android SDKs

## What?

A couple of interfaces that define a minimal HTTP client contract, inspired by [OkHttp3](https://square.github.io/okhttp/) interfaces.

## Why?

We should not couple SDKs to any specific HTTP client implemenetation. If an SDK leaks this implementation detail we are locked into supporting that library until the next major version. 

Here are a few Examples:

```java
public class EngineClient {
    // BaseRequest subclasses com.android.volley.Request
    public BaseRequest<TokenResult> token(TokenParam param, Response.Listener<TokenResult> listener, Response.ErrorListener errorListener) {
        // ...
    }
}

// usage

EngineClient client = // ..
// ↓ exposing & forcing volley ↓
RequestQueue queue = // ...
RequestFuture<TokenResult> future = RequestFuture.newFuture();
client.token(param, future, future)
        .setTag(LoginConfig.REQUESTTAG_AUTH_IGNORE)
        .queue(queue);
TokenResult result = future.get(10, TimeUnit.SECONDS);
```

```java
public class Tracker {
    // the queue is a com.android.volley.RequestQueue
    public static Tracker initialize(Context context, RequestQueue queue) {
        // ...
    }
}

// usage

Context context = // ..
RequestQueue queue = // ...
Tracker tracker = Tracker.initialize(context, queue);
```

## How?

This repo defines a module of java interfaces that describe the contract for different HTTP clients to implement. Other modules are adapaters that convert common HTTP client libraries to that contract. With these tools we can remove coupling with implementation details 🤗. 

Here are the examples from above, fixed with our HTTP interfaces:

```java
public class EngineClient {
    Http.Client client;

    // no more volley specific code in SDK public interface 🤗
    public EngineClient(Http.Client client) {
        this.client = client;
    }

    // no more volley specific code in SDK public interface 🤗
    public Call<TokenResult> token(TokenParam param) {
        // ...
    }
}

RequestQueue queue = // ...
EngineClient client = new EngineClient(new VolleyClient(queue)); // using volley adapter
// ↓ exposing HTTP abstraction layer  ↓
Http.Call call = client.token(param); 
Http.Response response = call.execute();
TokenResult result = TokenResult.fromJson(response.string());
```

```java
public class Tracker {
    // no more volley specific code in SDK public interface 🤗
    public static Tracker initialize(Context context, Http.Client client) {
        // ...
    }
}

// usage (in app)

Context context = // ..
RequestQueue queue = // ...
Tracker tracker = Tracker.initialize(context, new VolleyClient(queue)); // using volley adapter
```
