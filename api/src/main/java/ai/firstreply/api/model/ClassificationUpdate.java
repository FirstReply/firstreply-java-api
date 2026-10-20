package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationUpdate {
    private String name;
    private String description;
    private int color;
    private boolean aiReply;
    private boolean aiReplyDraft;
    private boolean sentimentAnalysis;
    private boolean autoClose;
    private String defaultMemberId;
    private String templateId;
    private String promptId;
}
