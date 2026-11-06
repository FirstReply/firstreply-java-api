package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One day (UTC) of the analytics series.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialAnalyticsDay {
    /** yyyy-MM-dd */
    private String day;
    private int posts;
    private Long views;
    private Long engagement;
}
