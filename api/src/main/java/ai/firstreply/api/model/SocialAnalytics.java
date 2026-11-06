package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Publishing analytics for a period: totals of the posts published in it (latest metrics of
 * each target), a daily series, the top posts and a summary per channel. A metric no channel
 * offers is null, never 0.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialAnalytics {
    private Long from;
    private Long to;
    /** Published targets (one post on one channel) in the period */
    private int posts;
    private Long views;
    private Long reach;
    private Long likes;
    private Long comments;
    private Long shares;
    private Long saves;
    private Long clicks;
    /** likes + comments + shares + saves */
    private Long engagement;
    private List<SocialAnalyticsDay> series;
    private List<SocialPost> topPosts;
    private List<SocialChannelAnalytics> channels;
    /** Inbox conversations started by comments on these posts */
    private Integer commentConversations;
    private Integer questions;
    private Integer answeredByAi;
}
