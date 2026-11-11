package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One post on one channel, with its publish state and latest metrics. A metric the
 * platform does not offer is null.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostTarget {
    private String id;
    private String postId;
    private String channelId;
    /**
     * Channel type: MESSENGER (Facebook Page), INSTAGRAM, LINKEDIN, THREADS, X, TIKTOK, YOUTUBE,
     * GOOGLE_BUSINESS, BLUESKY, MASTODON or TELEGRAM
     */
    private String platform;
    /** Per-platform text; null means the post's base text */
    private String content;
    /** JSON with platform settings */
    private String settings;
    /** DRAFT, PENDING_APPROVAL, SCHEDULED, QUEUED, PUBLISHING, PUBLISHED or FAILED */
    private String status;
    private String externalPostId;
    private String externalUrl;
    /** The post's image as the platform serves it, for IMPORTED posts without media of their own */
    private String externalImageUrl;
    private Long publishedAt;
    private String error;
    private int attempts;
    private Long views;
    private Long reach;
    private Long likes;
    private Long comments;
    private Long shares;
    private Long saves;
    private Long clicks;
    private Long videoViews;
    private Long metricsUpdatedAt;
}
