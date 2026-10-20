package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreate {
    private String id;
    private String subject;
    private String messagePlain;
    private String messageHtml;
    private String language;
    private String translation;
    private boolean outgoing;
    private boolean positive = true;
    private boolean draft;
    private boolean internal;
    private int opened;
    private MessageContact sender;
    private List<MessageContact> to;
    private List<MessageContact> cc;
    private List<MessageContact> bcc;
    private List<MessageAttachment> attachments;
    private String messageId;
    private String inReplyTo;
    private String messageReferences;
    private String metadata;
    private long created;
}
