package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * How many posts are in each state right now: scheduled includes posts being published,
 * failed those that failed on some of their channels.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostStatistics {
    private int drafts;
    private int pendingApproval;
    private int scheduled;
    private int published;
    private int failed;
    /** When the next scheduled post goes out (epoch millis), null without one */
    private Long nextScheduledAt;
}
