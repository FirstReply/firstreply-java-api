package ai.firstreply.api;

import ai.firstreply.api.model.Contact;
import ai.firstreply.api.model.ContactUpdate;
import ai.firstreply.api.model.Inbox;
import ai.firstreply.api.model.Organization;
import ai.firstreply.api.model.OrganizationMember;
import ai.firstreply.api.model.TestResult;
import ai.firstreply.api.model.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The API speaks camelCase JSON, like the backend DTOs. */
class ObjectMapperTest {

    private final ObjectMapper mapper = FirstReplyService.defaultObjectMapper();

    @Test
    void serializesModelPropertiesAsCamelCase() throws Exception {
        ContactUpdate update = new ContactUpdate();
        update.setImageUrl("https://example.test/avatar.png");

        String json = mapper.writeValueAsString(update);

        assertEquals("{\"imageUrl\":\"https://example.test/avatar.png\"}", json);
        assertFalse(json.contains("image_url"));
    }

    @Test
    void deserializesCamelCaseApiResponses() throws Exception {
        Contact contact = mapper.readValue(
                "{\"organizationId\":\"organization\",\"whatsappPhone\":\"+4312345\",\"conversationCount\":3}",
                Contact.class);

        assertEquals("organization", contact.getOrganizationId());
        assertEquals("+4312345", contact.getWhatsappPhone());
        assertEquals(3, contact.getConversationCount());
    }

    @Test
    void readsBooleanFlagsNamedByTheirGetters() throws Exception {
        // The backend's "isTrial" / "isOwner" fields are serialized through isTrial() / isOwner().
        Organization organization = mapper.readValue("{\"trial\":true,\"expired\":true}", Organization.class);
        OrganizationMember member = mapper.readValue("{\"owner\":true,\"managerOrAdmin\":true}", OrganizationMember.class);

        assertTrue(organization.isTrial());
        assertTrue(organization.isExpired());
        assertTrue(member.isOwner());
        assertTrue(member.isManagerOrAdmin());
    }

    @Test
    void readsRotatedRefreshToken() throws Exception {
        TokenResponse response = mapper.readValue("{\"token\":\"access\",\"refreshToken\":\"next\"}", TokenResponse.class);

        assertEquals("next", response.getRefreshToken());
    }

    @Test
    void readsTestResultClassification() throws Exception {
        TestResult result = mapper.readValue("{\"reply\":\"Hi\",\"classificationDto\":{\"name\":\"Billing\"}}", TestResult.class);

        assertEquals("Billing", result.getClassification().getName());
    }

    @Test
    void readsSocialInboxFields() throws Exception {
        Inbox inbox = mapper.readValue("{\"type\":\"TIKTOK\",\"publishingEnabled\":true,\"publishingAuthorized\":true,"
                + "\"tiktokUsername\":\"firstreply\",\"tiktokBusinessTokenExpiry\":1762000000000,\"twitterUserId\":\"42\"}",
                Inbox.class);

        assertTrue(inbox.getPublishingAuthorized());
        assertEquals("firstreply", inbox.getTiktokUsername());
        assertEquals(1762000000000L, inbox.getTiktokBusinessTokenExpiry());
        assertEquals("42", inbox.getTwitterUserId());
    }
}
