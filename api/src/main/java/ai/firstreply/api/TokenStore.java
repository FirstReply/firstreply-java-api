package ai.firstreply.api;

/**
 * Holds the access token used to authenticate requests and supplies a refreshed token on demand.
 *
 * <p>Implementations must be thread-safe: {@link #getToken()} is read on every request, and
 * {@link #refresh(String)} can be invoked concurrently by OkHttp's {@code Authenticator} when a
 * 401 is observed. Implementations are responsible for performing only one network refresh at a
 * time (single-flight) so that a burst of concurrent failed requests does not produce a burst of
 * refresh calls. {@link DefaultTokenStore} provides a reference implementation.
 */
public interface TokenStore {

    /**
     * @return the current access token, or {@code null} if no token has been set
     */
    String getToken();

    /**
     * Refreshes the access token. Called by {@link RefreshAuthenticator} when a 401 response is
     * received. Implementations should:
     * <ul>
     *   <li>Skip the refresh and return the current token if {@code expiredToken} no longer
     *       matches the stored token (another thread already refreshed it).</li>
     *   <li>Block concurrent callers behind a single in-flight refresh.</li>
     *   <li>Return {@code null} if refresh failed (the caller will then surface 401 to the user).</li>
     * </ul>
     *
     * @param expiredToken the token that was rejected with 401, or {@code null} if unknown
     * @return the new access token, or {@code null} if refresh is not possible
     */
    String refresh(String expiredToken);
}
