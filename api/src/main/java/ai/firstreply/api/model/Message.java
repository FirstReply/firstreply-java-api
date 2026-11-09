package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private long lastUpdate;
    private long created;
    private String organizationId;
    private String channelId;
    private String conversationId;
    private List<MessageAttachment> attachments;
    private String subject;
    private String messagePlain;
    private String messageHtml;
    private String translation;
    private String language;
    private String metadata;
    private boolean outgoing;
    private boolean positive;
    private boolean draft;
    private boolean internal;
    private int opened;
    private MessageContact sender;
    private List<MessageContact> to;
    private List<MessageContact> cc;
    private List<MessageContact> bcc;
    private String messageId;
    private String inReplyTo;
    private String messageReferences;
}
