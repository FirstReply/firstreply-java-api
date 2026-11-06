package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    private String id;
    private long lastUpdate;
    private long created;
    private Classification classification;
    private Contact contact;
    private OrganizationMember assignedMember;
    private String organizationId;
    private String classificationId;
    private String classificationReason;
    private Double classificationConfidence;
    private String contactId;
    private String assignedMemberId;
    private long assignedAt;
    private String assignedBy;
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
    private boolean hasDraft;
    private String searchSnippet;
    private String searchMessageId;
    /** null for private messages, COMMENT for a comment thread on a social post */
    private String kind;
    private String externalThreadId;
    private String externalPostId;
    private String socialPostId;
    /** The post of a comment thread (single loads only) */
    private SocialPostSummary socialPost;
}
