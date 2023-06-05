package ai.firstreply.api;

import ai.firstreply.api.model.Contact;
import ai.firstreply.api.model.ContactUpdate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.reactivex.Single;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class FirstReplyService {

    private static final String BASE_URL = "https://api.firstreply.ai/";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final ObjectMapper mapper = defaultObjectMapper();

    private final FirstReplyApi api;
    private final String organizationId;
    private final ExecutorService executorService;

    /**
     * Creates a new FirstReplyService that wraps FirstReplyApi
     *
     * @param token FirstReply token string "fr-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     */
    public FirstReplyService(final String token, String organizationId) {
        this(token, organizationId, DEFAULT_TIMEOUT);
    }

    /**
     * Creates a new FirstReplyService that wraps FirstReplyApi
     *
     * @param token   FirstReply token string "fr-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
     * @param timeout http read timeout, Duration.ZERO means no timeout
     */
    public FirstReplyService(final String token, final String organizationId, final Duration timeout) {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(token, timeout);
        Retrofit retrofit = defaultRetrofit(client, mapper);

        this.organizationId = organizationId;
        this.api = retrofit.create(FirstReplyApi.class);
        this.executorService = client.dispatcher().executorService();
    }

    /**
     * Creates a new FirstReplyService that wraps FirstReplyApi.
     * Use this if you need more customization, but use FirstReplyService(api, executorService) if you use streaming and
     * want to shut down instantly
     *
     * @param api FirstReplyApi instance to use for all methods
     */
    public FirstReplyService(final FirstReplyApi api, final String organizationId) {
        this.api = api;
        this.organizationId = organizationId;
        this.executorService = null;
    }

    /**
     * Creates a new FirstReplyService that wraps FirstReplyApi.
     * The ExecutorService must be the one you get from the client you created the api with
     * otherwise shutdownExecutor() won't work.
     * <p>
     * Use this if you need more customization.
     *
     * @param api             FirstReplyApi instance to use for all methods
     * @param executorService the ExecutorService from client.dispatcher().executorService()
     */
    public FirstReplyService(final FirstReplyApi api, final String organizationId, final ExecutorService executorService) {
        this.api = api;
        this.organizationId = organizationId;
        this.executorService = executorService;
    }

    public Contact getContact(String contactId) {
        return execute(api.getContact(this.organizationId, contactId));
    }

    public Contact updateContact(String contactId, ContactUpdate request) {
        return execute(api.updateContact(this.organizationId, contactId, request));
    }

    /**
     * Calls the FirstReply api, returns the response, and parses error messages if the request fails
     */
    public static <T> T execute(Single<T> apiCall) {
        return apiCall.blockingGet();
    }

    /**
     * Shuts down the OkHttp ExecutorService.
     * The default behaviour of OkHttp's ExecutorService (ConnectionPool)
     * is to shut down after an idle timeout of 60s.
     * Call this method to shut down the ExecutorService immediately.
     */
    public void shutdownExecutor() {
        Objects.requireNonNull(this.executorService, "executorService must be set in order to shut down");
        this.executorService.shutdown();
    }

    public static FirstReplyApi buildApi(String token, Duration timeout) {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(token, timeout);
        Retrofit retrofit = defaultRetrofit(client, mapper);

        return retrofit.create(FirstReplyApi.class);
    }

    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper;
    }

    public static OkHttpClient defaultClient(String token, Duration timeout) {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(token))
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                .readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    public static Retrofit defaultRetrofit(OkHttpClient client, ObjectMapper mapper) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

}
