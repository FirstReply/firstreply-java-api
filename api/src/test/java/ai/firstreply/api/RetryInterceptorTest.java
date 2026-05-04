package ai.firstreply.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RetryInterceptorTest {

    private MockWebServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void retriesOn503AndSucceeds() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(503));
        server.enqueue(new MockResponse().setResponseCode(503));
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3, Duration.ofMillis(1), Duration.ofMillis(10)))
                .build();

        try (Response resp = client.newCall(new Request.Builder().url(server.url("/v1/test")).build()).execute()) {
            assertEquals(200, resp.code());
        }
        assertEquals(3, server.getRequestCount());
    }

    @Test
    void retriesOn429HonoringRetryAfter() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(429).setHeader("Retry-After", "0"));
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(2, Duration.ofMillis(1), Duration.ofMillis(10)))
                .build();

        try (Response resp = client.newCall(new Request.Builder().url(server.url("/v1/test")).build()).execute()) {
            assertEquals(200, resp.code());
        }
        assertEquals(2, server.getRequestCount());
    }

    @Test
    void doesNotRetryPostByDefault() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(503));

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(3, Duration.ofMillis(1), Duration.ofMillis(10)))
                .build();

        Request post = new Request.Builder()
                .url(server.url("/v1/test"))
                .post(okhttp3.RequestBody.create("{}", okhttp3.MediaType.get("application/json")))
                .build();
        try (Response resp = client.newCall(post).execute()) {
            assertEquals(503, resp.code());
        }
        assertEquals(1, server.getRequestCount());
    }

    @Test
    void exhaustsRetriesAndReturnsLastFailure() throws Exception {
        for (int i = 0; i < 4; i++) {
            server.enqueue(new MockResponse().setResponseCode(500));
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor(2, Duration.ofMillis(1), Duration.ofMillis(10)))
                .build();

        try (Response resp = client.newCall(new Request.Builder().url(server.url("/v1/test")).build()).execute()) {
            assertEquals(500, resp.code());
        }
        // 1 initial + 2 retries = 3
        assertEquals(3, server.getRequestCount());
    }

    @Test
    void parseRetryAfterHandlesSeconds() {
        Duration parsed = RetryInterceptor.parseRetryAfter("5");
        assertEquals(Duration.ofSeconds(5), parsed);
    }

    @Test
    void parseRetryAfterHandlesNullAndGarbage() {
        org.junit.jupiter.api.Assertions.assertNull(RetryInterceptor.parseRetryAfter(null));
        org.junit.jupiter.api.Assertions.assertNull(RetryInterceptor.parseRetryAfter("not-a-date"));
    }
}
