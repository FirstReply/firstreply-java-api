package ai.firstreply.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A new knowledge source. Its first sync starts right away unless {@code syncEnabled} is false.
 */
@Data
@NoArgsConstructor
public class KnowledgeSourceCreate {
    private String name;
    /** WEBSITE, HELPDESK, GITHUB, NOTION, GITBOOK, CLICKUP or MONDAY */
    private String sourceType;
    private String baseUrl;
    /** The settings of the source type as a JSON object, e.g. {@code {"baseUrl":"https://example.com"}} */
    private String configuration;
    /** Default true */
    private Boolean syncEnabled;
    /** Default 60 */
    private Integer syncIntervalMinutes;
    /** The finished sign-in of a source type that is connected with OAuth */
    private String oauthSessionId;
}
