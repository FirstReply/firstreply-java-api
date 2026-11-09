package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationCreate {
    private String classificationId;
    private String assignedMemberId;
    private String channelId;
    private String contactId;
    private String subject;
    private int priority;
    private int status;
    private Boolean spam;
    private MessageCreate message;
}
