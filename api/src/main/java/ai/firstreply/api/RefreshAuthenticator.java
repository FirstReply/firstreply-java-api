package ai.firstreply.api;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import java.util.Objects;

/**
 * OkHttp {@link Authenticator} that refreshes the access token when the server returns 401.
 *
 * <p>Behavior:
 * <ul>
 *   <li>Returns {@code null} (giving up) for non-Bearer requests, requests without an
 *       {@code Authorization} header, or after the configured retry depth is reached.</li>
 *   <li>Calls {@link TokenStore#refresh(String)} with the previously-used token; the
 *       {@link DefaultTokenStore} reference implementation guarantees only one refresh per
 *       expired token across concurrent threads.</li>
 *   <li>If the refreshed token equals the expired token (refresh failed or no-op) the
 *       authenticator returns {@code null} to surface the 401 to the caller.</li>
 * </ul>
 *
 * <p>Pair this with {@link AuthenticationInterceptor} so requests carry the latest token from the
 * {@link TokenStore} on every attempt (the interceptor reads the store on each call).
 */
public class RefreshAuthenticator implements Authenticator {

    private static final int DEFAULT_MAX_ATTEMPTS = 1;

    private final TokenStore tokenStore;
    private final int maxAttempts;

    public RefreshAuthenticator(TokenStore tokenStore) {
        this(tokenStore, DEFAULT_MAX_ATTEMPTS);
    }

    public RefreshAuthenticator(TokenStore tokenStore, int maxAttempts) {
        this.tokenStore = Objects.requireNonNull(tokenStore, "tokenStore");
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        this.maxAttempts = maxAttempts;
    }

    @Override
    public Request authenticate(Route route, Response response) {
        if (responseCount(response) >= maxAttempts + 1) {
            return null;
        }

        String previousAuth = response.request().header("Authorization");
        String expiredToken = extractBearerToken(previousAuth);
        if (expiredToken == null) {
            return null;
        }

        String fresh = tokenStore.refresh(expiredToken);
        if (fresh == null || fresh.equals(expiredToken)) {
            return null;
        }

        return response.request().newBuilder()
                .header("Authorization", "Bearer " + fresh)
                .build();
    }

    private static String extractBearerToken(String authHeader) {
        if (authHeader == null) {
            return null;
        }
        String prefix = "Bearer ";
        if (authHeader.regionMatches(true, 0, prefix, 0, prefix.length())) {
            return authHeader.substring(prefix.length()).trim();
        }
        return null;
    }

    private static int responseCount(Response response) {
        int count = 1;
        Response prior = response.priorResponse();
        while (prior != null) {
            count++;
            prior = prior.priorResponse();
        }
        return count;
    }
}
