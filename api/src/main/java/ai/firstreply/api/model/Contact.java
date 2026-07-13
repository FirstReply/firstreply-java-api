package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    private String id;
    private String organizationId;

    private String firstname;
    private String lastname;

    private String name;
    private String email;

    private String phone;
    private String imageUrl;

    private String description;

    private String whatsappPhone;
    private String messengerUserId;
    private String instagramUserId;
    private String linkedinUserId;
    private String platformMetadata;

    private List<ContactFieldValue> fields;
}
