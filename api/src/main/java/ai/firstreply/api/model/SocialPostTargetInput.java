package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A channel of a post being saved: a channel with Publishing on, optionally with its own text
 * and platform settings (JSON).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostTargetInput {
    private String channelId;
    private String content;
    private String settings;
}
