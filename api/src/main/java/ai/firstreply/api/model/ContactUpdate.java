package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactUpdate {
    private String firstname;
    private String lastname;

    private String name;
    private String email;

    private String phone;
    private String imageUrl;

    private String description;

    private List<ContactFieldValueUpdate> fields;
}
