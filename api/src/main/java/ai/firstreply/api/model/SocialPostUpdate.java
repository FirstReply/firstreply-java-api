package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Replaces a post's text, time, targets and media until publishing starts. Fields left
 * {@code null} are cleared, not kept: start from the post as read.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostUpdate {
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
