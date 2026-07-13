package ai.firstreply.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TokenRefreshTest {

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
    void refreshesTokenOn401AndRetries() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401));
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        AtomicInteger refreshCount = new AtomicInteger();
        DefaultTokenStore store = new DefaultTokenStore("expired-token", expired -> {
            refreshCount.incrementAndGet();
            return "fresh-token";
        });

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(store))
                .authenticator(new RefreshAuthenticator(store))
                .build();

        try (Response resp = client.newCall(new Request.Builder().url(server.url("/v1/me")).build()).execute()) {
            assertEquals(200, resp.code());
        }

        assertEquals(2, server.getRequestCount());
        RecordedRequest first = server.takeRequest();
        RecordedRequest second = server.takeRequest();
        assertEquals("Bearer expired-token", first.getHeader("Authorization"));
        assertEquals("Bearer fresh-token", second.getHeader("Authorization"));
        assertEquals(1, refreshCount.get());
        assertEquals("fresh-token", store.getToken());
    }

    @Test
    void givesUpWhenRefreshReturnsNull() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401));

        DefaultTokenStore store = new DefaultTokenStore("expired-token", expired -> null);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(store))
                .authenticator(new RefreshAuthenticator(store))
                .build();

        try (Response resp = client.newCall(new Request.Builder().url(server.url("/v1/me")).build()).execute()) {
            assertEquals(401, resp.code());
        }
        assertEquals(1, server.getRequestCount());
    }

    @Test
    void singleFlightSkipsRefreshIfTokenAlreadyChanged() {
        DefaultTokenStore store = new DefaultTokenStore("token-A", expired -> "should-not-be-called");
        // Simulate concurrent refresh: another thread already swapped the token before we acquired the lock.
        store.setToken("token-B");
        String result = store.refresh("token-A");
        assertEquals("token-B", result);
        assertEquals("token-B", store.getToken());
    }

    @Test
    void nullRefreshFunctionReturnsCurrentToken() {
        DefaultTokenStore store = new DefaultTokenStore("only-token", null);
        assertEquals("only-token", store.refresh("only-token"));
    }

    @Test
    void requestWithoutBearerHeaderIsNotRefreshed() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401));

        AtomicInteger refreshCount = new AtomicInteger();
        DefaultTokenStore store = new DefaultTokenStore(null, expired -> {
            refreshCount.incrementAndGet();
            return "fresh";
        });

        OkHttpClient client = new OkHttpClient.Builder()
                .authenticator(new RefreshAuthenticator(store))
                .build();

        try (Response resp = client.newCall(new Request.Builder().url(server.url("/v1/me")).build()).execute()) {
            assertEquals(401, resp.code());
        }
        assertEquals(1, server.getRequestCount());
        assertEquals(0, refreshCount.get());
        assertNull(store.getToken());
    }
}
