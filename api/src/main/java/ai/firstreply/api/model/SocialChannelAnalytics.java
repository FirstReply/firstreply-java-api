package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One channel (inbox) in the analytics period.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialChannelAnalytics {
    private String inboxId;
    private String platform;
    private String name;
    private int posts;
    private Long views;
    private Long engagement;
    /** Latest follower count in the period */
    private Long followers;
    /** Change over the period */
    private Long followersChange;
}
