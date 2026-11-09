package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A social post with its targets (one per channel, i.e. channel) and media.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPost {
    private String id;
    private String organizationId;
    private String authorId;
    /** DRAFT, PENDING_APPROVAL, SCHEDULED, PUBLISHING, PUBLISHED, PARTIALLY_FAILED or FAILED */
    private String status;
    /** Base text; targets may override it */
    private String content;
    /** Epoch millis; null for drafts without a planned time */
    private Long scheduledAt;
    /** IANA time zone the author picked the time in */
    private String timezone;
    /** FIRSTREPLY or IMPORTED (made in the platform's own app) */
    private String source;
    private String approvedBy;
    private Long approvedAt;
    private Long publishedAt;
    private Long created;
    private Long lastUpdate;
    private List<SocialPostTarget> targets;
    private List<SocialPostMedia> media;
}
