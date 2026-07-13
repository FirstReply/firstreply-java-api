package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationUpdate {
    private String classificationId;
    private String assignedMemberId;
    private String contactId;
    private String subject;
    private String language;
    private Integer status;
    private Integer priority;
    private Boolean spam;
}
