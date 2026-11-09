package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filters of the post list, sent as query parameters; {@code null} filters are left out.
 * {@code from} / {@code to} (epoch millis) apply to the scheduled time, else the published
 * or created time.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostListParams {
    /** One status or several separated by commas, e.g. "SCHEDULED,PENDING_APPROVAL" */
    private String status;
    private String channelId;
    private Long from;
    private Long to;
    private String search;
    /** "asc" for calendar order (soonest first); newest first by default */
    private String order;
    private int page;
    private int limit;
}
