package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The post a comment thread belongs to: published from FirstReply (source FIRSTREPLY) or
 * found on the platform (IMPORTED).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostSummary {
    private String id;
    private String source;
    private String platform;
    private String inboxId;
    private String text;
    private String permalink;
    private String imageUrl;
    private Long publishedAt;
}
