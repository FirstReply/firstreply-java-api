package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    private String id;
    private long lastUpdate;
    private long created;

    private Map<String, Object> classification;
    private Contact contact;
    private Map<String, Object> assignedMember;

    private String organizationId;
    private String classificationId;
    private String contactId;
    private String assignedMemberId;
    private String lastMessageId;
    private String inboxId;

    private String subject;
    private String language;

    private int status;
    private int priority;

    private boolean aiReply;
    private boolean processed;
    private boolean spam;

    private Message lastMessage;
    private int messageCounter;
}
