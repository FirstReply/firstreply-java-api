package ai.firstreply.api;

import ai.firstreply.api.model.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class FirstReplyService {

    private static final String DEFAULT_BASE_URL = "https://api.firstreply.ai/";
    private static final String BASE_URL_ENV_VAR = "FIRSTREPLY_API_URL";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final Set<String> LOOPBACK_HOSTS = Set.of("localhost", "127.0.0.1", "::1", "[::1]");

    private final FirstReplyApi api;
    private final String organizationId;
    private final ExecutorService executorService;

    /**
     * Creates a FirstReplyService using the default or env-configured base URL.
     *
     * @param token          API token ("fr-XXXXXXX")
     * @param organizationId organization UUID
     */
    public FirstReplyService(final String token, String organizationId) {
        this(token, organizationId, resolveBaseUrl(), DEFAULT_TIMEOUT);
    }

    /**
     * Creates a FirstReplyService with a custom timeout.
     *
     * @param token          API token
     * @param organizationId organization UUID
     * @param timeout        HTTP read timeout; Duration.ZERO means no timeout
     */
    public FirstReplyService(final String token, final String organizationId, final Duration timeout) {
        this(token, organizationId, resolveBaseUrl(), timeout);
    }

    /**
     * Creates a FirstReplyService with a fully custom base URL and timeout.
     *
     * @param token          API token
     * @param organizationId organization UUID
     * @param baseUrl        base URL including trailing slash
     * @param timeout        HTTP read timeout
     */
    public FirstReplyService(final String token, final String organizationId, final String baseUrl, final Duration timeout) {
        this(new DefaultTokenStore(token, null), organizationId, baseUrl, timeout);
    }

    /**
     * Creates a FirstReplyService backed by a {@link TokenStore}, enabling automatic token refresh
     * on 401 when the store is configured with a refresh function (see {@link DefaultTokenStore}).
     */
    public FirstReplyService(final TokenStore tokenStore, final String organizationId, final String baseUrl, final Duration timeout) {
        String validatedBaseUrl = requireSecureBaseUrl(baseUrl);
        OkHttpClient client = defaultClient(tokenStore, timeout);
        Retrofit retrofit = defaultRetrofit(client, defaultObjectMapper(), validatedBaseUrl);

        this.organizationId = organizationId;
        this.api = retrofit.create(FirstReplyApi.class);
        this.executorService = client.dispatcher().executorService();
    }

    /**
     * Creates a FirstReplyService from a pre-built {@link FirstReplyApi} instance.
     */
    public FirstReplyService(final FirstReplyApi api, final String organizationId) {
        this.api = api;
        this.organizationId = organizationId;
        this.executorService = null;
    }

    /**
     * Creates a FirstReplyService from a pre-built {@link FirstReplyApi} and executor.
     * The executor must be the one obtained from the OkHttpClient used to build the api.
     */
    public FirstReplyService(final FirstReplyApi api, final String organizationId, final ExecutorService executorService) {
        this.api = api;
        this.organizationId = organizationId;
        this.executorService = executorService;
    }

    // ── Contacts ──────────────────────────────────────────────────────────

    public Contact getContact(String contactId) {
        return execute(api.getContact(this.organizationId, contactId));
    }

    public Contact updateContact(String contactId, ContactUpdate request) {
        return execute(api.updateContact(this.organizationId, contactId, request));
    }

    // ── Conversations ─────────────────────────────────────────────────────

    public Conversation createConversation(ConversationCreate request) {
        return execute(api.createConversation(this.organizationId, request));
    }

    public Conversation getConversation(String conversationId) {
        return execute(api.getConversation(this.organizationId, conversationId));
    }

    public Conversation updateConversation(String conversationId, ConversationUpdate request) {
        return execute(api.updateConversation(this.organizationId, conversationId, request));
    }

    public void deleteConversation(String conversationId) {
        execute(api.deleteConversation(this.organizationId, conversationId));
    }

    public ConversationList searchConversations(ConversationListParams params) {
        return execute(api.searchConversations(this.organizationId, params));
    }

    public void readConversation(String conversationId) {
        execute(api.readConversation(this.organizationId, conversationId));
    }

    public void closeConversation(String conversationId) {
        execute(api.closeConversation(this.organizationId, conversationId));
    }

    // ── Messages ──────────────────────────────────────────────────────────

    public Message getMessage(String messageId) {
        return execute(api.getMessage(this.organizationId, messageId));
    }

    public Message replyMessage(String messageId, MessageCreate request) {
        return execute(api.replyMessage(this.organizationId, messageId, request));
    }

    public Message updateMessage(String messageId, MessageUpdate request) {
        return execute(api.updateMessage(this.organizationId, messageId, request));
    }

    public void deleteMessage(String messageId) {
        execute(api.deleteMessage(this.organizationId, messageId));
    }

    public MessageList searchMessages(MessageListParams params) {
        return execute(api.searchMessages(this.organizationId, params));
    }

    // ── Organizations ─────────────────────────────────────────────────────

    public OrganizationList listOrganizations(String sort, String order, int page, int limit) {
        return execute(api.listOrganizations(sort, order, page, limit));
    }

    public Organization createOrganization(OrganizationCreate request) {
        return execute(api.createOrganization(request));
    }

    public Organization getOrganization(String organizationId) {
        return execute(api.getOrganization(organizationId));
    }

    public Organization updateOrganization(String organizationId, OrganizationUpdate request) {
        return execute(api.updateOrganization(organizationId, request));
    }

    public OrganizationList searchOrganizations(OrganizationListParams params) {
        return execute(api.searchOrganizations(params));
    }

    // ── API Keys ──────────────────────────────────────────────────────────

    public ApiKeyList listApiKeys(int page, int limit) {
        return execute(api.listApiKeys(this.organizationId, page, limit));
    }

    /**
     * Creates a new named API key. The {@code apiKey} field of the returned
     * {@link ApiKeySecret} is the only time the plaintext key is exposed —
     * store it immediately.
     */
    public ApiKeySecret createApiKey(ApiKeyCreate request) {
        return execute(api.createApiKey(this.organizationId, request));
    }

    public void deleteApiKey(String apiKeyId) {
        execute(api.deleteApiKey(this.organizationId, apiKeyId));
    }

    // ── Authentication ────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest request) {
        return execute(api.login(request));
    }

    public AuthResponse signup(SignupRequest request) {
        return execute(api.signup(request));
    }

    public TokenResponse refresh(RefreshRequest request) {
        return execute(api.refresh(request));
    }

    public void requestPasswordReset(String email) {
        execute(api.requestPasswordReset(new PasswordResetRequest(email)));
    }

    // ── Execution & Error Handling ────────────────────────────────────────

    /**
     * Executes a Retrofit {@link Single}, blocking until the result is available.
     * HTTP errors (non-2xx) are converted to {@link FirstReplyException} with the status code.
     */
    public static <T> T execute(Single<T> apiCall) {
        try {
            return apiCall.blockingGet();
        } catch (HttpException e) {
            String body = null;
            try {
                if (e.response() != null && e.response().errorBody() != null) {
                    body = e.response().errorBody().string();
                }
            } catch (Exception ignored) {
            }
            throw new FirstReplyException(e.code(), body != null ? body : e.message());
        } catch (Exception e) {
            if (e.getCause() instanceof HttpException) {
                HttpException he = (HttpException) e.getCause();
                throw new FirstReplyException(he.code(), he.message());
            }
            throw new FirstReplyException(0, e.getMessage());
        }
    }

    /**
     * Shuts down the OkHttp ExecutorService immediately.
     * By default OkHttp shuts down after 60 s of inactivity; call this to shut down at once.
     */
    public void shutdownExecutor() {
        Objects.requireNonNull(this.executorService, "executorService must be set in order to shut down");
        this.executorService.shutdown();
    }

    // ── Builder Helpers ───────────────────────────────────────────────────

    public static FirstReplyApi buildApi(String token, Duration timeout) {
        return buildApi(token, resolveBaseUrl(), timeout);
    }

    public static FirstReplyApi buildApi(String token, String baseUrl, Duration timeout) {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(token, timeout);
        return defaultRetrofit(client, mapper, requireSecureBaseUrl(baseUrl)).create(FirstReplyApi.class);
    }

    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    public static OkHttpClient defaultClient(String token, Duration timeout) {
        return defaultClient(new DefaultTokenStore(token, null), timeout);
    }

    /**
     * Builds the default OkHttp client with auth, retry, and (when the store supports it)
     * automatic token refresh on 401. The retry interceptor uses safe defaults
     * (3 attempts, exponential backoff up to 30s) and only retries idempotent methods.
     */
    public static OkHttpClient defaultClient(TokenStore tokenStore, Duration timeout) {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(tokenStore))
                .addInterceptor(new RetryInterceptor())
                .authenticator(new RefreshAuthenticator(tokenStore))
                .connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
                .readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    public static Retrofit defaultRetrofit(OkHttpClient client, ObjectMapper mapper) {
        return defaultRetrofit(client, mapper, resolveBaseUrl());
    }

    public static Retrofit defaultRetrofit(OkHttpClient client, ObjectMapper mapper, String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(requireSecureBaseUrl(baseUrl))
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    /**
     * Returns the base URL to use: checks the {@code FIRSTREPLY_API_URL} environment variable
     * first, falling back to the default production URL. Validates HTTPS.
     */
    public static String resolveBaseUrl() {
        String envUrl = System.getenv(BASE_URL_ENV_VAR);
        return requireSecureBaseUrl((envUrl != null && !envUrl.isEmpty()) ? envUrl : DEFAULT_BASE_URL);
    }

    /**
     * Enforces HTTPS for the API base URL. {@code http://} is permitted only for loopback hosts
     * (localhost, 127.0.0.1, ::1) so local development against a proxy still works. Any other
     * non-HTTPS scheme is rejected to prevent token interception over plaintext.
     *
     * @throws IllegalArgumentException if the URL is null, empty, malformed, or not HTTPS
     */
    static String requireSecureBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalArgumentException("baseUrl must not be null or empty");
        }
        URI uri;
        try {
            uri = new URI(baseUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("baseUrl is not a valid URI: " + baseUrl, e);
        }
        String scheme = uri.getScheme();
        if (scheme == null) {
            throw new IllegalArgumentException("baseUrl must include a scheme (https://): " + baseUrl);
        }
        if ("https".equalsIgnoreCase(scheme)) {
            return baseUrl;
        }
        if ("http".equalsIgnoreCase(scheme) && uri.getHost() != null && LOOPBACK_HOSTS.contains(uri.getHost().toLowerCase())) {
            return baseUrl;
        }
        throw new IllegalArgumentException(
                "baseUrl must use HTTPS (http:// is only allowed for loopback hosts): " + baseUrl);
    }
}
