package ai.firstreply.api;

import ai.firstreply.api.model.Conversation;
import ai.firstreply.api.model.ConversationStatisticsParams;
import ai.firstreply.api.model.MessageCreate;
import ai.firstreply.api.model.MetricsStatistics;
import ai.firstreply.api.model.OrganizationMemberUpdate;
import ai.firstreply.api.model.SearchResult;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
