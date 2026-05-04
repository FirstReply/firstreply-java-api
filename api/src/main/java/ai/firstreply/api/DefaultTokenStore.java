package ai.firstreply.api;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Thread-safe {@link TokenStore} that performs a single-flight refresh: when multiple requests
 * fail with 401 concurrently, only one calls the supplied {@code refreshFn}; the rest wait and
 * pick up the result. If the stored token has already been replaced by a different thread between
 * the 401 and acquiring the lock, refresh is skipped (avoids redundant network calls).
 */
public class DefaultTokenStore implements TokenStore {

    private final Function<String, String> refreshFn;
    private final ReentrantLock lock = new ReentrantLock();
    private volatile String token;

    /**
     * @param initialToken initial access token, may be {@code null}
     * @param refreshFn    function invoked under the refresh lock; receives the expired token and
     *                     returns a new token, or {@code null} if refresh failed. May be
     *                     {@code null} to disable automatic refresh (always returns current token)
     */
    public DefaultTokenStore(String initialToken, Function<String, String> refreshFn) {
        this.token = initialToken;
        this.refreshFn = refreshFn;
    }

    @Override
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String refresh(String expiredToken) {
        if (refreshFn == null) {
            return token;
        }
        lock.lock();
        try {
            // Another thread may have refreshed while we were waiting for the lock.
            String current = token;
            if (current != null && !Objects.equals(current, expiredToken)) {
                return current;
            }
            String fresh = refreshFn.apply(expiredToken);
            if (fresh != null) {
                token = fresh;
            }
            return fresh;
        } finally {
            lock.unlock();
        }
    }
}
