package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A channel of a post being saved: an inbox with Publishing on, optionally with its own text
 * and platform settings (JSON).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostTargetInput {
    private String inboxId;
    private String content;
    private String settings;
}
