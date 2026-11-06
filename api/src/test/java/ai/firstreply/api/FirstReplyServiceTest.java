package ai.firstreply.api;

import ai.firstreply.api.model.CommentModerationRequest;
import ai.firstreply.api.model.Conversation;
import ai.firstreply.api.model.ConversationList;
import ai.firstreply.api.model.ConversationStatisticsParams;
import ai.firstreply.api.model.Message;
import ai.firstreply.api.model.MessageCreate;
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
import ai.firstreply.api.model.SocialPostTarget;
import ai.firstreply.api.model.SocialPostTargetInput;
import ai.firstreply.api.model.SocialPostWriteRequest;
import ai.firstreply.api.model.SocialPostWriteResult;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
                + "\"targets\":[{\"id\":\"t1\",\"inboxId\":\"i1\",\"platform\":\"LINKEDIN\",\"likes\":12,\"views\":null}]}],"
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
                + "\"status\":\"SCHEDULED\",\"targets\":[{\"inboxId\":\"i1\"}]}", request.getBody().readUtf8());
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
                + "\"channels\":[{\"inboxId\":\"i1\",\"platform\":\"INSTAGRAM\",\"followers\":1200,\"followersChange\":35}]}"));

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
}
