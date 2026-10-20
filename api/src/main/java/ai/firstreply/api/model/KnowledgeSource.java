package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeSource {
    private String id;
    private String organizationId;
    private String name;
    private String sourceType;
    private String baseUrl;
    private String configuration;
    private boolean syncEnabled;
    private long lastSyncTimestamp;
    private String lastSyncStatus;
    private String lastSyncError;
    private int syncIntervalMinutes;
    private int contentCount;
    private boolean activated;
    private boolean deleted;
    private long created;
    private long lastUpdate;
}
