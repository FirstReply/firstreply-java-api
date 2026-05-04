package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classification {
    private String id;
    private Long lastUpdate;
    private Long created;

    private String organizationId;
    private String name;
    private String description;
    private int color;

    private boolean aiReply;
    private boolean sentimentAnalysis;
    private boolean autoClose;

    private String defaultMemberId;
    private String promptId;
    private String templateId;
}
