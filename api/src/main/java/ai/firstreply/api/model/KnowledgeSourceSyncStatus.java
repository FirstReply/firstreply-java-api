package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeSourceSyncStatus {
    private String status;
    /** Local date-time as the API serializes it (a JSON array or string). */
    private Object lastSyncTime;
    /** Local date-time as the API serializes it (a JSON array or string). */
    private Object nextSyncTime;
    private String message;
    private Integer documentsProcessed;
    private Integer documentsTotal;
    private Boolean isRunning;
    private String error;
}
