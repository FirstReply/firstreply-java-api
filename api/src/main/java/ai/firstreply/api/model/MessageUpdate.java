package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageUpdate {
    private String subject;
    private String messagePlain;
    private String messageHtml;
    private String translation;
    private String language;
    private Boolean outgoing;
    private Boolean positive;
    private Integer opened;
    private MessageContact sender;
    private List<MessageContact> to;
    private List<MessageContact> cc;
    private List<MessageContact> bcc;
    private String messageId;
    private String inReplyTo;
    private String messageReferences;
}
