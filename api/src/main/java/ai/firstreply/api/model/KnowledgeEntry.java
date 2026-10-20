package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeEntry {
    private String id;
    private Long lastUpdate;
    private Long created;
    private String organizationId;
    private String classificationId;
    private boolean positive;
    private String prompt;
    private String completion;
    private String sourceType;
    private String sourceUrl;
    private String sourceId;
    private Long lastSyncTimestamp;
    private String parentSourceId;
    private Classification classification;
}
