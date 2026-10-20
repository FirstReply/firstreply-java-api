package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactField {
    private String id;
    private String organizationId;
    private int fieldType;
    private String name;
    private String key;
    private String mask;
    private boolean showInConversation;
}
