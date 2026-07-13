package ai.firstreply.api;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * OkHttp interceptor that adds a {@code Authorization: Bearer <token>} header to every request.
 *
 * <p>Backed by a {@link TokenStore}, so the token can be refreshed at runtime (e.g. by
 * {@link RefreshAuthenticator} after a 401) without rebuilding the OkHttp client. Each request
 * reads the current token from the store, ensuring concurrent requests pick up refreshes
 * immediately.
 */
public class AuthenticationInterceptor implements Interceptor {

    private final TokenStore tokenStore;

    public AuthenticationInterceptor(String token) {
        this(new DefaultTokenStore(token, null));
    }

    public AuthenticationInterceptor(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String token = tokenStore.getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return chain.proceed(builder.build());
    }
}
