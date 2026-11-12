package ai.firstreply.api;

import ai.firstreply.api.model.Channel;
import ai.firstreply.api.model.ChannelConnectRequest;
import ai.firstreply.api.model.ChannelConnectResult;
import ai.firstreply.api.model.ChannelCreate;
import ai.firstreply.api.model.ChannelOAuthStatus;
import ai.firstreply.api.model.ChannelReconnectRequest;
import ai.firstreply.api.model.ChannelUpdate;
import ai.firstreply.api.model.CommentModerationRequest;
import ai.firstreply.api.model.ContactImportResult;
import ai.firstreply.api.model.Conversation;
import ai.firstreply.api.model.ConversationList;
import ai.firstreply.api.model.ConversationStatisticsParams;
import ai.firstreply.api.model.KnowledgeSourceCreate;
import ai.firstreply.api.model.KnowledgeSourceUpdate;
import ai.firstreply.api.model.Message;
import ai.firstreply.api.model.MessageAttachment;
import ai.firstreply.api.model.MessageCreate;
import ai.firstreply.api.model.MessageListParams;
import ai.firstreply.api.model.MetricsStatistics;
import ai.firstreply.api.model.OrganizationMemberUpdate;
import ai.firstreply.api.model.PrivateReplyRequest;
import ai.firstreply.api.model.SearchResult;
import ai.firstreply.api.model.SocialAnalytics;
import ai.firstreply.api.model.SocialPost;
import ai.firstreply.api.model.SocialPostCreate;
import ai.firstreply.api.model.SocialPostList;
import ai.firstreply.api.model.SocialPostListParams;
import ai.firstreply.api.model.SocialPostMetric;
import ai.firstreply.api.model.SocialPostStatistics;
import ai.firstreply.api.model.SocialPostTarget;
import ai.firstreply.api.model.SocialPostTargetInput;
import ai.firstreply.api.model.SocialPostWriteRequest;
import ai.firstreply.api.model.SocialPostWriteResult;
import ai.firstreply.api.model.TikTokCreatorInfo;
import ai.firstreply.api.model.TranslateRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FirstReplyServiceTest {

    private MockWebServer server;
    private FirstReplyService service;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        service = new FirstReplyService("fr_test", "org1", server.url("/").toString(), Duration.ofSeconds(5));
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void callsWithoutResponseBodySucceed() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));

        service.closeConversation("c1");

        RecordedRequest request = server.takeRequest();
        assertEquals("PUT", request.getMethod());
        assertEquals("/v1/organizations/org1/conversations/c1/close", request.getPath());
        assertEquals("Bearer fr_test", request.getHeader("Authorization"));
    }

    @Test
    void callsWithoutResponseBodyReportHttpErrors() {
        server.enqueue(new MockResponse().setResponseCode(403)
                .setBody("{\"error\":\"InvalidPermissionException\",\"message\":\"This API key only has access to its own organization.\"}"));

        FirstReplyException error = assertThrows(FirstReplyException.class, () -> service.deleteConversation("c1"));

        assertEquals(403, error.getStatusCode());
        assertTrue(error.getMessage().contains("only has access to its own organization"));
    }

    @Test
    void readsConversation() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"id\":\"c1\",\"organizationId\":\"org1\",\"lastUpdate\":1760000000000,"
                + "\"hasDraft\":true,\"classificationConfidence\":0.82,\"assignedMember\":{\"id\":\"m1\",\"owner\":true},"
                + "\"lastMessage\":{\"id\":\"msg1\",\"messagePlain\":\"Hello\",\"draft\":true}}"));

        Conversation conversation = service.getConversation("c1");

        assertEquals("org1", conversation.getOrganizationId());
        assertEquals(1760000000000L, conversation.getLastUpdate());
        assertTrue(conversation.isHasDraft());
        assertEquals(0.82, conversation.getClassificationConfidence());
        assertTrue(conversation.getAssignedMember().isOwner());
        assertEquals("Hello", conversation.getLastMessage().getMessagePlain());
        assertTrue(conversation.getLastMessage().isDraft());
        assertEquals("/v1/organizations/org1/conversations/c1", server.takeRequest().getPath());
    }

    @Test
    void storesReplyAsDraft() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"id\":\"msg2\",\"draft\":true}"));
        MessageCreate reply = new MessageCreate();
        reply.setMessagePlain("Thanks, we are on it.");
        reply.setDraft(true);

        service.replyMessage("msg1", reply);

        RecordedRequest request = server.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/v1/organizations/org1/messages/msg1/reply", request.getPath());
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("\"messagePlain\":\"Thanks, we are on it.\""), body);
        assertTrue(body.contains("\"draft\":true"), body);
        assertFalse(body.contains("message_plain"), body);
    }

    @Test
    void sendsOnlyTheMemberFieldsThatWereSet() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"id\":\"m1\",\"permission\":2}"));
        OrganizationMemberUpdate update = new OrganizationMemberUpdate();
        update.setPermission(2);

        service.updateMember("m1", update);

        RecordedRequest request = server.takeRequest();
        assertEquals("PUT", request.getMethod());
        assertEquals("/v1/organizations/org1/members/m1", request.getPath());
        assertEquals("{\"permission\":2}", request.getBody().readUtf8());
    }

    @Test
    void passesQueryParameters() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"conversation\":{\"id\":\"c1\"},\"messages\":[]}"));
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse().setBody("{\"query\":\"refund\",\"conversationCount\":4}"));

        service.getConversationThread("c1", true);
        service.deleteClassification("cl1", "cl2");
        SearchResult result = service.search("refund", null);

        assertEquals("/v1/organizations/org1/conversations/c1/thread?markRead=true", server.takeRequest().getPath());
        RecordedRequest delete = server.takeRequest();
        assertEquals("DELETE", delete.getMethod());
        assertEquals("/v1/organizations/org1/classifications/cl1?reassignTo=cl2", delete.getPath());
        assertEquals("/v1/organizations/org1/search?q=refund", server.takeRequest().getPath());
        assertEquals(4, result.getConversationCount());
    }

    @Test
    void readsMetrics() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"overview\":{\"total\":10,\"closingRate\":0.5,\"firstReplyP50Ms\":60000},"
                + "\"closeReasons\":[{\"reason\":\"ai\",\"total\":3}]}"));

        MetricsStatistics metrics = service.getMetricsStatistics(new ConversationStatisticsParams("2026-09-01", "2026-09-30"));

        assertEquals(10, metrics.getOverview().getTotal());
        assertEquals(60000.0, metrics.getOverview().getFirstReplyP50Ms());
        assertEquals("ai", metrics.getCloseReasons().get(0).getReason());
        RecordedRequest request = server.takeRequest();
        assertEquals("/v1/organizations/org1/metrics/statistic", request.getPath());
        assertEquals("{\"startDate\":\"2026-09-01\",\"endDate\":\"2026-09-30\"}", request.getBody().readUtf8());
    }

    @Test
    void listsPostsWithTheFiltersThatWereSet() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"items\":[{\"id\":\"p1\",\"status\":\"SCHEDULED\","
                + "\"targets\":[{\"id\":\"t1\",\"channelId\":\"i1\",\"platform\":\"LINKEDIN\",\"likes\":12,\"views\":null}]}],"
                + "\"params\":{\"count\":1,\"page\":1,\"limit\":20}}"));
        SocialPostListParams params = new SocialPostListParams();
        params.setStatus("SCHEDULED,PENDING_APPROVAL");
        params.setFrom(1762000000000L);
        params.setOrder("asc");
        params.setPage(1);
        params.setLimit(20);

        SocialPostList posts = service.listPosts(params);

        assertEquals("/v1/organizations/org1/posts?status=SCHEDULED%2CPENDING_APPROVAL&from=1762000000000&order=asc&page=1&limit=20",
                server.takeRequest().getPath());
        SocialPostTarget target = posts.getItems().get(0).getTargets().get(0);
        assertEquals("LINKEDIN", target.getPlatform());
        assertEquals(12L, target.getLikes());
        assertNull(target.getViews());
    }

    @Test
    void schedulesPost() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"id\":\"p1\",\"status\":\"PENDING_APPROVAL\",\"scheduledAt\":1762765200000}"));
        SocialPostCreate post = new SocialPostCreate();
        post.setContent("Our new feature is live.");
        post.setStatus("SCHEDULED");
        post.setScheduledAt(1762765200000L);
        post.setTimezone("Europe/Vienna");
        post.setTargets(List.of(new SocialPostTargetInput("i1", null, null)));

        SocialPost created = service.createPost(post);

        RecordedRequest request = server.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/v1/organizations/org1/posts", request.getPath());
        assertEquals("{\"content\":\"Our new feature is live.\",\"scheduledAt\":1762765200000,\"timezone\":\"Europe/Vienna\","
                + "\"status\":\"SCHEDULED\",\"targets\":[{\"channelId\":\"i1\"}]}", request.getBody().readUtf8());
        assertEquals("PENDING_APPROVAL", created.getStatus());
    }

    @Test
    void publishesDeletesAndRetriesPosts() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"id\":\"p1\",\"status\":\"PUBLISHING\"}"));
        server.enqueue(new MockResponse().setBody("{\"id\":\"p1\",\"status\":\"PUBLISHING\"}"));
        server.enqueue(new MockResponse().setResponseCode(200));

        service.publishPost("p1");
        service.retryPostTarget("p1", "t1");
        service.deletePost("p1", true);

        RecordedRequest publish = server.takeRequest();
        assertEquals("POST", publish.getMethod());
        assertEquals("/v1/organizations/org1/posts/p1/publish", publish.getPath());
        assertEquals("/v1/organizations/org1/posts/p1/targets/t1/retry", server.takeRequest().getPath());
        RecordedRequest delete = server.takeRequest();
        assertEquals("DELETE", delete.getMethod());
        assertEquals("/v1/organizations/org1/posts/p1?deleteOnPlatform=true", delete.getPath());
    }

    @Test
    void readsPostAnalytics() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"from\":1759300000000,\"to\":1761900000000,\"posts\":3,\"views\":900,"
                + "\"saves\":null,\"engagement\":42,\"series\":[{\"day\":\"2026-10-01\",\"posts\":1,\"views\":300}],"
                + "\"channels\":[{\"channelId\":\"i1\",\"platform\":\"INSTAGRAM\",\"followers\":1200,\"followersChange\":35}]}"));

        SocialAnalytics analytics = service.getPostAnalytics(1759300000000L, 1761900000000L, null);

        assertEquals("/v1/organizations/org1/posts/analytics?from=1759300000000&to=1761900000000", server.takeRequest().getPath());
        assertEquals(3, analytics.getPosts());
        assertEquals(42L, analytics.getEngagement());
        assertNull(analytics.getSaves());
        assertEquals("2026-10-01", analytics.getSeries().get(0).getDay());
        assertEquals(35L, analytics.getChannels().get(0).getFollowersChange());
    }

    @Test
    void readsPostMetricSnapshots() throws Exception {
        server.enqueue(new MockResponse().setBody("[{\"targetId\":\"t1\",\"capturedAt\":1762000000000,\"likes\":5},"
                + "{\"targetId\":\"t1\",\"capturedAt\":1762086400000,\"likes\":9}]"));

        List<SocialPostMetric> metrics = service.getPostMetrics("p1");

        assertEquals("/v1/organizations/org1/posts/p1/metrics", server.takeRequest().getPath());
        assertEquals(2, metrics.size());
        assertEquals(9L, metrics.get(1).getLikes());
    }

    @Test
    void writesPostWithAi() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"text\":\"New feature, live today.\",\"tooLong\":true}"));
        SocialPostWriteRequest request = new SocialPostWriteRequest();
        request.setAction("SHORTEN");
        request.setText("Our new feature is live today, try it out.");
        request.setPlatform("X");
        request.setMaxLength(20);

        SocialPostWriteResult result = service.writePost(request);

        RecordedRequest recorded = server.takeRequest();
        assertEquals("POST", recorded.getMethod());
        assertEquals("/v1/organizations/org1/posts/ai", recorded.getPath());
        assertEquals("{\"action\":\"SHORTEN\",\"text\":\"Our new feature is live today, try it out.\",\"platform\":\"X\","
                + "\"maxLength\":20}", recorded.getBody().readUtf8());
        assertEquals("New feature, live today.", result.getText());
        assertTrue(result.isTooLong());
    }

    @Test
    void listsCommentThreadsOfPost() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"items\":[{\"id\":\"c1\",\"organizationId\":\"org1\"}],"
                + "\"params\":{\"count\":1,\"page\":1,\"limit\":20}}"));

        ConversationList threads = service.listPostConversations("p1", 1, 20);

        assertEquals("/v1/organizations/org1/posts/p1/conversations?page=1&limit=20", server.takeRequest().getPath());
        assertEquals("c1", threads.getItems().get(0).getId());
    }

    @Test
    void moderatesAndAnswersComments() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"id\":\"m1\"}"));
        server.enqueue(new MockResponse().setBody("{\"id\":\"m1\"}"));

        Message hidden = service.moderateComment("c1", "m1", new CommentModerationRequest("HIDE"));
        service.replyToCommentPrivately("c1", new PrivateReplyRequest("We sent you the details.", "m1"));

        RecordedRequest moderation = server.takeRequest();
        assertEquals("POST", moderation.getMethod());
        assertEquals("/v1/organizations/org1/conversations/c1/messages/m1/moderation", moderation.getPath());
        assertEquals("{\"action\":\"HIDE\"}", moderation.getBody().readUtf8());
        assertEquals("m1", hidden.getId());
        RecordedRequest privateReply = server.takeRequest();
        assertEquals("POST", privateReply.getMethod());
        assertEquals("/v1/organizations/org1/conversations/c1/private-reply", privateReply.getPath());
        assertEquals("{\"text\":\"We sent you the details.\",\"messageId\":\"m1\"}", privateReply.getBody().readUtf8());
    }

    @Test
    void createsChannelWithTheFieldsOfItsType() throws Exception {
        server.enqueue(new MockResponse().setBody(
                "{\"id\":\"ch1\",\"type\":\"BLUESKY\",\"blueskyHandle\":\"example.bsky.social\",\"blueskyDid\":\"did:plc:abc\"}"));

        ChannelCreate create = new ChannelCreate();
        create.setType("BLUESKY");
        create.setName("Bluesky");
        create.setBlueskyHandle("example.bsky.social");
        create.setBlueskyAppPassword("abcd-efgh-ijkl-mnop");
        Channel channel = service.createChannel(create);

        RecordedRequest request = server.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/v1/organizations/org1/channels", request.getPath());
        assertEquals("{\"type\":\"BLUESKY\",\"name\":\"Bluesky\",\"blueskyHandle\":\"example.bsky.social\","
                + "\"blueskyAppPassword\":\"abcd-efgh-ijkl-mnop\"}", request.getBody().readUtf8());
        assertEquals("did:plc:abc", channel.getBlueskyDid());
    }

    @Test
    void updatesChannelOnTopOfTheStoredOne() throws Exception {
        server.enqueue(new MockResponse().setBody(
                "{\"id\":\"ch1\",\"type\":\"MASTODON\",\"name\":\"Mastodon\",\"activated\":true,"
                        + "\"mastodonAccount\":\"team@mastodon.social\",\"publishingEnabled\":false}"));
        server.enqueue(new MockResponse().setBody("{\"id\":\"ch1\",\"publishingEnabled\":true}"));

        ChannelUpdate update = new ChannelUpdate();
        update.setPublishingEnabled(true);
        Channel channel = service.updateChannel("ch1", update);

        assertEquals("GET", server.takeRequest().getMethod());
        RecordedRequest put = server.takeRequest();
        assertEquals("PUT", put.getMethod());
        assertEquals("/v1/organizations/org1/channels/ch1", put.getPath());
        String body = put.getBody().readUtf8();
        assertTrue(body.contains("\"publishingEnabled\":true"), body);
        assertTrue(body.contains("\"type\":\"MASTODON\""), body);
        assertTrue(body.contains("\"mastodonAccount\":\"team@mastodon.social\""), body);
        assertTrue(body.contains("\"name\":\"Mastodon\""), body);
        assertTrue(body.contains("\"activated\":true"), body);
        assertTrue(channel.getPublishingEnabled());
    }

    @Test
    void startsChannelLogins() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"authUrl\":\"https://mastodon.social/oauth/authorize?x=1\",\"state\":\"s1\"}"));
        server.enqueue(new MockResponse().setBody("{\"authUrl\":\"https://www.facebook.com/dialog/oauth?x=1\"}"));

        ChannelConnectRequest connect = new ChannelConnectRequest();
        connect.setPlatform("MASTODON");
        connect.setChannelName("Mastodon");
        connect.setServer("mastodon.social");
        ChannelConnectResult link = service.connectChannel(connect);
        ChannelReconnectRequest reconnect = new ChannelReconnectRequest();
        reconnect.setCapabilities(List.of("PUBLISHING"));
        service.reconnectChannel("ch1", reconnect);

        RecordedRequest init = server.takeRequest();
        assertEquals("/v1/organizations/org1/channels/oauth/init", init.getPath());
        assertEquals("{\"platform\":\"MASTODON\",\"channelName\":\"Mastodon\",\"server\":\"mastodon.social\"}", init.getBody().readUtf8());
        assertEquals("https://mastodon.social/oauth/authorize?x=1", link.getAuthUrl());
        RecordedRequest again = server.takeRequest();
        assertEquals("POST", again.getMethod());
        assertEquals("/v1/organizations/org1/channels/ch1/oauth/reconnect", again.getPath());
        assertEquals("{\"capabilities\":[\"PUBLISHING\"]}", again.getBody().readUtf8());
    }

    @Test
    void readsPlayStoreServiceAccountAndTikTokCreatorInfo() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"email\":\"fr-org1@example.iam.gserviceaccount.com\"}"));
        server.enqueue(new MockResponse().setBody("{\"privacyLevelOptions\":[\"SELF_ONLY\"],\"canPost\":true,\"audited\":false}"));

        assertEquals("fr-org1@example.iam.gserviceaccount.com", service.getPlayStoreServiceAccount().getEmail());
        TikTokCreatorInfo info = service.getTikTokCreatorInfo("ch1");

        RecordedRequest account = server.takeRequest();
        assertEquals("POST", account.getMethod());
        assertEquals("/v1/organizations/org1/channels/playstore/service-account", account.getPath());
        assertEquals("/v1/organizations/org1/channels/ch1/tiktok/creator-info", server.takeRequest().getPath());
        assertEquals(List.of("SELF_ONLY"), info.getPrivacyLevelOptions());
        assertTrue(info.isCanPost());
        assertFalse(info.isAudited());
    }

    @Test
    void translatesWithinALimit() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"translation\":\"Danke\",\"translated\":true}"));
        server.enqueue(new MockResponse().setBody("{\"translation\":\"Danke\",\"translated\":true}"));

        service.translate(new TranslateRequest("de", "Thanks"));
        service.translate(new TranslateRequest("de", "Thanks", 300));

        assertEquals("{\"language\":\"de\",\"text\":\"Thanks\"}", server.takeRequest().getBody().readUtf8());
        assertEquals("{\"language\":\"de\",\"text\":\"Thanks\",\"maxLength\":300}", server.takeRequest().getBody().readUtf8());
    }

    @Test
    void createsUpdatesAndDeletesKnowledgeSources() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"id\":\"s1\",\"sourceType\":\"WEBSITE\",\"syncEnabled\":true}"));
        server.enqueue(new MockResponse().setBody("{\"id\":\"s1\",\"name\":\"Docs\",\"syncEnabled\":false}"));
        server.enqueue(new MockResponse().setResponseCode(200));

        KnowledgeSourceCreate create = new KnowledgeSourceCreate();
        create.setName("Help center");
        create.setSourceType("WEBSITE");
        create.setConfiguration("{\"baseUrl\":\"https://example.com\"}");
        assertTrue(service.createKnowledgeSource(create).isSyncEnabled());
        KnowledgeSourceUpdate update = new KnowledgeSourceUpdate();
        update.setSyncEnabled(false);
        assertEquals("Docs", service.updateKnowledgeSource("s1", update).getName());
        service.deleteKnowledgeSource("s1");

        RecordedRequest created = server.takeRequest();
        assertEquals("POST", created.getMethod());
        assertEquals("/v1/organizations/org1/knowledge-sources", created.getPath());
        // Unset fields are left out, so the API's defaults (sync on, every 60 minutes) apply
        assertEquals("{\"name\":\"Help center\",\"sourceType\":\"WEBSITE\",\"configuration\":\"{\\\"baseUrl\\\":\\\"https://example.com\\\"}\"}",
                created.getBody().readUtf8());
        RecordedRequest updated = server.takeRequest();
        assertEquals("PUT", updated.getMethod());
        assertEquals("/v1/organizations/org1/knowledge-sources/s1", updated.getPath());
        assertEquals("{\"syncEnabled\":false}", updated.getBody().readUtf8());
        RecordedRequest deleted = server.takeRequest();
        assertEquals("DELETE", deleted.getMethod());
        assertEquals("/v1/organizations/org1/knowledge-sources/s1", deleted.getPath());
    }

    @Test
    void readsPostStatistics() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"drafts\":2,\"pendingApproval\":1,\"scheduled\":4,\"published\":9,\"failed\":0,"
                + "\"nextScheduledAt\":1762000000000}"));
        server.enqueue(new MockResponse().setBody("{\"drafts\":0,\"nextScheduledAt\":null}"));

        SocialPostStatistics statistics = service.getPostStatistics(null);
        assertNull(service.getPostStatistics("ch1").getNextScheduledAt());

        assertEquals("/v1/organizations/org1/posts/statistics", server.takeRequest().getPath());
        assertEquals("/v1/organizations/org1/posts/statistics?channelId=ch1", server.takeRequest().getPath());
        assertEquals(4, statistics.getScheduled());
        assertEquals(1762000000000L, statistics.getNextScheduledAt());
    }

    @Test
    void uploadsAttachment(@TempDir Path directory) throws Exception {
        server.enqueue(new MockResponse().setBody("{\"name\":\"invoice.pdf\",\"size\":7,\"mime\":\"application/pdf\","
                + "\"file\":\"20261112100000_ab12_invoice.pdf\"}"));
        Path file = Files.write(directory.resolve("invoice.pdf"), "PDFDATA".getBytes(StandardCharsets.UTF_8));

        MessageAttachment attachment = service.uploadAttachment(file.toFile(), "application/pdf");

        RecordedRequest upload = server.takeRequest();
        assertEquals("POST", upload.getMethod());
        assertEquals("/v1/organizations/org1/attachment", upload.getPath());
        assertTrue(upload.getHeader("Content-Type").startsWith("multipart/form-data; boundary="));
        String body = upload.getBody().readUtf8();
        assertTrue(body.contains("name=\"file\"; filename=\"invoice.pdf\""), body);
        assertTrue(body.contains("Content-Type: application/pdf"), body);
        assertTrue(body.contains("PDFDATA"), body);
        assertTrue(body.contains("name=\"fileSize\""), body);
        assertEquals("20261112100000_ab12_invoice.pdf", attachment.getFile());
        assertEquals(7, attachment.getSize());
    }

    @Test
    void importsContactsAndExportsMessagesAsCsv() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"created\":1,\"updated\":0,\"skipped\":1,\"errorCount\":1,"
                + "\"errors\":[{\"line\":3,\"message\":\"Invalid email\"}]}"));
        server.enqueue(new MockResponse().setHeader("Content-Type", "text/csv").setBody("id,subject\nm1,Hello\n"));

        ContactImportResult result = service.importContacts("email,name\na@example.com,Ada\n", true);
        MessageListParams params = new MessageListParams();
        params.setConversationId("c1");
        String csv = service.exportMessages(params);

        RecordedRequest imported = server.takeRequest();
        assertEquals("/v1/organizations/org1/contacts/import?updateExisting=true", imported.getPath());
        assertEquals("text/csv; charset=utf-8", imported.getHeader("Content-Type"));
        assertEquals("email,name\na@example.com,Ada\n", imported.getBody().readUtf8());
        assertEquals(1, result.getCreated());
        assertEquals(3, result.getErrors().get(0).getLine());
        RecordedRequest exported = server.takeRequest();
        assertEquals("POST", exported.getMethod());
        assertEquals("/v1/organizations/org1/messages/export", exported.getPath());
        assertTrue(exported.getBody().readUtf8().contains("\"conversationId\":\"c1\""));
        assertEquals("id,subject\nm1,Hello\n", csv);
    }

    @Test
    void readsAndDisconnectsChannelLoginAndDeletesChannel() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"connected\":true,\"status\":\"CONNECTED\",\"platform\":\"LINKEDIN\","
                + "\"requiresReauth\":false,\"accountName\":\"Example\"}"));
        server.enqueue(new MockResponse().setBody("{\"success\": true}"));
        server.enqueue(new MockResponse().setResponseCode(200));

        ChannelOAuthStatus status = service.getChannelOAuthStatus("ch1");
        service.disconnectChannelOAuth("ch1", false);
        service.deleteChannel("ch1");

        assertEquals("/v1/organizations/org1/channels/ch1/oauth/status", server.takeRequest().getPath());
        assertTrue(status.isConnected());
        assertEquals("CONNECTED", status.getStatus());
        assertNull(status.getTokenExpiryDate());
        RecordedRequest disconnect = server.takeRequest();
        assertEquals("POST", disconnect.getMethod());
        assertEquals("/v1/organizations/org1/channels/ch1/oauth/disconnect?revokeToken=false", disconnect.getPath());
        RecordedRequest delete = server.takeRequest();
        assertEquals("DELETE", delete.getMethod());
        assertEquals("/v1/organizations/org1/channels/ch1", delete.getPath());
    }
}
