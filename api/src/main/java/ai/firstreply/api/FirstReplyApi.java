package ai.firstreply.api;

import ai.firstreply.api.model.*;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

import java.util.List;

public interface FirstReplyApi {

    // ── Contacts ──────────────────────────────────────────────────────────

    @POST("/v1/organizations/{organizationId}/contacts/search")
    Single<ContactList> searchContacts(
            @Path("organizationId") String organizationId,
            @Body ContactListParams params);

    @POST("/v1/organizations/{organizationId}/contacts")
    Single<Contact> createContact(
            @Path("organizationId") String organizationId,
            @Body ContactCreate request);

    @GET("/v1/organizations/{organizationId}/contacts/{contactId}")
    Single<Contact> getContact(
            @Path("organizationId") String organizationId,
            @Path("contactId") String contactId);

    @PUT("/v1/organizations/{organizationId}/contacts/{contactId}")
    Single<Contact> updateContact(
            @Path("organizationId") String organizationId,
            @Path("contactId") String contactId,
            @Body ContactUpdate request);

    @DELETE("/v1/organizations/{organizationId}/contacts/{contactId}")
    Completable deleteContact(
            @Path("organizationId") String organizationId,
            @Path("contactId") String contactId);

    @GET("/v1/organizations/{organizationId}/contactfields")
    Single<ContactFieldList> listContactFields(
            @Path("organizationId") String organizationId,
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("page") int page,
            @Query("limit") int limit);

    @GET("/v1/organizations/{organizationId}/contactfields/{id}")
    Single<ContactField> getContactField(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    // ── Conversations ─────────────────────────────────────────────────────

    @POST("/v1/organizations/{organizationId}/conversations")
    Single<Conversation> createConversation(
            @Path("organizationId") String organizationId,
            @Body ConversationCreate request);

    @GET("/v1/organizations/{organizationId}/conversations/{id}")
    Single<Conversation> getConversation(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @GET("/v1/organizations/{organizationId}/conversations/{id}/thread")
    Single<ConversationThread> getConversationThread(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Query("markRead") boolean markRead);

    @PUT("/v1/organizations/{organizationId}/conversations/{id}")
    Single<Conversation> updateConversation(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body ConversationUpdate request);

    @DELETE("/v1/organizations/{organizationId}/conversations/{id}")
    Completable deleteConversation(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/conversations/search")
    Single<ConversationList> searchConversations(
            @Path("organizationId") String organizationId,
            @Body ConversationListParams params);

    @PUT("/v1/organizations/{organizationId}/conversations/{id}/read")
    Completable readConversation(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @PUT("/v1/organizations/{organizationId}/conversations/{id}/close")
    Completable closeConversation(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @PUT("/v1/organizations/{organizationId}/conversations/{id}/spam")
    Completable markConversationAsSpam(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @PUT("/v1/organizations/{organizationId}/conversations/{id}/not-spam")
    Completable markConversationAsNotSpam(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @GET("/v1/organizations/{organizationId}/conversations/totals")
    Single<ConversationTotals> getConversationTotals(
            @Path("organizationId") String organizationId);

    @POST("/v1/organizations/{organizationId}/conversations/statistic")
    Single<ConversationStatistics> getConversationStatistics(
            @Path("organizationId") String organizationId,
            @Body ConversationStatisticsParams params);

    @POST("/v1/organizations/{organizationId}/conversations/statistic/by-member")
    Single<MemberConversationStatistics> getMemberConversationStatistics(
            @Path("organizationId") String organizationId,
            @Body ConversationStatisticsParams params);

    @POST("/v1/organizations/{organizationId}/metrics/statistic")
    Single<MetricsStatistics> getMetricsStatistics(
            @Path("organizationId") String organizationId,
            @Body ConversationStatisticsParams params);

    // ── Messages ──────────────────────────────────────────────────────────

    @GET("/v1/organizations/{organizationId}/messages/{id}")
    Single<Message> getMessage(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/messages/{id}/reply")
    Single<Message> replyMessage(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body MessageCreate request);

    @PUT("/v1/organizations/{organizationId}/messages/{id}")
    Single<Message> updateMessage(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body MessageUpdate request);

    @POST("/v1/organizations/{organizationId}/messages/{id}/send")
    Single<Message> sendMessage(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @DELETE("/v1/organizations/{organizationId}/messages/{id}")
    Completable deleteMessage(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/messages/search")
    Single<MessageList> searchMessages(
            @Path("organizationId") String organizationId,
            @Body MessageListParams params);

    // ── Organizations ─────────────────────────────────────────────────────

    @GET("/v1/organizations")
    Single<OrganizationList> listOrganizations(
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("page") int page,
            @Query("limit") int limit);

    @POST("/v1/organizations")
    Single<Organization> createOrganization(
            @Body OrganizationCreate request);

    @GET("/v1/organizations/{id}")
    Single<Organization> getOrganization(
            @Path("id") String id);

    @PUT("/v1/organizations/{id}")
    Single<Organization> updateOrganization(
            @Path("id") String id,
            @Body OrganizationUpdate request);

    @GET("/v1/organizations/{id}/usage")
    Single<OrganizationUsage> getOrganizationUsage(
            @Path("id") String id);

    @POST("/v1/organizations/search")
    Single<OrganizationList> searchOrganizations(
            @Body OrganizationListParams params);

    // ── Members ───────────────────────────────────────────────────────────

    @GET("/v1/organizations/{organizationId}/members")
    Single<OrganizationMemberList> listMembers(
            @Path("organizationId") String organizationId,
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("page") int page,
            @Query("limit") int limit);

    @GET("/v1/organizations/{organizationId}/members/{id}")
    Single<OrganizationMember> getMember(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/members")
    Single<OrganizationMember> createMember(
            @Path("organizationId") String organizationId,
            @Body OrganizationMemberCreate request);

    @PUT("/v1/organizations/{organizationId}/members/{id}")
    Single<OrganizationMember> updateMember(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body OrganizationMemberUpdate request);

    @DELETE("/v1/organizations/{organizationId}/members/{id}")
    Completable deleteMember(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    // ── API Keys ──────────────────────────────────────────────────────────

    @GET("/v1/organizations/{organizationId}/apiKeys")
    Single<ApiKeyList> listApiKeys(
            @Path("organizationId") String organizationId,
            @Query("page") int page,
            @Query("limit") int limit);

    @POST("/v1/organizations/{organizationId}/apiKeys")
    Single<ApiKeySecret> createApiKey(
            @Path("organizationId") String organizationId,
            @Body ApiKeyCreate request);

    @DELETE("/v1/organizations/{organizationId}/apiKeys/{id}")
    Completable deleteApiKey(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    // ── Inboxes ───────────────────────────────────────────────────────────

    @GET("/v1/organizations/{organizationId}/inboxes")
    Single<InboxList> listInboxes(
            @Path("organizationId") String organizationId,
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("page") int page,
            @Query("limit") int limit);

    @GET("/v1/organizations/{organizationId}/inboxes/{id}")
    Single<Inbox> getInbox(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @GET("/v1/organizations/{organizationId}/inboxes/statistics")
    Single<InboxStatistics> getInboxStatistics(
            @Path("organizationId") String organizationId);

    @POST("/v1/organizations/{organizationId}/inboxes/{id}/refresh")
    Completable refreshInbox(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/inboxes/{id}/test")
    Single<ConnectionTestResult> testInbox(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    // ── Classifications ───────────────────────────────────────────────────

    @GET("/v1/organizations/{organizationId}/classifications")
    Single<ClassificationList> listClassifications(
            @Path("organizationId") String organizationId,
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("page") int page,
            @Query("limit") int limit);

    @GET("/v1/organizations/{organizationId}/classifications/{id}")
    Single<Classification> getClassification(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/classifications")
    Single<Classification> createClassification(
            @Path("organizationId") String organizationId,
            @Body ClassificationCreate request);

    @PUT("/v1/organizations/{organizationId}/classifications/{id}")
    Single<Classification> updateClassification(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body ClassificationUpdate request);

    @DELETE("/v1/organizations/{organizationId}/classifications/{id}")
    Completable deleteClassification(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Query("reassignTo") String reassignTo);

    @GET("/v1/organizations/{organizationId}/classifications/{id}/impact")
    Single<ClassificationImpact> getClassificationImpact(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @GET("/v1/organizations/{organizationId}/classifications/statistics")
    Single<ClassificationStatistics> getClassificationStatistics(
            @Path("organizationId") String organizationId);

    // ── Knowledge ─────────────────────────────────────────────────────────

    @POST("/v1/organizations/{organizationId}/knowledge/search")
    Single<KnowledgeList> searchKnowledge(
            @Path("organizationId") String organizationId,
            @Body KnowledgeListParams params);

    @GET("/v1/organizations/{organizationId}/knowledge/{id}")
    Single<KnowledgeEntry> getKnowledge(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/knowledge")
    Single<KnowledgeEntry> createKnowledge(
            @Path("organizationId") String organizationId,
            @Body KnowledgeEntryCreate request);

    @PUT("/v1/organizations/{organizationId}/knowledge/{id}")
    Single<KnowledgeEntry> updateKnowledge(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body KnowledgeEntryUpdate request);

    @DELETE("/v1/organizations/{organizationId}/knowledge/{id}")
    Completable deleteKnowledge(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @GET("/v1/organizations/{organizationId}/knowledge-sources")
    Single<KnowledgeSourceList> listKnowledgeSources(
            @Path("organizationId") String organizationId,
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("page") int page,
            @Query("limit") int limit);

    @GET("/v1/organizations/{organizationId}/knowledge-sources/{id}")
    Single<KnowledgeSource> getKnowledgeSource(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/knowledge-sources/{id}/sync")
    Completable syncKnowledgeSource(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @GET("/v1/organizations/{organizationId}/knowledge-sources/{id}/status")
    Single<KnowledgeSourceSyncStatus> getKnowledgeSourceStatus(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    // ── AI ────────────────────────────────────────────────────────────────

    @GET("/v1/organizations/{organizationId}/ai/complete/{conversationId}")
    Single<AiResult> draftReply(
            @Path("organizationId") String organizationId,
            @Path("conversationId") String conversationId);

    @POST("/v1/organizations/{organizationId}/ai")
    Single<AiResult> prompt(
            @Path("organizationId") String organizationId,
            @Body AiRequest request);

    @POST("/v1/organizations/{organizationId}/ai/translate")
    Single<TranslateResult> translate(
            @Path("organizationId") String organizationId,
            @Body TranslateRequest request);

    @POST("/v1/organizations/{organizationId}/ai/test")
    Single<TestResult> testAi(
            @Path("organizationId") String organizationId,
            @Body TestRequest request);

    // ── Search ────────────────────────────────────────────────────────────

    @GET("/v1/organizations/{organizationId}/search")
    Single<SearchResult> search(
            @Path("organizationId") String organizationId,
            @Query("q") String query,
            @Query("limit") Integer limit);

    // ── Templates ─────────────────────────────────────────────────────────

    @POST("/v1/organizations/{organizationId}/templates/search")
    Single<TemplateList> searchTemplates(
            @Path("organizationId") String organizationId,
            @Body TemplateListParams params);

    @GET("/v1/organizations/{organizationId}/templates/{id}")
    Single<Template> getTemplate(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/templates")
    Single<Template> createTemplate(
            @Path("organizationId") String organizationId,
            @Body TemplateCreate request);

    @PUT("/v1/organizations/{organizationId}/templates/{id}")
    Single<Template> updateTemplate(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body TemplateUpdate request);

    @DELETE("/v1/organizations/{organizationId}/templates/{id}")
    Completable deleteTemplate(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    // ── Prompts ───────────────────────────────────────────────────────────

    @POST("/v1/organizations/{organizationId}/prompts/search")
    Single<PromptList> searchPrompts(
            @Path("organizationId") String organizationId,
            @Body ListParams params);

    @GET("/v1/organizations/{organizationId}/prompts/{id}")
    Single<Prompt> getPrompt(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/prompts")
    Single<Prompt> createPrompt(
            @Path("organizationId") String organizationId,
            @Body PromptCreate request);

    @PUT("/v1/organizations/{organizationId}/prompts/{id}")
    Single<Prompt> updatePrompt(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body PromptUpdate request);

    @DELETE("/v1/organizations/{organizationId}/prompts/{id}")
    Completable deletePrompt(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    // ── Webhooks ──────────────────────────────────────────────────────────

    @POST("/v1/organizations/{organizationId}/webhooks/search")
    Single<WebhookList> searchWebhooks(
            @Path("organizationId") String organizationId,
            @Body WebhookListParams params);

    @GET("/v1/organizations/{organizationId}/webhooks/{id}")
    Single<Webhook> getWebhook(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/webhooks")
    Single<Webhook> createWebhook(
            @Path("organizationId") String organizationId,
            @Body WebhookCreate request);

    @PUT("/v1/organizations/{organizationId}/webhooks/{id}")
    Single<Webhook> updateWebhook(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body WebhookUpdate request);

    @DELETE("/v1/organizations/{organizationId}/webhooks/{id}")
    Completable deleteWebhook(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/webhooks/{id}/test")
    Single<WebhookTestResult> testWebhook(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/webhooks/{id}/rotate-secret")
    Single<WebhookSecret> rotateWebhookSecret(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    // ── Saved conversation filters ────────────────────────────────────────

    @GET("/v1/organizations/{organizationId}/filters")
    Single<ConversationFilterList> listFilters(
            @Path("organizationId") String organizationId,
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("page") int page,
            @Query("limit") int limit);

    @GET("/v1/organizations/{organizationId}/filters/{id}")
    Single<ConversationFilter> getFilter(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/filters")
    Single<ConversationFilter> createFilter(
            @Path("organizationId") String organizationId,
            @Body ConversationFilterCreate request);

    @PUT("/v1/organizations/{organizationId}/filters/{id}")
    Single<ConversationFilter> updateFilter(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body ConversationFilterUpdate request);

    @DELETE("/v1/organizations/{organizationId}/filters/{id}")
    Completable deleteFilter(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    // ── Social posts ──────────────────────────────────────────────────────

    @GET("/v1/organizations/{organizationId}/posts")
    Single<SocialPostList> listPosts(
            @Path("organizationId") String organizationId,
            @Query("status") String status,
            @Query("inboxId") String inboxId,
            @Query("from") Long from,
            @Query("to") Long to,
            @Query("search") String search,
            @Query("order") String order,
            @Query("page") int page,
            @Query("limit") int limit);

    @POST("/v1/organizations/{organizationId}/posts")
    Single<SocialPost> createPost(
            @Path("organizationId") String organizationId,
            @Body SocialPostCreate request);

    @GET("/v1/organizations/{organizationId}/posts/{id}")
    Single<SocialPost> getPost(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @PUT("/v1/organizations/{organizationId}/posts/{id}")
    Single<SocialPost> updatePost(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body SocialPostUpdate request);

    @DELETE("/v1/organizations/{organizationId}/posts/{id}")
    Completable deletePost(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Query("deleteOnPlatform") boolean deleteOnPlatform);

    @POST("/v1/organizations/{organizationId}/posts/{id}/publish")
    Single<SocialPost> publishPost(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/posts/{id}/approve")
    Single<SocialPost> approvePost(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/posts/{id}/targets/{targetId}/retry")
    Single<SocialPost> retryPostTarget(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Path("targetId") String targetId);

    @GET("/v1/organizations/{organizationId}/posts/{id}/metrics")
    Single<List<SocialPostMetric>> getPostMetrics(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @GET("/v1/organizations/{organizationId}/posts/analytics")
    Single<SocialAnalytics> getPostAnalytics(
            @Path("organizationId") String organizationId,
            @Query("from") Long from,
            @Query("to") Long to,
            @Query("inboxId") String inboxId);

    @POST("/v1/organizations/{organizationId}/posts/media")
    Single<SocialPostMediaUpload> startPostMediaUpload(
            @Path("organizationId") String organizationId,
            @Body SocialPostMediaUploadRequest request);

    @POST("/v1/organizations/{organizationId}/posts/media/{mediaId}/complete")
    Single<SocialPostMedia> completePostMediaUpload(
            @Path("organizationId") String organizationId,
            @Path("mediaId") String mediaId);

    // ── Authentication ────────────────────────────────────────────────────

    @POST("/v1/auth/login")
    Single<AuthResponse> login(
            @Body LoginRequest request);

    @POST("/v1/auth/signup")
    Single<AuthResponse> signup(
            @Body SignupRequest request);

    @POST("/v1/auth/refresh")
    Single<TokenResponse> refresh(
            @Body RefreshRequest request);

    @POST("/v1/auth/password-reset")
    Completable requestPasswordReset(
            @Body PasswordResetRequest request);
}
