package ai.firstreply.api;

import ai.firstreply.api.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

public interface FirstReplyApi {

    // ── Contacts ──────────────────────────────────────────────────────────

    @GET("/v1/organizations/{organizationId}/contacts/{contactId}")
    Single<Contact> getContact(
            @Path("organizationId") String organizationId,
            @Path("contactId") String contactId);

    @PUT("/v1/organizations/{organizationId}/contacts/{contactId}")
    Single<Contact> updateContact(
            @Path("organizationId") String organizationId,
            @Path("contactId") String contactId,
            @Body ContactUpdate request);

    // ── Conversations ─────────────────────────────────────────────────────

    @POST("/v1/organizations/{organizationId}/conversations")
    Single<Conversation> createConversation(
            @Path("organizationId") String organizationId,
            @Body ConversationCreate request);

    @GET("/v1/organizations/{organizationId}/conversations/{id}")
    Single<Conversation> getConversation(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @PUT("/v1/organizations/{organizationId}/conversations/{id}")
    Single<Conversation> updateConversation(
            @Path("organizationId") String organizationId,
            @Path("id") String id,
            @Body ConversationUpdate request);

    @DELETE("/v1/organizations/{organizationId}/conversations/{id}")
    Single<Void> deleteConversation(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @POST("/v1/organizations/{organizationId}/conversations/search")
    Single<ConversationList> searchConversations(
            @Path("organizationId") String organizationId,
            @Body ConversationListParams params);

    @PUT("/v1/organizations/{organizationId}/conversations/{id}/read")
    Single<Void> readConversation(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

    @PUT("/v1/organizations/{organizationId}/conversations/{id}/close")
    Single<Void> closeConversation(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

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

    @DELETE("/v1/organizations/{organizationId}/messages/{id}")
    Single<Void> deleteMessage(
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

    @POST("/v1/organizations/search")
    Single<OrganizationList> searchOrganizations(
            @Body OrganizationListParams params);

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
    Single<Void> deleteApiKey(
            @Path("organizationId") String organizationId,
            @Path("id") String id);

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
    Single<Void> requestPasswordReset(
            @Body PasswordResetRequest request);
}
