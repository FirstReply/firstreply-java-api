package ai.firstreply.api;

import ai.firstreply.api.model.Contact;
import ai.firstreply.api.model.ContactUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ObjectMapperTest {

    private final ObjectMapper mapper = FirstReplyService.defaultObjectMapper();

    @Test
    void serializesModelPropertiesAsSnakeCase() throws Exception {
        ContactUpdate update = new ContactUpdate();
        update.setImageUrl("https://example.test/avatar.png");

        String json = mapper.writeValueAsString(update);

        assertEquals("{\"image_url\":\"https://example.test/avatar.png\"}", json);
        assertFalse(json.contains("imageUrl"));
    }

    @Test
    void deserializesSnakeCaseApiResponses() throws Exception {
        Contact contact = mapper.readValue(
                "{\"organization_id\":\"organization\",\"whatsapp_phone\":\"+4312345\"}",
                Contact.class);

        assertEquals("organization", contact.getOrganizationId());
        assertEquals("+4312345", contact.getWhatsappPhone());
    }
}
