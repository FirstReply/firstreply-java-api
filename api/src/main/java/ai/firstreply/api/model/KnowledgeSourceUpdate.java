package ai.firstreply.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Changes to a knowledge source; fields left {@code null} keep their value.
 */
@Data
@NoArgsConstructor
public class KnowledgeSourceUpdate {
    private String name;
    private String baseUrl;
    /**
     * The settings as a JSON object. They replace the stored settings as a whole (send every
     * setting, not only the changed ones); the stored credentials of a sign-in are kept.
     */
    private String configuration;
    private Boolean syncEnabled;
    private Integer syncIntervalMinutes;
    /** The finished sign-in of a reconnect, its credentials replace the stored ones */
    private String oauthSessionId;
}
