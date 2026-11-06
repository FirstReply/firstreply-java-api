package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Creates a post. {@code status} DRAFT (the default) keeps it a draft; SCHEDULED schedules it
 * for {@code scheduledAt} (epoch millis), or sends it for approval when the organization
 * requires it.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostCreate {
    private String content;
    private Long scheduledAt;
    /** IANA time zone, e.g. Europe/Vienna */
    private String timezone;
    /** DRAFT or SCHEDULED */
    private String status;
    private List<SocialPostTargetInput> targets;
    /** Uploaded media in display order */
    private List<SocialPostMediaInput> media;
}
