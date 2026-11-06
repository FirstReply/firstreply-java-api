package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One metrics snapshot of a target. A metric the platform does not offer is null.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostMetric {
    private String targetId;
    private long capturedAt;
    private Long views;
    private Long reach;
    private Long likes;
    private Long comments;
    private Long shares;
    private Long saves;
    private Long clicks;
    private Long videoViews;
}
