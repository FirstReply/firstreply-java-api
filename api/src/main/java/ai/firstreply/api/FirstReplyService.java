package ai.firstreply.api;

import ai.firstreply.api.model.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Completable;
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
import java.util.List;
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

    public ContactList searchContacts(ContactListParams params) {
        return execute(api.searchContacts(this.organizationId, params));
    }

    public Contact createContact(ContactCreate request) {
        return execute(api.createContact(this.organizationId, request));
    }

    public Contact getContact(String contactId) {
        return execute(api.getContact(this.organizationId, contactId));
    }

    public Contact updateContact(String contactId, ContactUpdate request) {
        return execute(api.updateContact(this.organizationId, contactId, request));
    }

    public void deleteContact(String contactId) {
        execute(api.deleteContact(this.organizationId, contactId));
    }

    public ContactFieldList listContactFields(String sort, String order, int page, int limit) {
        return execute(api.listContactFields(this.organizationId, sort, order, page, limit));
    }

    public ContactField getContactField(String contactFieldId) {
        return execute(api.getContactField(this.organizationId, contactFieldId));
    }

    // ── Conversations ─────────────────────────────────────────────────────

    public Conversation createConversation(ConversationCreate request) {
        return execute(api.createConversation(this.organizationId, request));
    }

    public Conversation getConversation(String conversationId) {
        return execute(api.getConversation(this.organizationId, conversationId));
    }

    /**
     * Returns the conversation together with all of its messages in one call.
     *
     * @param markRead also mark the conversation as read
     */
    public ConversationThread getConversationThread(String conversationId, boolean markRead) {
        return execute(api.getConversationThread(this.organizationId, conversationId, markRead));
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

    public void markConversationAsSpam(String conversationId) {
        execute(api.markConversationAsSpam(this.organizationId, conversationId));
    }

    public void markConversationAsNotSpam(String conversationId) {
        execute(api.markConversationAsNotSpam(this.organizationId, conversationId));
    }

    public ConversationTotals getConversationTotals() {
        return execute(api.getConversationTotals(this.organizationId));
    }

    /** Opened and closed conversations per day; dates are {@code yyyy-MM-dd}. */
    public ConversationStatistics getConversationStatistics(ConversationStatisticsParams params) {
        return execute(api.getConversationStatistics(this.organizationId, params));
    }

    public MemberConversationStatistics getMemberConversationStatistics(ConversationStatisticsParams params) {
        return execute(api.getMemberConversationStatistics(this.organizationId, params));
    }

    /** First reply times, closing rate, AI reply share, sentiment and close reasons for a date range. */
    public MetricsStatistics getMetricsStatistics(ConversationStatisticsParams params) {
        return execute(api.getMetricsStatistics(this.organizationId, params));
    }

    /**
     * Hides, unhides, deletes or likes (Facebook only) a comment of a comment thread on the
     * platform. Instagram has no like; LinkedIn comments cannot be moderated.
     *
     * @param messageId the comment's message
     * @return the message with its new comment state
     */
    public Message moderateComment(String conversationId, String messageId, CommentModerationRequest request) {
        return execute(api.moderateComment(this.organizationId, conversationId, messageId, request));
    }

    /**
     * Answers a comment of a Facebook or Instagram comment thread with a private message to its
     * author, once per comment and within 7 days of it. The message is stored in the person's
     * private conversation of the inbox.
     *
     * @return the comment
     */
    public Message replyToCommentPrivately(String conversationId, PrivateReplyRequest request) {
        return execute(api.replyToCommentPrivately(this.organizationId, conversationId, request));
    }

    // ── Messages ──────────────────────────────────────────────────────────

    public Message getMessage(String messageId) {
        return execute(api.getMessage(this.organizationId, messageId));
    }

    /**
     * Replies to a message. Set {@code draft} on the request to store the reply as a draft
     * (send it later with {@link #sendMessage(String)}), or {@code internal} for an internal note.
     */
    public Message replyMessage(String messageId, MessageCreate request) {
        return execute(api.replyMessage(this.organizationId, messageId, request));
    }

    /** Updates a draft or an internal note; fields left {@code null} keep their value. */
    public Message updateMessage(String messageId, MessageUpdate request) {
        return execute(api.updateMessage(this.organizationId, messageId, request));
    }

    /** Sends a draft to the customer through the conversation's inbox. */
    public Message sendMessage(String messageId) {
        return execute(api.sendMessage(this.organizationId, messageId));
    }

    public void deleteMessage(String messageId) {
        execute(api.deleteMessage(this.organizationId, messageId));
    }

    public MessageList searchMessages(MessageListParams params) {
        return execute(api.searchMessages(this.organizationId, params));
    }

    // ── Organizations ─────────────────────────────────────────────────────

    /**
     * Lists the organizations of the authenticated user. With an organization API key the
     * list holds only the key's own organization.
     */
    public OrganizationList listOrganizations(String sort, String order, int page, int limit) {
        return execute(api.listOrganizations(sort, order, page, limit));
    }

    /** Needs a user token: organization API keys are refused with 403. */
    public Organization createOrganization(OrganizationCreate request) {
        return execute(api.createOrganization(request));
    }

    public Organization getOrganization(String organizationId) {
        return execute(api.getOrganization(organizationId));
    }

    public Organization updateOrganization(String organizationId, OrganizationUpdate request) {
        return execute(api.updateOrganization(organizationId, request));
    }

    /** Plan, inbox and member limits of an organization and how much of them is used. */
    public OrganizationUsage getOrganizationUsage(String organizationId) {
        return execute(api.getOrganizationUsage(organizationId));
    }

    /** Needs a user token: organization API keys are refused with 403. */
    public OrganizationList searchOrganizations(OrganizationListParams params) {
        return execute(api.searchOrganizations(params));
    }

    // ── Members ───────────────────────────────────────────────────────────

    public OrganizationMemberList listMembers(String sort, String order, int page, int limit) {
        return execute(api.listMembers(this.organizationId, sort, order, page, limit));
    }

    public OrganizationMember getMember(String memberId) {
        return execute(api.getMember(this.organizationId, memberId));
    }

    /** Invites a member by email. Requires organization admin permission. */
    public OrganizationMember createMember(OrganizationMemberCreate request) {
        return execute(api.createMember(this.organizationId, request));
    }

    /** Fields left {@code null} keep their value. Requires organization admin permission. */
    public OrganizationMember updateMember(String memberId, OrganizationMemberUpdate request) {
        return execute(api.updateMember(this.organizationId, memberId, request));
    }

    public void deleteMember(String memberId) {
        execute(api.deleteMember(this.organizationId, memberId));
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

    // ── Inboxes ───────────────────────────────────────────────────────────

    public InboxList listInboxes(String sort, String order, int page, int limit) {
        return execute(api.listInboxes(this.organizationId, sort, order, page, limit));
    }

    public Inbox getInbox(String inboxId) {
        return execute(api.getInbox(this.organizationId, inboxId));
    }

    /** Conversation counts per inbox. */
    public InboxStatistics getInboxStatistics() {
        return execute(api.getInboxStatistics(this.organizationId));
    }

    /** Fetches new messages of the inbox now instead of waiting for the next scheduled run. */
    public void refreshInbox(String inboxId) {
        execute(api.refreshInbox(this.organizationId, inboxId));
    }

    /** Tests the stored connection settings of the inbox against its platform. */
    public ConnectionTestResult testInbox(String inboxId) {
        return execute(api.testInbox(this.organizationId, inboxId));
    }

    // ── Classifications ───────────────────────────────────────────────────

    public ClassificationList listClassifications(String sort, String order, int page, int limit) {
        return execute(api.listClassifications(this.organizationId, sort, order, page, limit));
    }

    public Classification getClassification(String classificationId) {
        return execute(api.getClassification(this.organizationId, classificationId));
    }

    public Classification createClassification(ClassificationCreate request) {
        return execute(api.createClassification(this.organizationId, request));
    }

    /**
     * Replaces the classification's settings. The flags and the color are plain values, so
     * leaving them unset turns them off: start from {@link #getClassification(String)}.
     */
    public Classification updateClassification(String classificationId, ClassificationUpdate request) {
        return execute(api.updateClassification(this.organizationId, classificationId, request));
    }

    /**
     * Deletes a classification.
     *
     * @param reassignTo classification that takes over its conversations and knowledge, or
     *                   {@code null} to leave them unclassified
     */
    public void deleteClassification(String classificationId, String reassignTo) {
        execute(api.deleteClassification(this.organizationId, classificationId, reassignTo));
    }

    /** How many conversations and knowledge entries use the classification. */
    public ClassificationImpact getClassificationImpact(String classificationId) {
        return execute(api.getClassificationImpact(this.organizationId, classificationId));
    }

    public ClassificationStatistics getClassificationStatistics() {
        return execute(api.getClassificationStatistics(this.organizationId));
    }

    // ── Knowledge ─────────────────────────────────────────────────────────

    public KnowledgeList searchKnowledge(KnowledgeListParams params) {
        return execute(api.searchKnowledge(this.organizationId, params));
    }

    public KnowledgeEntry getKnowledge(String knowledgeId) {
        return execute(api.getKnowledge(this.organizationId, knowledgeId));
    }

    public KnowledgeEntry createKnowledge(KnowledgeEntryCreate request) {
        return execute(api.createKnowledge(this.organizationId, request));
    }

    /**
     * Replaces the knowledge entry. {@code positive} is a plain value, so leaving it unset
     * makes the entry negative: start from {@link #getKnowledge(String)}.
     */
    public KnowledgeEntry updateKnowledge(String knowledgeId, KnowledgeEntryUpdate request) {
        return execute(api.updateKnowledge(this.organizationId, knowledgeId, request));
    }

    public void deleteKnowledge(String knowledgeId) {
        execute(api.deleteKnowledge(this.organizationId, knowledgeId));
    }

    public KnowledgeSourceList listKnowledgeSources(String sort, String order, int page, int limit) {
        return execute(api.listKnowledgeSources(this.organizationId, sort, order, page, limit));
    }

    public KnowledgeSource getKnowledgeSource(String knowledgeSourceId) {
        return execute(api.getKnowledgeSource(this.organizationId, knowledgeSourceId));
    }

    /** Starts a sync of the knowledge source; poll {@link #getKnowledgeSourceStatus(String)}. */
    public void syncKnowledgeSource(String knowledgeSourceId) {
        execute(api.syncKnowledgeSource(this.organizationId, knowledgeSourceId));
    }

    public KnowledgeSourceSyncStatus getKnowledgeSourceStatus(String knowledgeSourceId) {
        return execute(api.getKnowledgeSourceStatus(this.organizationId, knowledgeSourceId));
    }

    // ── AI ────────────────────────────────────────────────────────────────

    /** Generates a reply to the latest customer message of the conversation, without sending it. */
    public AiResult draftReply(String conversationId) {
        return execute(api.draftReply(this.organizationId, conversationId));
    }

    /** Runs a free prompt with the organization's knowledge (unless {@code removeKnowledge}). */
    public AiResult prompt(AiRequest request) {
        return execute(api.prompt(this.organizationId, request));
    }

    /**
     * Translates text. When the text already is in the target language it comes back
     * unchanged with {@code translated == false}; an empty translation means it failed.
     */
    public TranslateResult translate(TranslateRequest request) {
        return execute(api.translate(this.organizationId, request));
    }

    /** Classifies a sample customer message and drafts the reply the AI would send. */
    public TestResult testAi(TestRequest request) {
        return execute(api.testAi(this.organizationId, request));
    }

    // ── Search ────────────────────────────────────────────────────────────

    /**
     * Searches conversations, contacts, knowledge and messages at once.
     *
     * @param query search text, at least 2 characters; supports the search operators
     *              ({@code "phrase"}, {@code -word}, {@code from:}, {@code subject:}, ...)
     * @param limit results per type, or {@code null} for the default
     */
    public SearchResult search(String query, Integer limit) {
        return execute(api.search(this.organizationId, query, limit));
    }

    // ── Templates ─────────────────────────────────────────────────────────

    public TemplateList searchTemplates(TemplateListParams params) {
        return execute(api.searchTemplates(this.organizationId, params));
    }

    public Template getTemplate(String templateId) {
        return execute(api.getTemplate(this.organizationId, templateId));
    }

    public Template createTemplate(TemplateCreate request) {
        return execute(api.createTemplate(this.organizationId, request));
    }

    public Template updateTemplate(String templateId, TemplateUpdate request) {
        return execute(api.updateTemplate(this.organizationId, templateId, request));
    }

    public void deleteTemplate(String templateId) {
        execute(api.deleteTemplate(this.organizationId, templateId));
    }

    // ── Prompts ───────────────────────────────────────────────────────────

    public PromptList searchPrompts(ListParams params) {
        return execute(api.searchPrompts(this.organizationId, params));
    }

    public Prompt getPrompt(String promptId) {
        return execute(api.getPrompt(this.organizationId, promptId));
    }

    public Prompt createPrompt(PromptCreate request) {
        return execute(api.createPrompt(this.organizationId, request));
    }

    public Prompt updatePrompt(String promptId, PromptUpdate request) {
        return execute(api.updatePrompt(this.organizationId, promptId, request));
    }

    public void deletePrompt(String promptId) {
        execute(api.deletePrompt(this.organizationId, promptId));
    }

    // ── Webhooks ──────────────────────────────────────────────────────────

    public WebhookList searchWebhooks(WebhookListParams params) {
        return execute(api.searchWebhooks(this.organizationId, params));
    }

    public Webhook getWebhook(String webhookId) {
        return execute(api.getWebhook(this.organizationId, webhookId));
    }

    /**
     * Creates a webhook. {@code event} is one of the {@link WebhookEventType} names, or several
     * separated by commas.
     */
    public Webhook createWebhook(WebhookCreate request) {
        return execute(api.createWebhook(this.organizationId, request));
    }

    public Webhook updateWebhook(String webhookId, WebhookUpdate request) {
        return execute(api.updateWebhook(this.organizationId, webhookId, request));
    }

    public void deleteWebhook(String webhookId) {
        execute(api.deleteWebhook(this.organizationId, webhookId));
    }

    /** Delivers a {@link WebhookEventType#WEBHOOK_TEST} event to the webhook's target. */
    public WebhookTestResult testWebhook(String webhookId) {
        return execute(api.testWebhook(this.organizationId, webhookId));
    }

    /** Creates a new signing secret for the webhook's deliveries; the old one stops working. */
    public WebhookSecret rotateWebhookSecret(String webhookId) {
        return execute(api.rotateWebhookSecret(this.organizationId, webhookId));
    }

    // ── Saved conversation filters ────────────────────────────────────────

    public ConversationFilterList listFilters(String sort, String order, int page, int limit) {
        return execute(api.listFilters(this.organizationId, sort, order, page, limit));
    }

    public ConversationFilter getFilter(String filterId) {
        return execute(api.getFilter(this.organizationId, filterId));
    }

    public ConversationFilter createFilter(ConversationFilterCreate request) {
        return execute(api.createFilter(this.organizationId, request));
    }

    /**
     * Replaces the saved filter. {@code color} and {@code arrangement} are plain values, so
     * leaving them unset resets them to 0: start from {@link #getFilter(String)}.
     */
    public ConversationFilter updateFilter(String filterId, ConversationFilterUpdate request) {
        return execute(api.updateFilter(this.organizationId, filterId, request));
    }

    public void deleteFilter(String filterId) {
        execute(api.deleteFilter(this.organizationId, filterId));
    }

    // ── Social posts ──────────────────────────────────────────────────────

    /** Lists posts, newest first ({@code order = "asc"} for calendar order). */
    public SocialPostList listPosts(SocialPostListParams params) {
        return execute(api.listPosts(this.organizationId, params.getStatus(), params.getInboxId(), params.getFrom(),
                params.getTo(), params.getSearch(), params.getOrder(), params.getPage(), params.getLimit()));
    }

    /**
     * Creates a draft, or a scheduled post with {@code status = "SCHEDULED"} and
     * {@code scheduledAt}. When the organization requires approval, a member's scheduled post
     * comes back as PENDING_APPROVAL and waits for a manager.
     */
    public SocialPost createPost(SocialPostCreate request) {
        return execute(api.createPost(this.organizationId, request));
    }

    /** The post with its targets, their status and latest metrics, and its media. */
    public SocialPost getPost(String postId) {
        return execute(api.getPost(this.organizationId, postId));
    }

    /**
     * Edits or reschedules a post until publishing starts. Replaces text, time, targets and
     * media: start from {@link #getPost(String)}.
     */
    public SocialPost updatePost(String postId, SocialPostUpdate request) {
        return execute(api.updatePost(this.organizationId, postId, request));
    }

    /**
     * Deletes a draft or cancels a scheduled post.
     *
     * @param deleteOnPlatform also delete the published copies on the platforms first; when one
     *                         of them fails, nothing is deleted
     */
    public void deletePost(String postId, boolean deleteOnPlatform) {
        execute(api.deletePost(this.organizationId, postId, deleteOnPlatform));
    }

    /** Publishes right away. With approval required, a member's post goes to the managers instead. */
    public SocialPost publishPost(String postId) {
        return execute(api.publishPost(this.organizationId, postId));
    }

    /**
     * Approves a post waiting for approval (manager or above). It is published at its time, or
     * now when that has passed.
     */
    public SocialPost approvePost(String postId) {
        return execute(api.approvePost(this.organizationId, postId));
    }

    /** Publishes a failed target (one channel of the post) again. */
    public SocialPost retryPostTarget(String postId, String targetId) {
        return execute(api.retryPostTarget(this.organizationId, postId, targetId));
    }

    /** Metric snapshots of every target of the post, oldest first. */
    public List<SocialPostMetric> getPostMetrics(String postId) {
        return execute(api.getPostMetrics(this.organizationId, postId));
    }

    /**
     * The comment threads on the post, newest first, as conversations (members see the ones
     * they may see in the inbox).
     */
    public ConversationList listPostConversations(String postId, int page, int limit) {
        return execute(api.listPostConversations(this.organizationId, postId, page, limit));
    }

    /**
     * Writes a post from an instruction with the knowledge base ({@code action = "WRITE"}), or
     * shortens or rewrites a text ({@code SHORTEN}, {@code REWRITE}), for a platform and within
     * {@code maxLength}. Nothing is saved; {@code tooLong} when the text still exceeds the limit.
     */
    public SocialPostWriteResult writePost(SocialPostWriteRequest request) {
        return execute(api.writePost(this.organizationId, request));
    }

    /**
     * Totals, a daily series, top posts and channels for the posts published in a period.
     *
     * @param from    epoch millis, or {@code null} for 30 days before {@code to}
     * @param to      epoch millis, or {@code null} for now; the period is at most one year
     * @param inboxId only this channel, or {@code null} for all
     */
    public SocialAnalytics getPostAnalytics(Long from, Long to, String inboxId) {
        return execute(api.getPostAnalytics(this.organizationId, from, to, inboxId));
    }

    /**
     * Registers a media file and returns a signed URL to send it to. Upload the file there,
     * then call {@link #completePostMediaUpload(String)}.
     */
    public SocialPostMediaUpload startPostMediaUpload(SocialPostMediaUploadRequest request) {
        return execute(api.startPostMediaUpload(this.organizationId, request));
    }

    /** Checks the uploaded file and makes it usable in posts ({@link SocialPostMediaInput}). */
    public SocialPostMedia completePostMediaUpload(String mediaId) {
        return execute(api.completePostMediaUpload(this.organizationId, mediaId));
    }

    // ── Authentication ────────────────────────────────────────────────────

    /**
     * Logs a user in. When the account has two-factor authentication enabled the response
     * carries {@code mfaRequired} and an {@code mfaToken} instead of session tokens.
     */
    public AuthResponse login(LoginRequest request) {
        return execute(api.login(request));
    }

    public AuthResponse signup(SignupRequest request) {
        return execute(api.signup(request));
    }

    /**
     * Exchanges a refresh token for a new access token. Refresh tokens are rotated: the
     * presented one is revoked, keep the {@code refreshToken} of the response for the next call.
     */
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
        } catch (Exception e) {
            throw toFirstReplyException(e);
        }
    }

    /**
     * Executes a Retrofit {@link Completable} (a call without response body), blocking until it
     * finished. HTTP errors (non-2xx) are converted to {@link FirstReplyException}.
     */
    public static void execute(Completable apiCall) {
        try {
            apiCall.blockingAwait();
        } catch (Exception e) {
            throw toFirstReplyException(e);
        }
    }

    private static FirstReplyException toFirstReplyException(Exception e) {
        if (e instanceof HttpException) {
            HttpException he = (HttpException) e;
            String body = null;
            try {
                if (he.response() != null && he.response().errorBody() != null) {
                    body = he.response().errorBody().string();
                }
            } catch (Exception ignored) {
            }
            return new FirstReplyException(he.code(), body != null ? body : he.message());
        }
        if (e.getCause() instanceof HttpException) {
            HttpException he = (HttpException) e.getCause();
            return new FirstReplyException(he.code(), he.message());
        }
        return new FirstReplyException(0, e.getMessage());
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
